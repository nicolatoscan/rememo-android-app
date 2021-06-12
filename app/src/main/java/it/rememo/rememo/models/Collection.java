package it.rememo.rememo.models;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.MainActivity;
import it.rememo.rememo.utils.Common;

public class Collection extends FirebaseModel {
    public final static String KEY_NAME = "name";
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_NUMBER_OF_ITEMS = "numberOfItems";
    public final static String KEY_OWNER_ID = "ownerId";
    public final static String COLLECTION_NAME = "collections";
    @Override
    public String getFirebaseCollectionName() {
        return COLLECTION_NAME;
    }

    private String name;
    private String description;
    private String ownerId;
    private int numberOfItems;
    private ArrayList<CollectionWord> words = new ArrayList<>();

    public Collection(String id, String name, String description, int numberOfItems) {
        this.Init(id, name, description, numberOfItems, null);
    }

    public Collection(String name, String description, int numberOfItems) {
        this.Init(null, name, description, numberOfItems, null);
    }

    public Collection(QueryDocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Init(
                doc.getId(),
                (String) data.get(KEY_NAME),
                (String) data.get(KEY_DESCRIPTION),
                ((Long) data.get(KEY_NUMBER_OF_ITEMS)).intValue(),
                (String) data.get(KEY_OWNER_ID)
        );
    }

    public void Init(String id, String name, String description, int numberOfItems, String ownerId) {
        this.setId(id);
        this.name = name;
        this.description = description;
        this.numberOfItems = numberOfItems;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public int getNumberOfItems() {
        return numberOfItems;
    }
    public ArrayList<CollectionWord> getWords() {
        return words;
    }

    public Map<String, Object>  getHashMap() {
        Map<String, Object> collection = new HashMap<>();
        collection.put(KEY_OWNER_ID, Common.getUserId());

        if (name != null) collection.put(KEY_NAME, name);
        if (description != null) collection.put(KEY_DESCRIPTION, description);
        collection.put(KEY_NUMBER_OF_ITEMS, numberOfItems);
        return collection;
    }

    public void fetchWords(
            @NonNull OnSuccessListener<? super List<CollectionWord>> success,
            @NonNull OnFailureListener fail) {
        CollectionWord.getWordsByCollectionId(
                getId(),
                newWords -> {
                    for (CollectionWord w : newWords) {
                        w.setCollectionParent(this);
                    }
                    words.clear();
                    words.addAll(newWords);
                },
                fail
        );
    }

    public void addWord(CollectionWord word,
            @NonNull OnSuccessListener<? super CollectionWord> success,
            @NonNull OnFailureListener fail) {
        Common.db().collection(COLLECTION_NAME)
                .document(getId())
                .collection(CollectionWord.COLLECTION_NAME)
                .add(word.getHashMap())
                .addOnSuccessListener(doc -> {
                    word.setId(doc.getId());
                    word.setCollectionParent(this);
                    words.add(word);
                    success.onSuccess(word);
                })
                .addOnFailureListener(fail);
    }

    public void deleteWord(CollectionWord word,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail) {
        if (words.remove(word)) {
            word.setCollectionParent(null);
            Common.db().collection(COLLECTION_NAME)
                    .document(getId())
                    .collection(CollectionWord.COLLECTION_NAME)
                    .document(word.getId())
                    .delete()
                    .addOnSuccessListener(success)
                    .addOnFailureListener(fail);
        }
    }

    public static void getMyCollections(
            @NonNull OnSuccessListener<? super List<Collection>> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db()
                .collection(Collection.COLLECTION_NAME)
                .whereEqualTo(Collection.KEY_OWNER_ID, Common.getUserId())
                .get()
                .addOnSuccessListener(docs -> {
                    ArrayList<Collection> colls = new ArrayList();
                    for (QueryDocumentSnapshot document : docs) {
                        colls.add(new Collection(document));
                    }
                    success.onSuccess(colls);
                })
                .addOnFailureListener(fail);

    }

}
