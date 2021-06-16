package it.rememo.rememo.models;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.utils.Common;

public class Stat extends FirebaseModel {
    public final static String KEY_CORRECT = "correct";
    public final static String KEY_WRONG = "wrong";
    public final static String COLLECTION_NAME = "stats";
    public final static String COLLECTION_DAYS_NAME = "days";
    public final static String COLLECTION_COLLECTION_NAME = "collections";

    private long correct = 0;
    private long wrong = 0;
    private final Map<String, StatData> days = new HashMap<>();
    private final Map<String, StatData> collectionStats = new HashMap<>();
    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");


    public Stat(DocumentSnapshot doc) {
        this.setId(doc.getId());
        Map<String, Object> data = doc.getData();
        Object correct = data.get(KEY_CORRECT);
        Object wrong = data.get(KEY_WRONG);
        if (correct != null)
            this.correct = (long) correct;
        if (wrong != null)
            this.wrong = (long) wrong;
    }



    @Override
    public String getName() { return null; }
    @Override
    public String getFirebaseCollectionName() {
        return COLLECTION_NAME;
    }
    @Override
    public Map<String, Object> getHashMap() { return null; }

    public static void fetchStats(
            @NonNull OnSuccessListener<? super Stat> success,
            @NonNull OnFailureListener fail
    ) {
        fetchStats(Common.getUserId(), success, fail);
    }

    public static void fetchStats(
            String userId,
            @NonNull OnSuccessListener<? super Stat> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db()
            .collection(COLLECTION_NAME)
            .document(userId)
            .get()
            .addOnSuccessListener(doc -> success.onSuccess(new Stat(doc)))
            .addOnFailureListener(fail);
    }

    public void fetchCollections(
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        fetchSubCollections(
                COLLECTION_COLLECTION_NAME,
                colls -> {
                    this.collectionStats.putAll(colls);
                    success.onSuccess(this.collectionStats);
                }
                , fail
        );
    }

    public void fetchDays(
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        fetchSubCollections(
            COLLECTION_DAYS_NAME,
            days -> {
                this.days.putAll(days);
                success.onSuccess(this.days);
            }
            , fail
        );
    }

    private void fetchSubCollections(
            String subCollection,
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db()
                .collection(COLLECTION_NAME)
                .document(getId())
                .collection(subCollection)
                .get()
                .addOnSuccessListener(docs -> {
                    Map<String, StatData> sub = new HashMap<>();
                    for (DocumentSnapshot day: docs) {
                        sub.put(day.getId(), new StatData(day));
                    }
                    success.onSuccess(sub);
                })
                .addOnFailureListener(fail);
    }

    public static void add(boolean result, String collectionId) {
        Map<String, Object> updateFields = new HashMap();
        updateFields.put(result ? KEY_CORRECT : KEY_WRONG, FieldValue.increment(1));

        DocumentReference doc = Common.db()
                .collection(COLLECTION_NAME)
                .document(Common.getUserId());

        doc.set(updateFields, SetOptions.merge());

        doc.collection(COLLECTION_DAYS_NAME)
                .document(formatter.format(new Date()))
                .set(updateFields, SetOptions.merge());

        doc.collection(COLLECTION_COLLECTION_NAME)
                .document(collectionId)
                .set(updateFields, SetOptions.merge());
    }



    public long getCorrect() {
        return correct;
    }

    public long getWrong() {
        return wrong;
    }

    public Map<String, StatData> getDays() {
        return days;
    }

    public Map<String, StatData> getCollectionStats() {
        return collectionStats;
    }
}