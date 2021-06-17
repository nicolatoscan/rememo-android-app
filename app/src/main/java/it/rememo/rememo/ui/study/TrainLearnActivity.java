package it.rememo.rememo.ui.study;

import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityTrainBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.models.Stat;
import it.rememo.rememo.models.StudyStatsWord;
import it.rememo.rememo.utils.Common;

// Abstract class used both by learn and train since they are similar
public abstract class TrainLearnActivity extends AppCompatActivity {

    public final static String ARG_COLLECTIONS = "collections";
    MediaPlayer successSound;
    ActivityTrainBinding binding;

    CollectionWord currentWord;
    StudyStatsWord currentStudyStats;

    HashMap<String, CollectionWord> words = new HashMap<>();
    List<StudyStatsWord> orderedStudyStats = new ArrayList<StudyStatsWord>() {
        public boolean add(StudyStatsWord sw) {
            int index = Collections.binarySearch(this, sw, StudyStatsWord::compareTo);
            if (index < 0) index = ~index;
            super.add(index, sw);
            return true;
        }
    };
    Map<String, StudyStatsWord> orderedStudyStatsMap = new HashMap<>();
    Map<String, StudyStatsWord> studyStatsByWordId = new HashMap<>();

    boolean isAnswerShown = false;
    private boolean wordCompleted = false;
    private boolean studyStatsCompleted = false;

    abstract void onSetup();
    abstract void onWordLoaded();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        successSound = MediaPlayer.create(this, R.raw.success);

        Intent i = getIntent();
        List<String> collectionsIds = i.getStringArrayListExtra(TestActivity.ARG_COLLECTIONS);
        if (collectionsIds.size() < 1) {
            finish();
            return;
        }

        binding.txtAnswer.setEnabled(false);
        binding.txtAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                checkAnswer(s.toString());
            }
        });
        binding.btnNext.setOnClickListener(v -> nextWord());

        binding.txtShowAnswer.setOnClickListener(v -> {
            binding.txtShowAnswer.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
            binding.txtShowAnswer.getPaint().setMaskFilter(null);
            isAnswerShown = true;
        });

        this.binding.progressBar.setVisibility(View.VISIBLE);
        onSetup();

        CollectionWord.getAllWordsOfCollections(collectionsIds,
                words -> {
                    for (CollectionWord w : words) {
                        this.words.put(w.getId(), w);
                    }
                    if (studyStatsCompleted)
                        start();
                    wordCompleted = true;

                },
                ex -> {}
        );

        StudyStatsWord.getStudyStatsByCollectionsId(collectionsIds,
            studyStatsWords -> {
                for (StudyStatsWord sw : studyStatsWords) {
                    studyStatsByWordId.put(sw.getId(), sw);
                }
                if (wordCompleted)
                    start();
                studyStatsCompleted = true;
            },
            ex -> {}
        );
    }

    private void start() {
        this.binding.progressBar.setVisibility(View.GONE);
        onWordLoaded();
        nextWord();
    }

    abstract Map<String, CollectionWord> getNextWordPool();

    void nextWord() {
        isAnswerShown = false;
        Pair<CollectionWord, StudyStatsWord> next = chooseWord(getNextWordPool());
        this.currentWord = next.first;
        this.currentStudyStats = next.second;

        binding.txtOriginal.setText(currentWord.getOriginal());

        binding.txtShowAnswer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        float radius = (float)((double)binding.txtShowAnswer.getTextSize() / 2.0);
        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        binding.txtShowAnswer.getPaint().setMaskFilter(filter);
        binding.txtShowAnswer.setText(currentWord.getTranslated());

        binding.txtAnswer.setText("");
        binding.txtAnswer.setEnabled(true);
        binding.txtAnswer.requestFocus();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(binding.txtAnswer, InputMethodManager.SHOW_IMPLICIT);
        binding.btnNext.setVisibility(View.GONE);
        // TODO: back to original
        DrawableCompat.setTint(binding.txtAnswer.getBackground(), getColor(R.color.error_red));
    }

    private void checkAnswer(String answer) {
        if (Common.checkAnswer(currentWord.getTranslated(), answer)) {
            binding.txtAnswer.setEnabled(false);
            binding.btnNext.setVisibility(View.VISIBLE);
            DrawableCompat.setTint(binding.txtAnswer.getBackground(), getColor(R.color.rememo_dark));
            successSound.start();

            boolean result = !isAnswerShown;
            Stat.add(result, this.currentWord.getCollectionParentId());
            updatePoints(currentWord.getId(), result);
        }
    }

    Pair<CollectionWord, StudyStatsWord> chooseWord(Map<String, CollectionWord> wordPool) {
        if (orderedStudyStats.size() < wordPool.size()) {
            List<String> ids = new ArrayList<>();
            ids.addAll(wordPool.keySet());
            Collections.shuffle(ids);
            for (String wId : ids) {
                if (orderedStudyStatsMap.get(wId) == null) {
                    CollectionWord w = wordPool.get(wId);
                    StudyStatsWord ssw = studyStatsByWordId.get(w.getId());
                    if (ssw == null) {
                        ssw = new StudyStatsWord(w.getCollectionParentId(), w.getId());
                        studyStatsByWordId.put(w.getId(), ssw);
                    }
                    orderedStudyStats.add(ssw);
                    orderedStudyStatsMap.put(ssw.getId(), ssw);
                    return new Pair<>(w, ssw);
                }
            }
        }

        double range = 0.1;
        double x = Math.random();
        double size = orderedStudyStats.size();

        int top = (int) Math.ceil((1.0 + range - Math.pow(Math.max(0.0, x - range), 2.0)) * ((double)wordPool.size()));
        if (top >= size) top = (int)size - 1;

        int bottom = (int) Math.floor((1.0 - (2.0 * x * range) - Math.pow(Math.max(2 * range, x), 2)) * ((double)wordPool.size()));
        if (bottom < 0) bottom = 0;

        // Check, should not be necessary
        if (bottom > top) bottom = 0;




        StudyStatsWord lowest = orderedStudyStats.get(bottom);
        for (int i = bottom; i < top; i++) {
            StudyStatsWord s = orderedStudyStats.get(i);
            if (s.getLastDoneCorrect() == null) {
                return new Pair<>(wordPool.get(s.getId()), s);
            }

            if (lowest.getLastDoneCorrect().compareTo(s.getLastDoneCorrect()) < 0)
                lowest = s;
        }
        return new Pair<>(wordPool.get(lowest.getId()), lowest);

    }

    abstract void updatePoints(String id, boolean result);

}