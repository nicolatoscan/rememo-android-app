package it.rememo.rememo.ui.study;

import android.view.View;

import java.util.Random;

import it.rememo.rememo.models.CollectionWord;

public class TrainActivity extends TrainLearnActivity {


    void onSetup() {
        this.binding.learnProgress.setVisibility(View.GONE);
    }

    void onWordLoaded() { }

    CollectionWord getNextWord() {
        return this.words.get(new Random().nextInt(this.words.size()));
    }

    void updatePoints(String id, boolean result) {
        double points = this.currentStudyStats.getTrainRate();
        points = result ?  points + ((1 - points) / 2.0) : points / 2.0;
        this.currentStudyStats.updateTrainRate(points);
    }

}