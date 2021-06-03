package it.rememo.rememo.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    @Override
    public String getName() {
        return name;
    }

    private String name;
    private String ownerId;
    private ArrayList<String> studentsIds = new ArrayList<>();
    private ArrayList<String> collectionsIds = new ArrayList<>();

    public StudentClass(String name) {
        this.Init(null, name, null, null, null);
    }

    public StudentClass(QueryDocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Init(doc.getId(), (String) data.get(KEY_NAME), (String) data.get(KEY_OWNER_ID), (ArrayList<String>) data.get(KEY_STUDENTS_ID), (ArrayList<String>) data.get(COLLECTION_NAME));
    }

    public void Init(String id, String name, String ownerId, ArrayList<String> studentsIds, ArrayList<String> collectionsIds) {
        setId(id);
        this.name = name;
        this.ownerId = ownerId;
        this.studentsIds = studentsIds;
        this.collectionsIds = collectionsIds;
    }

    public Map<String, Object>  getHashMap() {
        Map<String, Object> stClass = new HashMap<>();
        stClass.put(KEY_OWNER_ID, Common.getUserId());
        if (name != null) stClass.put(KEY_NAME, name);
        if (studentsIds != null) stClass.put(KEY_STUDENTS_ID, studentsIds);
        if (collectionsIds != null) stClass.put(KEY_COLLECTIONS_ID, collectionsIds);
        return stClass;
    }

}
