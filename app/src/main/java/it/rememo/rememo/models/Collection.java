package it.rememo.rememo.models;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.utils.Common;

// Collections of words
public class Collection extends FirebaseModel {
    // Firestore documents key
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
    private final ArrayList<CollectionWord> words = new ArrayList<>();

    public Collection(String id, String name, String description, int numberOfItems) {
        this.Init(id, name, description, numberOfItems, null);
    }

    public Collection(String name, String description, int numberOfItems) {
        this.Init(null, name, description, numberOfItems, null);
    }

    public Collection(DocumentSnapshot doc) {
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

    @Override
    public Map<String, Object>  getHashMap() {
        Map<String, Object> collection = new HashMap<>();
        collection.put(KEY_OWNER_ID, Common.getUserId());

        if (name != null) collection.put(KEY_NAME, name);
        if (description != null) collection.put(KEY_DESCRIPTION, description);
        collection.put(KEY_NUMBER_OF_ITEMS, numberOfItems);
        return collection;
    }


    // Get all words of collection from Firestore
    public void fetchWords(
            @NonNull OnSuccessListener<? super List<CollectionWord>> success,
            @NonNull OnFailureListener fail
    ) {
        CollectionWord.getWordsByCollectionId(
                getId(),
                newWords -> {
                    for (CollectionWord w : newWords) {
                        w.setCollectionParent(this);
                    }
                    words.clear();
                    words.addAll(newWords);
                    success.onSuccess(words);
                },
                fail
        );
    }

    // Add a new word to the collection
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

    // Delete word from collection
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

    // Get my collection
    public static void getMyCollections(
            @NonNull OnSuccessListener<? super List<Collection>> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db()
                .collection(Collection.COLLECTION_NAME)
                .whereEqualTo(Collection.KEY_OWNER_ID, Common.getUserId())
                .get()
                .addOnSuccessListener(docs -> {
                    ArrayList<Collection> colls = new ArrayList<>();
                    for (QueryDocumentSnapshot document : docs) {
                        colls.add(new Collection(document));
                    }
                    success.onSuccess(colls);
                })
                .addOnFailureListener(fail);

    }

    // Get a collection by Id
    public static void getCollectionById(
            String collectionId,
            @NonNull OnSuccessListener<? super Collection> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db()
                .collection(Collection.COLLECTION_NAME)
                .document(collectionId)
                .get()
                .addOnSuccessListener(doc -> {
                    success.onSuccess(new Collection(doc));
                })
                .addOnFailureListener(fail);

    }

    // clone a collection of another user
    public static void importCollection(
            Collection collection,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail
    ) {

        collection.fetchWords(
                words -> {

                    Collection newCollection =  new Collection(collection.getName(), collection.getDescription(), 0);
                    newCollection.addToFirestore(
                            savedModel -> {
                                WriteBatch batch = Common.db().batch();

                                for (CollectionWord w : words) {

                                    CollectionWord word = new CollectionWord(w.getOriginal(), w.getTranslated());

                                    DocumentReference docRef = Common.db().collection(COLLECTION_NAME)
                                            .document(newCollection.getId())
                                            .collection(CollectionWord.COLLECTION_NAME)
                                            .document();
                                    batch.set(docRef, word.getHashMap());
                                }

                                batch.commit().addOnSuccessListener(success).addOnFailureListener(fail);
                            },
                            fail
                    );

                },
                fail
        );

    }

}
