package it.rememo.rememo.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.utils.Common;
import it.rememo.rememo.utils.Counter;

public class Stat extends FirebaseModel {
    public final static String KEY_CORRECT = "correct";
    public final static String KEY_WRONG = "wrong";
    public final static String COLLECTION_NAME = "stats";
    public final static String COLLECTION_DAYS_NAME = "daysStats";
    public final static String COLLECTION_COLLECTION_NAME = "collectionsStats";

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
        fetchCollections(getId(), colls -> {
            this.collectionStats.putAll(colls);
            success.onSuccess(this.collectionStats);
        }, fail);
    }

    public void fetchDays(
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        fetchDays(getId(), days -> {
            this.days.putAll(days);
            success.onSuccess(this.days);
        }, fail);
    }

    public static void fetchCollections(
            String userId,
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        fetchSubCollections(userId, COLLECTION_COLLECTION_NAME, success, fail);
    }

    public static void fetchDays(
            String userId,
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        fetchSubCollections(userId, COLLECTION_DAYS_NAME, success, fail);
    }

    private static void fetchSubCollections(
            String id,
            String subCollection,
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db()
            .collection(COLLECTION_NAME)
            .document(id)
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
        Map<String, Object> updateFields = new HashMap<>();
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

    public static void getLastMonthRatio(
            @NonNull OnSuccessListener<? super List<Double>> success,
            @NonNull OnFailureListener fail
    ) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String lowerDate = formatter.format(cal.getTime());

        Common.db()
            .collection(COLLECTION_NAME)
            .document(Common.getUserId())
            .collection(COLLECTION_DAYS_NAME)
            .whereGreaterThan(FieldPath.documentId(), lowerDate)
            .get()
            .addOnSuccessListener(docs -> {
                List<Double> res = new ArrayList<>();
                int correct = 0;
                int total = 0;
                for (DocumentSnapshot doc : docs) {
                    StatData sd = new StatData(doc);
                    correct += sd.getCorrect();
                    total += sd.getCorrect() + sd.getWrong();
                    Log.d("AAA", sd.getCorrect() + "");
                    Log.d("CCC", sd.getWrong() + "");

                    if (total > 0) {
                        res.add( ((double) correct) / ((double) total) );
                    }

                }
                success.onSuccess(res);
            })
            .addOnFailureListener(fail);
    }

    public static void fetchCollectionsWithNames(
            String userId,
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        fetchSubCollections(userId, COLLECTION_COLLECTION_NAME, collectionsStats -> mapCollectionsNames(collectionsStats, success, fail), fail);
    }

    public static void fetchUsersStats(
            List<String> collectionsIds,
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db()
                .collectionGroup(COLLECTION_COLLECTION_NAME)
                .whereIn(FieldPath.documentId(), collectionsIds)
                .get()
                .addOnSuccessListener(docs -> {
                    Map<String, StatData> sds = new HashMap<>();
                    for (DocumentSnapshot day: docs) {
                        sds.put(day.getId(), new StatData(day));
                    }
                    mapCollectionsNames(sds, success, fail);
                });
    }

    private static void mapCollectionsNames(
            Map<String, StatData> collectionsStats,
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        ArrayList<String> collectionsIds = new ArrayList<>(collectionsStats.keySet());
        if (collectionsIds.size() <= 0) {
            success.onSuccess(new HashMap<>());
            return;
        }

        Common.db().collection(Collection.COLLECTION_NAME)
            .whereIn(FieldPath.documentId(), collectionsIds)
            .get()
            .addOnSuccessListener(colls -> {
                Map<String, StatData> res = new HashMap<>();
                for (DocumentSnapshot col : colls) {
                    res.put(
                        col.getString(Collection.KEY_NAME),
                        collectionsStats.get(col.getId())
                    );
                }
                success.onSuccess(res);
            })
            .addOnFailureListener(fail);
    }

    public static void getClassStats(
            StudentClass sClass,
            @NonNull OnSuccessListener<? super Map<String, StatData>> success,
            @NonNull OnFailureListener fail
    ) {
        sClass.getClassStudents(
                students -> {
                    final Counter todo = new Counter(students.size());
                    Map<String, StatData> res = new HashMap<>();

                    if (students.size() == 0 || sClass.getCollectionIds().size() == 0) {
                        success.onSuccess(res);
                        return;
                    }

                    for (Username student : students) {
                        Common.db()
                                .collection(COLLECTION_NAME)
                                .document(student.getId())
                                .collection(COLLECTION_COLLECTION_NAME)
                                .whereIn(FieldPath.documentId(), sClass.getCollectionIds())
                                .get()
                                .addOnSuccessListener(docs -> {
                                    StatData studentData = new StatData(0,0);
                                    for (DocumentSnapshot doc : docs) {
                                        studentData.add(new StatData(doc));
                                    }
                                    res.put(student.getName(), studentData);
                                    if (todo.decrease() <= 0) success.onSuccess(res);
                                })
                                .addOnFailureListener(ex -> {
                                    if (todo.decrease() <= 0) success.onSuccess(res);
                                });
                    }
                },
                fail
        );
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