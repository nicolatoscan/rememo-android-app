package it.rememo.rememo.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class StatData {
    public static final String KEY_CORRECT = "correct";
    public static final String KEY_WRONG = "wrong";
    public long correct = 0;
    public long wrong = 0;

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
            this.correct = (long) wrong;
    }

    public StatData(DocumentSnapshot doc) {
        Object correct = doc.get(KEY_CORRECT);
        Object wrong = doc.get(KEY_WRONG);
        if (correct != null)
            this.correct = (long) correct;
        if (wrong != null)
            this.correct = (long) wrong;
    }
}
