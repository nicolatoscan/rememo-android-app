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

public class LearnActivity extends TrainLearnActivity {

    int initialWordsSize;
    int learnedWords;
    List<CollectionWord> learningWords;
    HashMap<String, Double> learningStatus;

    void onSetup() {
        learningWords = new ArrayList<>();
        learningStatus = new HashMap<>();
        setProgressBar(0);
    }

    void onWordLoaded() {
        initialWordsSize = words.size();
        for (int i = 0; i < 3; i++) addWord();
    }

    CollectionWord getNextWord() {
        return learningWords.get(new Random().nextInt(learningWords.size()));
    }

    void updatePoints(String id, boolean result) {
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

        if (learnedWords >= initialWordsSize) {
            finishLearn();
        }
    }

    void setProgressBar(int progress) {
        double v = (double)progress / (double)initialWordsSize;
        binding.learnProgress.setProgress((int)(v * 100));
    }

    private void finishLearn() {

    }

    private void addWord() {
        int wordsSize = words.size();
        if (wordsSize <= 0) {
            return;
        }
        int pos = new Random().nextInt(wordsSize);
        CollectionWord cw = words.get(pos);
        words.remove(pos);
        learningWords.add(cw);
        learningStatus.put(cw.getId(), 0.0);
    }
}