package it.rememo.rememo.models;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import it.rememo.rememo.utils.Common;

public class Collection {
    public final static String KEY_NAME = "name";
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_NUMBER_OF_ITEMS = "numberOfItems";
    public final static String KEY_OWNER_ID = "ownerId";
    public final static String COLLECTION_NAME = "collections";

    private String id;
    private String name;
    private String description;
    private String ownerId;
    private int numberOfItems;

    public Collection(String id, String name, String description, int numberOfItems) {
        this.Init(id, name, description, numberOfItems, null);
    }

    public Collection(String name, String description, int numberOfItems) {
        this.Init(null, name, description, numberOfItems, null);
    }

    public Collection(QueryDocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Init(doc.getId(), (String) data.get(KEY_NAME), (String) data.get(KEY_DESCRIPTION), ((Long) data.get(KEY_NUMBER_OF_ITEMS)).intValue(), (String) data.get(KEY_OWNER_ID));
    }

    public void Init(String id, String name, String description, int numberOfItems, String ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.numberOfItems = numberOfItems;
        this.ownerId = ownerId;
    }

    public Map<String, Object>  getHashMap() {
        Map<String, Object> collection = new HashMap<>();
        collection.put(Collection.KEY_OWNER_ID, Common.getUserId());

        if (name != null) collection.put(Collection.KEY_NAME, name);
        if (description != null) collection.put(Collection.KEY_DESCRIPTION, description);
        collection.put(Collection.KEY_NUMBER_OF_ITEMS, numberOfItems);
        return collection;
    }

    public void addToFirestore(
            @NonNull OnSuccessListener<? super DocumentReference> success,
            @NonNull OnFailureListener fail) {
        FirebaseFirestore.getInstance().collection(Collection.COLLECTION_NAME)
                .add(this.getHashMap())
                .addOnSuccessListener(doc -> {
                    this.id = doc.getId();
                    success.onSuccess(doc);
                })
                .addOnFailureListener(fail);

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

}
