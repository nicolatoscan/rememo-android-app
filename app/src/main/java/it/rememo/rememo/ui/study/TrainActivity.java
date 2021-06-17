package it.rememo.rememo.ui.study;

import android.view.View;

import java.util.Map;
import java.util.Random;

import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.models.StudyStatsWord;

// Train with words from collections
public class TrainActivity extends TrainLearnActivity {


    void onSetup() {
        StudyStatsWord.sortByLearn = true;
        this.binding.learnProgress.setVisibility(View.GONE);
    }

    void onWordLoaded() { }

    @Override
    Map<String, CollectionWord> getNextWordPool() {
        return this.words;
    }

    void updatePoints(String id, boolean result) {
        this.currentStudyStats.updateTrainRate(result);
        this.orderedStudyStats.remove(this.currentStudyStats);
        this.orderedStudyStats.add(currentStudyStats);
    }

}