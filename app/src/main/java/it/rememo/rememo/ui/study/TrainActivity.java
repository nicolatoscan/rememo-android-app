package it.rememo.rememo.ui.study;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityTrainBinding;
import it.rememo.rememo.models.CollectionWord;

public class TrainActivity extends AppCompatActivity {

    MediaPlayer successSound;
    ActivityTrainBinding binding;

    CollectionWord currentWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        successSound = MediaPlayer.create(this, R.raw.success);

        currentWord = new CollectionWord("Ciao", "Ciao");
        resetAnswer();

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

    }

    private void checkAnswer(String answer) {
        if (currentWord.getTranslated().trim().toLowerCase().equals(answer.trim().toLowerCase())) {
            binding.txtAnswer.setEnabled(false);
            binding.btnNext.setVisibility(View.VISIBLE);
            DrawableCompat.setTint(binding.txtAnswer.getBackground(), getColor(R.color.rememo_dark));
            playSuccessSound();
        }
    }

    private void resetAnswer() {
        binding.txtAnswer.setText("");
        binding.txtAnswer.setEnabled(true);

        binding.txtAnswer.requestFocus();
        ((InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE)).showSoftInput(binding.txtAnswer, InputMethodManager.SHOW_IMPLICIT);

        binding.btnNext.setVisibility(View.GONE);
        DrawableCompat.setTint(binding.txtAnswer.getBackground(), getColor(R.color.error_red));
    }

    private void nextWord() {
        resetAnswer();

    }

    private void playSuccessSound() {
        successSound.start();
    }
}