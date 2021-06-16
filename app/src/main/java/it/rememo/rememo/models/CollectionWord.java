package it.rememo.rememo.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.utils.Common;
import it.rememo.rememo.utils.Counter;

public class CollectionWord {
    public final static String KEY_ORIGINAL = "original";
    public final static String KEY_TRANSLATED = "translated";
    public final static String COLLECTION_NAME = "words";

    private String id;
    private String original;
    private String translated;
    private Collection collectionParent;
    private String collectionParentId;

    public CollectionWord(Collection collectionParent, String id, String original, String translated) {
        Init(collectionParent, id, original, translated);
    }
    public CollectionWord(String original, String translated) {
        Init(null, null, original, translated);
    }

    public CollectionWord(Collection collectionParent, QueryDocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Init(collectionParent, doc.getId(), (String) data.get(KEY_ORIGINAL), (String) data.get(KEY_TRANSLATED));
    }

    public CollectionWord(String collectionParentId, QueryDocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Init(null, doc.getId(), (String) data.get(KEY_ORIGINAL), (String) data.get(KEY_TRANSLATED));
        this.collectionParentId = collectionParentId;
    }

    private void Init(Collection collectionParent, String id, String original, String translated) {
        this.collectionParent = collectionParent;
        this.id = id;
        this.original = original;
        this.translated = translated;
        if (collectionParent != null)
            this.collectionParentId = collectionParent.getId();
    }

    public Map<String, Object>  getHashMap() {
        Map<String, Object> word = new HashMap<>();
        if (original != null) word.put(KEY_ORIGINAL, original);
        if (translated != null) word.put(KEY_TRANSLATED, translated);
        return word;
    }

    public void updateFirestore(
            Map<String, Object> updateData,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail) {
        FirebaseFirestore.getInstance().collection(Collection.COLLECTION_NAME)
                .document(collectionParent.getId())
                .collection(CollectionWord.COLLECTION_NAME)
                .document(id)
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(success)
                .addOnFailureListener(fail);

    }

    protected void setId(String id) {
        this.id = id;
    }

    protected void setCollectionParent(Collection collectionParent) {
        this.collectionParent = collectionParent;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
    public void setTranslated(String translated) {
        this.translated = translated;
    }

    public String getId() {
        return id;
    }

    public String getOriginal() {
        return original;
    }

    public String getTranslated() {
        return translated;
    }

    public Collection getCollectionParent() {
        return collectionParent;
    }

    public String getCollectionParentId() {
        return collectionParentId;
    }

    public static void getWordsByCollectionId(
            String collectionId,
            @NonNull OnSuccessListener<? super List<CollectionWord>> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db()
                .collection(Collection.COLLECTION_NAME)
                .document(collectionId)
                .collection(CollectionWord.COLLECTION_NAME)
                .get()
                .addOnSuccessListener(docs -> {
                    List<CollectionWord> w = new ArrayList();
                    for (QueryDocumentSnapshot document : docs) {
                        CollectionWord cw = new CollectionWord(collectionId, document);
                        w.add(cw);
                    }
                    success.onSuccess(w);
                })
                .addOnFailureListener(fail);
    }

    public static void getAllWordsOfCollections(
            List<String> ids,
            @NonNull OnSuccessListener<? super List<CollectionWord>> success,
            @NonNull OnFailureListener fail
    ) {
        final Counter todo = new Counter(ids.size());
        List<CollectionWord> allWords = new ArrayList<>();
        for (String id : ids) {
            getWordsByCollectionId(
                    id,
                    collectionWords -> {
                        allWords.addAll(collectionWords);
                        if (todo.decrease() <= 0) success.onSuccess(allWords);
                    },
                    ex -> {
                        if (todo.decrease() <= 0) success.onSuccess(allWords);
                        fail.onFailure(ex);
                    }
            );
        }
    }
}
