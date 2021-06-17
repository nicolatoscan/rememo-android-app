package it.rememo.rememo.ui.study;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.utils.Common;

public class LearnActivity extends TrainLearnActivity {

    private int initialWordsSize = 0;
    private int learnedWords = 0;
    private List<CollectionWord> learningWords;
    private final double threshold = 0.7;


    void onSetup() {
        learningWords = new ArrayList<>();
    }

    void onWordLoaded() {
        initialWordsSize = words.size();

        for (String s : this.studyStats.keySet()) {
            if (this.studyStats.get(s).getLearnRate() >= threshold) {
                this.learnedWords++;
            }
        }
        setProgressBar(learnedWords);

        for (int i = 0; i < 3; i++) addWord();
    }

    CollectionWord getNextWord() {
        return learningWords.get(new Random().nextInt(learningWords.size()));
    }

    void updatePoints(String id, boolean result) {
        double points = this.currentStudyStats.getLearnRate();
        double nextPoints = result ?  points + ((1 - points) / 2.0) : points / 2.0;
        this.currentStudyStats.updateLearnRate(nextPoints);

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
        Common.toast(this, "" + progress);
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
    }
}