package it.rememo.rememo.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import it.rememo.rememo.utils.Common;

public class CollectionWord {
    public final static String KEY_ORIGINAL = "original";
    public final static String KEY_TRANSLATED = "translated";
    public final static String COLLECTION_NAME = "words";

    private String id;
    private String original;
    private String translated;
    private Collection collectionParent;

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

    private void Init(Collection collectionParent, String id, String original, String translated) {
        this.collectionParent = collectionParent;
        this.id = id;
        this.original = original;
        this.translated = translated;
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
}
