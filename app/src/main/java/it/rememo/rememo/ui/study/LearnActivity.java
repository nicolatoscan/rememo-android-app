package it.rememo.rememo.ui.study;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.models.StudyStatsWord;
import it.rememo.rememo.utils.Common;

public class LearnActivity extends TrainLearnActivity {

    private int initialWordsSize = 0;
    private int learnedWords = 0;
    private Map<String, CollectionWord> learningWords;
    private final double threshold = 0.7;


    void onSetup() {
        StudyStatsWord.sortByLearn = false;
        learningWords = new HashMap<>();
    }

    void onWordLoaded() {
        initialWordsSize = words.size();

        for (String id : this.studyStatsByWordId.keySet()) {
            StudyStatsWord s = this.studyStatsByWordId.get(id);
            if (s.getLearnRate() >= threshold) {
                this.learnedWords++;
            }
            if (s.getLearnRate() >= 0.25) {
                CollectionWord cw = words.get(s.getId());
                words.remove(cw.getId());
                learningWords.put(cw.getId(), cw);
            }

        }
        setProgressBar(learnedWords);

        for (int i = 0; i < 3; i++) addWord();
    }

    @Override
    Map<String, CollectionWord> getNextWordPool() {
        return this.learningWords;
    }

    void updatePoints(String id, boolean result) {
        double points = this.currentStudyStats.getLearnRate();
        double nextPoints = this.currentStudyStats.updateLearnRate(result);

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
        CollectionWord cw = (CollectionWord) words.values().toArray()[pos];
        words.remove(cw.getId());
        learningWords.put(cw.getId(), cw);
    }
}