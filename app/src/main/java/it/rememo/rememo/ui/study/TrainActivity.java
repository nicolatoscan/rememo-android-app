package it.rememo.rememo.ui.study;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityTrainBinding;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.utils.Common;

public class TrainActivity extends AppCompatActivity {

    public final static String ARG_COLLECTIONS = "collections";
    MediaPlayer successSound;
    ActivityTrainBinding binding;
    CollectionWord currentWord;
    List<CollectionWord> words;
    int initialWordsSize;
    int learnedWords;
    List<CollectionWord> learningWords;
    HashMap<String, Double> learningStatus;
    boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        successSound = MediaPlayer.create(this, R.raw.success);

        learningWords = new ArrayList<>();
        learningStatus = new HashMap<>();

        Intent i = getIntent();
        List<String> collectionsIds = i.getStringArrayListExtra(ARG_COLLECTIONS);
        if (collectionsIds.size() != 1) {
            finish();
            return;
        }

        CollectionWord.getWordsByCollectionId(
                collectionsIds.get(0),
                collectionWords -> {
                    if (collectionWords == null) {
                        Common.toast(this, "Couldn't load words");
                        finish();
                        return;
                    }
                    words = collectionWords;
                    initialWordsSize = words.size();
                    start();
                },
                ex -> {
                    Common.toast(this, "Couldn't load words");
                    finish();
                }
        );

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
        setProgressBar(0);
    }

    private void start() {
        binding.progressBar.setVisibility(View.GONE);
        for (int i = 0; i < 3; i++) addWord();
        nextWord();
    }

    private void nextWord() {
        this.currentWord = learningWords.get(new Random().nextInt(learningWords.size()));

        binding.txtOriginal.setText(currentWord.getOriginal());
        binding.txtAnswer.setText("");
        binding.txtAnswer.setEnabled(true);
        binding.txtAnswer.requestFocus();
        ((InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE)).showSoftInput(binding.txtAnswer, InputMethodManager.SHOW_IMPLICIT);
        binding.btnNext.setVisibility(View.GONE);
        DrawableCompat.setTint(binding.txtAnswer.getBackground(), getColor(R.color.error_red));
    }

    private void checkAnswer(String answer) {
        if (currentWord.getTranslated().trim().toLowerCase().equals(answer.trim().toLowerCase())) {
            binding.txtAnswer.setEnabled(false);
            binding.btnNext.setVisibility(View.VISIBLE);
            DrawableCompat.setTint(binding.txtAnswer.getBackground(), getColor(R.color.rememo_dark));
            successSound.start();

            updatePoints(currentWord.getId(), true);
        }
    }

    private void updatePoints(String id, boolean result) {
        double points = this.learningStatus.get(id);
        double nextPoints = result ?  points + ((1 - points) / 2.0) : points / 2.0;
        this.learningStatus.put(id, nextPoints);

        double threshold = 0.7;

        if (points < threshold && nextPoints >= threshold) {
            learnedWords++;
            setProgressBar(learnedWords);
        }
        else if (points >= threshold && nextPoints < threshold) {
            learnedWords--;
            setProgressBar(learnedWords);
        }

        if (this.learningWords.size() < learnedWords + 3) {
            addWord();
        }
    }

    private void setProgressBar(int progress) {
        double v = (double)progress / (double)initialWordsSize;
        binding.learnProgress.setProgress((int)(v * 100));
    }

    private void addWord() {
        int wordsSize = words.size();
        if (wordsSize <= 0) {
            finished = true;
            return;
        }
        int pos = new Random().nextInt(wordsSize);
        CollectionWord cw = words.get(pos);
        words.remove(pos);
        learningWords.add(cw);
        learningStatus.put(cw.getId(), 0.0);
    }
}