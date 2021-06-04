package it.rememo.rememo.models;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        Init(doc.getId(), (String) data.get(KEY_NAME), (String) data.get(KEY_OWNER_ID), (HashMap<String, Boolean>) data.get(KEY_STUDENTS_ID), (HashMap<String, Boolean>) data.get(COLLECTION_NAME));
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

        FirebaseFirestore.getInstance().collection(getFirebaseCollectionName())
                .document(getId())
                .update(updateData)
                .addOnSuccessListener(success)
                .addOnFailureListener(fail);
    }

    static public void getClasses(boolean createdJoined,
            @NonNull OnSuccessListener<? super ArrayList<StudentClass>> success,
           @NonNull OnFailureListener fail
    ) {
        String whereField = createdJoined ? (KEY_OWNER_ID) : (KEY_STUDENTS_ID + "." + Common.getUserId());
        Object whereValue = createdJoined ? Common.getUserId() : true;

        FirebaseFirestore.getInstance().collection(StudentClass.COLLECTION_NAME)
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

}
