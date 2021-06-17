package it.rememo.rememo.models;


import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.utils.Common;

public class StudyStatsWord extends FirebaseModel implements Comparable {

    public final static String KEY_LEARN_RATE = "learnRate";
    public final static String KEY_TRAIN_RATE = "trainRate";
    public final static String KEY_COLLECTION_ID = "collectionId";
    public final static String KEY_LAST_DONE = "lastDone";
    public final static String KEY_LAST_DONE_CORRECT = "lastDoneCorrect";
    public final static String COLLECTION_NAME = "studyStats";
    public final static String COLLECTION_WORDS_NAME = "words";

    private Double learnRate;
    private Double trainRate;
    private final String collectionId;
    private Date lastDone;
    private Date lastDoneCorrect;

    static public boolean sortByLearn = true;

    public StudyStatsWord(DocumentSnapshot doc) {
        this.setId(doc.getId());
        this.learnRate = doc.getDouble(KEY_LEARN_RATE);
        this.trainRate = doc.getDouble(KEY_TRAIN_RATE);
        this.collectionId = doc.getString(KEY_COLLECTION_ID);
        this.lastDone = doc.getDate(KEY_LAST_DONE);
        this.lastDoneCorrect = doc.getDate(KEY_LAST_DONE_CORRECT);

        if (learnRate == null)
            learnRate = 0.0;
        if (trainRate == null)
            trainRate = 0.0;
    }

    public StudyStatsWord(String collectionId, String wordId) {
        this.setId(wordId);
        this.learnRate = 0.0;
        this.trainRate = 0.0;
        this.collectionId = collectionId;
        this.lastDone = null;
        this.lastDone = lastDoneCorrect;

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_LEARN_RATE, learnRate);
        data.put(KEY_TRAIN_RATE, trainRate);
        data.put(KEY_COLLECTION_ID, collectionId);
        data.put(KEY_LAST_DONE, lastDone);
        data.put(KEY_LAST_DONE_CORRECT, lastDoneCorrect);
        update(data, false);
    }

    public double getLearnRate() { return learnRate; }
    public double getTrainRate() { return trainRate; }


    @Override
    public Map<String, Object> getHashMap() {
        Map<String, Object> res = new HashMap<>();
        res.put(KEY_LEARN_RATE, learnRate);
        res.put(KEY_TRAIN_RATE, trainRate);
        res.put(KEY_LAST_DONE, lastDone);
        res.put(KEY_LAST_DONE_CORRECT, lastDoneCorrect);
        return res;
    }

    @Override
    public String getFirebaseCollectionName() {
        return COLLECTION_NAME;
    }

    @Override
    public String getName() {
        return getId();
    }

    private double updatePoint(double original, boolean result) {
        return result ?  original + ((1 - original) / 2.0) : original / 2.0;
    }

    public double resetLearnRate() {
        this.learnRate = 0.0;
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(KEY_LEARN_RATE, learnRate);
        update(updateData, false);
        return this.learnRate;
    }

    public double updateLearnRate(boolean result) {
        this.learnRate = updatePoint(this.learnRate, result);
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(KEY_LEARN_RATE, learnRate);
        update(updateData, result);

        return this.learnRate;
    }

    public double updateTrainRate(boolean result) {
        this.trainRate = updatePoint(this.trainRate, result);
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(KEY_TRAIN_RATE, trainRate);
        update(updateData, result);

        return this.trainRate;
    }

    private void update(Map<String, Object> updateData, boolean result) {
        lastDone = new Date();
        updateData.put(KEY_LAST_DONE, lastDone);

        if (result) {
            lastDoneCorrect = lastDone;
            updateData.put(KEY_LAST_DONE_CORRECT, lastDoneCorrect);
        }

        Common.db()
                .collection(COLLECTION_NAME)
                .document(Common.getUserId())
                .collection(COLLECTION_WORDS_NAME)
                .document(getId())
                .set(updateData, SetOptions.merge());
    }

    public static void getStudyStatsByCollectionsId(
            List<String> collectionIds,
            @NonNull OnSuccessListener<? super List<StudyStatsWord>> success,
            @NonNull OnFailureListener fail
    ) {
        if (collectionIds.size() <= 0) {
            success.onSuccess(new ArrayList<>());
            return;
        }
        Common.db()
                .collection(COLLECTION_NAME)
                .document(Common.getUserId())
                .collection(COLLECTION_WORDS_NAME)
                .whereIn(KEY_COLLECTION_ID, collectionIds)
                .get()
                .addOnSuccessListener(docs -> {
                    List<StudyStatsWord> res = new ArrayList<>();
                    for (DocumentSnapshot doc : docs) {
                        res.add(new StudyStatsWord(doc));
                    }
                    success.onSuccess(res);
                })
                .addOnFailureListener(fail);
    }

    @Override
    public int compareTo(Object o) {
        StudyStatsWord sw = (StudyStatsWord) o;
        if (sortByLearn) {
            return ((Double) this.getLearnRate()).compareTo((Double) sw.getLearnRate());
        } else {
            return ((Double) this.getTrainRate()).compareTo((Double) sw.getTrainRate());
        }

    }
}
