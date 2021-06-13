package it.rememo.rememo.ui.study;

import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityTrainBinding;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.utils.Common;

public abstract class TrainLearnActivity extends AppCompatActivity {

    public final static String ARG_COLLECTIONS = "collections";
    MediaPlayer successSound;
    ActivityTrainBinding binding;
    CollectionWord currentWord;
    List<CollectionWord> words;
    boolean isAnswerShown = false;

    abstract void onSetup();
    abstract void onWordLoaded();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        successSound = MediaPlayer.create(this, R.raw.success);

        Intent i = getIntent();
        List<String> collectionsIds = i.getStringArrayListExtra(ARG_COLLECTIONS);
        if (collectionsIds.size() != 1) {
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

        onSetup();

        CollectionWord.getWordsByCollectionId(
                collectionsIds.get(0),
                collectionWords -> {
                    if (collectionWords == null) {
                        Common.toast(this, "Couldn't load words");
                        finish();
                        return;
                    }
                    words = collectionWords;
                    binding.progressBar.setVisibility(View.GONE);
                    onWordLoaded();
                    nextWord();
                },
                ex -> {
                    Common.toast(this, "Couldn't load words");
                    finish();
                }
        );
    }

    abstract CollectionWord getNextWord();

    void nextWord() {
        isAnswerShown = false;
        this.currentWord = getNextWord();

        binding.txtOriginal.setText(currentWord.getOriginal());

        binding.txtShowAnswer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        float radius = (float)((double)binding.txtShowAnswer.getTextSize() / 2.0);
        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        binding.txtShowAnswer.getPaint().setMaskFilter(filter);
        binding.txtShowAnswer.setText(currentWord.getTranslated());

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

            updatePoints(currentWord.getId(), !isAnswerShown);
        }
    }

    abstract void updatePoints(String id, boolean result);

}