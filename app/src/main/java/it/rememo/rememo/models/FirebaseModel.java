package it.rememo.rememo.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.Map;

// abstract class to use as an interface for any collection on firebase
// All methods implemented should reflect their result on the database
public abstract class FirebaseModel implements Serializable {

    private String id;
    public String getId() {
        return id;
    }
    protected void setId(String id) {
        this.id = id;
    }

    // Get map to save on firestore
    public abstract Map<String, Object> getHashMap();
    public abstract String getFirebaseCollectionName();
    public abstract String getName();

    // save document on Firestore
    public void addToFirestore(
            @NonNull OnSuccessListener<? super DocumentReference> success,
            @NonNull OnFailureListener fail) {
        FirebaseFirestore.getInstance().collection(getFirebaseCollectionName())
                .add(getHashMap())
                .addOnSuccessListener(doc -> {
                    this.id = doc.getId();
                    success.onSuccess(doc);
                })
                .addOnFailureListener(fail);
    }

    // Update document on Firestore
    public void updateFirestore(
            Map<String, Object> updateData,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail) {
        FirebaseFirestore.getInstance().collection(getFirebaseCollectionName())
                .document(id)
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(success)
                .addOnFailureListener(fail);
    }

    // Delete from Firestore
    public void deleteFromFirestore(
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail) {
        FirebaseFirestore.getInstance().collection(getFirebaseCollectionName())
                .document(id)
                .delete()
                .addOnSuccessListener(success)
                .addOnFailureListener(fail);

    }
}
