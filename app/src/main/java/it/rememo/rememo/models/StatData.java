package it.rememo.rememo.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

// Stats about wrong and correct answer given, stored in Firebase
// It uses the same format for user, collection of each user and each
// day of each user
public class StatData {
    public static final String KEY_CORRECT = "correct";
    public static final String KEY_WRONG = "wrong";
    private long correct = 0;
    private long wrong = 0;

    public StatData(long correct, long wrong) {
        this.correct = correct;
        this.wrong = wrong;
    }

    public StatData(Map<String, Object> data) {
        Object correct = data.get(KEY_CORRECT);
        Object wrong = data.get(KEY_WRONG);
        if (correct != null)
            this.correct = (long) correct;
        if (wrong != null)
            this.wrong = (long) wrong;
    }

    public StatData(DocumentSnapshot doc) {
        Object correct = doc.get(KEY_CORRECT);
        Object wrong = doc.get(KEY_WRONG);
        if (correct != null)
            this.correct = (long) correct;
        if (wrong != null)
            this.wrong = (long) wrong;
    }


    public long getCorrect() {
        return correct;
    }

    public long getWrong() {
        return wrong;
    }

    public void add(StatData toAdd) {
        this.correct += toAdd.getCorrect();
        this.wrong += toAdd.getWrong();
    }


}
