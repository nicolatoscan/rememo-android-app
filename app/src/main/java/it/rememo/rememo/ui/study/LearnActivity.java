package it.rememo.rememo.ui.study;



import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import it.rememo.rememo.R;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.models.StudyStatsWord;
import it.rememo.rememo.utils.Common;

// Learn tries to repeat a small number of words until the user understand them
// and only then adds some more
public class LearnActivity extends TrainLearnActivity {

    private int initialWordsSize = 0;
    private int learnedWords = 0;

    // pool of words that the user is learning
    private Map<String, CollectionWord> learningWords;

    // threshold at with a word is considered learned
    private final double threshold = 0.7;

    // All words learned
    private boolean isFinished = false;


    void onSetup() {
        StudyStatsWord.sortByLearn = false;
    }

    // Set up UI and start asking words
    void onWordLoaded() {
        initialWordsSize = words.size();
        learnedWords = 0;
        isFinished = false;
        learningWords = new HashMap<>();

        // Load words known
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

        // Load 3 unknown words
        for (int i = 0; i < 3; i++) addWord();
        finishLearnCheck();
    }

    @Override
    Map<String, CollectionWord> getNextWordPool() {
        return this.learningWords;
    }

    // Update learn status for word based on answer
    void updatePoints(String id, boolean result) {
        double points = this.currentStudyStats.getLearnRate();
        double nextPoints = this.currentStudyStats.updateLearnRate(result);

        // check if new word learned or forgotten
        if (points < threshold && nextPoints >= threshold) {
            learnedWords++;
            setProgressBar(learnedWords);
        }
        else if (points >= threshold && nextPoints < threshold) {
            learnedWords--;
            setProgressBar(learnedWords);
        }

        // Add words if less than 3 unknown words
        if (this.learningWords.size() < learnedWords + 3) {
            addWord();
        }

        finishLearnCheck();
    }

    void setProgressBar(int progress) {
        Common.toast(this, "" + progress);
        double v = (double)progress / (double)initialWordsSize;
        binding.learnProgress.setProgress((int)(v * 100));
    }

    // Check if user has finish learning
    private void finishLearnCheck() {
        if (!isFinished && learnedWords >= initialWordsSize) {
            isFinished = true;
            new AlertDialog.Builder(this)
                    .setTitle(Common.resStr(this, R.string.learn_completed_alert_title))
                    .setMessage(Common.resStr(this, R.string.learn_completed_alert_text))
                    .setPositiveButton(Common.resStr(this, R.string.form_continue), (dialog, whichButton) -> { })
                    .setNegativeButton(Common.resStr(this, R.string.form_reset), (dialog, whichButton) -> {
                        // Reset learning status
                        for (String key : this.studyStatsByWordId.keySet()) {
                            this.studyStatsByWordId.get(key).resetLearnRate();
                        }
                        finish();
                    })
                    .show();
        }
    }

    // Add new random unknown word too pool to learn
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