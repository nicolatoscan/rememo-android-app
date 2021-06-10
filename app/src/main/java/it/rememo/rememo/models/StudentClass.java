package it.rememo.rememo.models;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.MainActivity;
import it.rememo.rememo.utils.Common;

public class StudentClass extends FirebaseModel {
    public final static String KEY_NAME = "name";
    public final static String KEY_OWNER_ID = "ownerId";
    public final static String KEY_STUDENTS_ID = "students";
    public final static String KEY_COLLECTIONS_ID = "collections";
    public final static String COLLECTION_NAME = "classes";
    @Override
    public String getFirebaseCollectionName() {
        return COLLECTION_NAME;
    }



    private String name;
    private String ownerId;
    private Map<String, Boolean> studentsIds = new HashMap<>();
    private Map<String, Boolean> collectionsIds = new HashMap<>();

    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public StudentClass(String name) {
        this.Init(null, name, null, null, null);
    }

    public StudentClass(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Init(doc.getId(), (String) data.get(KEY_NAME), (String) data.get(KEY_OWNER_ID), (HashMap<String, Boolean>) data.get(KEY_STUDENTS_ID), (HashMap<String, Boolean>) data.get(KEY_COLLECTIONS_ID));
    }

    public void Init(String id, String name, String ownerId, HashMap<String, Boolean> studentsIds, HashMap<String, Boolean> collectionsIds) {
        setId(id);
        this.name = name;
        this.ownerId = ownerId;
        this.studentsIds = studentsIds != null ? studentsIds : new HashMap<String, Boolean>();
        this.collectionsIds = collectionsIds != null ? collectionsIds : new HashMap<String, Boolean>();
    }

    public Map<String, Object>  getHashMap() {
        Map<String, Object> stClass = new HashMap<>();
        stClass.put(KEY_OWNER_ID, Common.getUserId());
        if (name != null) stClass.put(KEY_NAME, name);
        if (studentsIds != null) stClass.put(KEY_STUDENTS_ID, studentsIds);
        if (collectionsIds != null) stClass.put(KEY_COLLECTIONS_ID, collectionsIds);
        return stClass;
    }

    private List<String> getListFromHashMap(Map<String, Boolean> hash) {
        ArrayList<String> res = new ArrayList<>();
        Iterator it = hash.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Boolean> pair = (Map.Entry)it.next();
            if (pair.getValue()) {
                res.add(pair.getKey());
            }
        }
        return res;
    }

    public List<String> getCollectionIds() {
        return  this.getListFromHashMap(this.collectionsIds);
    }

    public List<String> getStudentsIds() {
        return  this.getListFromHashMap(this.studentsIds);
    }

    public void joinClass(
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail
    ) {
        toggleClass(true, success, fail);
    }

    public void leaveClass(
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail
    ) {
        toggleClass(false, success, fail);
    }

    public void toggleClass(
            boolean joinLeave,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail
    ) {
        String userId = Common.getUserId();

        Map<String, Object> studentId = new HashMap<>();
        studentId.put(userId, joinLeave);
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(KEY_STUDENTS_ID, studentId);

        this.updateFirestore(updateData, success, fail);
    }

    public void addCollections(
            List<Collection> collections,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail
    ) {
        toggleCollections(true, collections, success, fail);
    }

    public void removeCollections(
            List<Collection> collections,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail
    ) {
        toggleCollections(false, collections, success, fail);
    }

    private void toggleCollections(
            boolean addRemove,
            List<Collection> collections,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail
    ) {
        for (Collection c : collections) {
            this.collectionsIds.put(c.getId(), addRemove);
        }
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(KEY_COLLECTIONS_ID, this.collectionsIds);
        this.updateFirestore(updateData, success, fail);
    }

    public void removeStudents(
            List<Username> usernames,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail
    ) {
        for (Username u : usernames) {
            this.studentsIds.put(u.getUserId(), false);
        }
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(KEY_STUDENTS_ID, this.studentsIds);
        this.updateFirestore(updateData, success, fail);
    }

    private void updateStudentClass(
        Map<String, Object> updateData,
        @NonNull OnSuccessListener<? super Void> success,
        @NonNull OnFailureListener fail
    ) {
        FirebaseFirestore.getInstance().collection(getFirebaseCollectionName())
                .document(getId())
                .update(updateData)
                .addOnSuccessListener(success)
                .addOnFailureListener(fail);
    }

    public void getClassCollections(
        @NonNull OnSuccessListener<? super List<Collection>> success,
        @NonNull OnFailureListener fail
    ) {
        List collsIds = this.getCollectionIds();
        if (collsIds.size() > 0) {
            Common.db()
                    .collection(Collection.COLLECTION_NAME)
                    .whereIn(FieldPath.documentId(), collsIds)
                    .get()
                    .addOnSuccessListener((docs) -> {
                        ArrayList<Collection> colls = new ArrayList();
                        for (QueryDocumentSnapshot d : docs) {
                            colls.add(new Collection(d));
                        }
                        success.onSuccess(colls);
                    })
                    .addOnFailureListener(fail);
        } else {
            success.onSuccess(new ArrayList<Collection>());
        }
    }

    public void getClassStudents(
            @NonNull OnSuccessListener<? super ArrayList<Username>> success,
            @NonNull OnFailureListener fail
    ) {
        List studIds = this.getStudentsIds();
        if (studIds.size() > 0) {
            Common.db()
                    .collection(Username.COLLECTION_NAME)
                    .whereIn(Username.KEY_USERID, studIds)
                    .get()
                    .addOnSuccessListener((docs) -> {
                        ArrayList<Username> colls = new ArrayList();
                        for (QueryDocumentSnapshot d : docs) {
                            Log.d("KKKKKK", new Username(d).getName());
                            colls.add(new Username(d));
                        }
                        success.onSuccess(colls);
                    })
                    .addOnFailureListener(fail);
        } else {
            success.onSuccess(new ArrayList<Username>());
        }
    }

    static public void getClasses(boolean createdJoined,
            @NonNull OnSuccessListener<? super ArrayList<StudentClass>> success,
           @NonNull OnFailureListener fail
    ) {
        String whereField = createdJoined ? (KEY_OWNER_ID) : (KEY_STUDENTS_ID + "." + Common.getUserId());
        Object whereValue = createdJoined ? Common.getUserId() : true;

        Common.db().collection(StudentClass.COLLECTION_NAME)
            .whereEqualTo(whereField, whereValue)
            .get()
            .addOnSuccessListener(docs -> {
                ArrayList<StudentClass> updatedCollections = new ArrayList();
                for (QueryDocumentSnapshot document : docs) {
                    updatedCollections.add(new StudentClass(document));
                }
                success.onSuccess(updatedCollections);
            })
            .addOnFailureListener(fail);
    }

    static public void getClassById(
            String classId,
            @NonNull OnSuccessListener<? super StudentClass> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db().collection(StudentClass.COLLECTION_NAME)
                .document(classId)
                .get()
                .addOnSuccessListener((doc) -> {
                    success.onSuccess(new StudentClass(doc));
                })
                .addOnFailureListener(fail);
    }

}
