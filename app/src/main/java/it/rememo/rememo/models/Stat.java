package it.rememo.rememo.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stat extends FirebaseModel {
    public final static String KEY_CORRECT = "correct";
    public final static String KEY_WRONG = "wrong";
    public final static String COLLECTION_NAME = "stats";

    private String userId;
    private int correct;
    private int wrong;
    private ArrayList<StatDay> days = new ArrayList<>();

    public Stat(String userId, int correct, int wrong) {
        Init(userId, correct, wrong);
    }

    public Stat(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Init(
                doc.getId(),
                (int) data.get(KEY_CORRECT),
                (int) data.get(KEY_WRONG)
        );
    }

    public void Init(String userId, int correct, int wrong) {
        this.userId = userId;
        this.correct = correct;
        this.wrong = wrong;
    }

    @Override
    public String getName() { return null; }
    @Override
    public String getFirebaseCollectionName() {
        return COLLECTION_NAME;
    }
    @Override
    public Map<String, Object> getHashMap() { return null; }

    /*public void fetchDays(@NonNull OnSuccessListener<? super List<StatDay>> success,
                          @NonNull OnFailureListener fail) {
        StatDay
    }*/
}