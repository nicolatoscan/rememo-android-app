package it.rememo.rememo.models;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.utils.Common;

public class StudyStatsWord extends FirebaseModel {

    public final static String KEY_LEARN_RATE = "learnRate";
    public final static String KEY_TRAIN_RATE = "trainRate";
    public final static String KEY_COLLECTION_ID = "collectionId";
    public final static String KEY_LAST_DONE = "lastDone";
    public final static String COLLECTION_NAME = "studyStats";
    public final static String COLLECTION_WORDS_NAME = "words";

    private Double learnRate;
    private Double trainRate;
    private final String collectionId;
    private Date lastDone;

    public StudyStatsWord(DocumentSnapshot doc) {
        this.setId(doc.getId());
        this.learnRate = doc.getDouble(KEY_LEARN_RATE);
        this.trainRate = doc.getDouble(KEY_TRAIN_RATE);
        this.collectionId = doc.getString(KEY_COLLECTION_ID);
        this.lastDone = doc.getDate(KEY_LAST_DONE);

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

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_LEARN_RATE, learnRate);
        data.put(KEY_TRAIN_RATE, trainRate);
        data.put(KEY_COLLECTION_ID, collectionId);
        data.put(KEY_LAST_DONE, lastDone);
        update(data);
    }

    public double getLearnRate() { return learnRate; }
    public double getTrainRate() { return trainRate; }


    @Override
    public Map<String, Object> getHashMap() {
        Map<String, Object> res = new HashMap<>();
        res.put(KEY_LEARN_RATE, learnRate);
        res.put(KEY_TRAIN_RATE, trainRate);
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

    public void updateLearnRate(double learnRate) {
        this.learnRate = learnRate;
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(KEY_LEARN_RATE, learnRate);
        update(updateData);
    }

    public void updateTrainRate(double trainRate) {
        this.trainRate = trainRate;
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(KEY_TRAIN_RATE, trainRate);
        update(updateData);
    }

    private void update(Map<String, Object> updateData) {
        lastDone = new Date();
        updateData.put(KEY_LAST_DONE, lastDone);

        Common.db()
                .collection(COLLECTION_NAME)
                .document(Common.getUserId())
                .collection(COLLECTION_WORDS_NAME)
                .document(getId())
                .set(updateData, SetOptions.merge());
    }

    public static void getStudyStatsByCollectionsId(
            List<String> collectionIds,
            @NonNull OnSuccessListener<? super Map<String, StudyStatsWord>> success,
            @NonNull OnFailureListener fail
    ) {
        if (collectionIds.size() <= 0) {
            success.onSuccess(new HashMap<>());
            return;
        }
        Common.db()
                .collection(COLLECTION_NAME)
                .document(Common.getUserId())
                .collection(COLLECTION_WORDS_NAME)
                .whereIn(KEY_COLLECTION_ID, collectionIds)
                .get()
                .addOnSuccessListener(docs -> {
                    Map<String, StudyStatsWord> res = new HashMap<>();
                    for (DocumentSnapshot doc : docs) {
                        StudyStatsWord s = new StudyStatsWord(doc);
                        res.put(s.getId(), s);
                    }
                    success.onSuccess(res);
                })
                .addOnFailureListener(fail);
    }

}
