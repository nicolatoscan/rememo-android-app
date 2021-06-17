package it.rememo.rememo.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import it.rememo.rememo.utils.Common;


// save the username of each users (usually user data)
public class Username extends FirebaseModel {

    public final static String KEY_NAME = "name";
    public final static String COLLECTION_NAME = "usernames";

    private String name;

    public Username(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Init(doc.getId(), (String) data.get(KEY_NAME));
    }

    public void Init(String id, String name) {
        setId(id);
        this.name = name;
    }

    @Override
    public Map<String, Object> getHashMap() {
        Map<String, Object> username = new HashMap<>();
        username.put(name, Common.getUserId());
        return username;
    }

    @Override
    public String getFirebaseCollectionName() {
        return COLLECTION_NAME;
    }

    @Override
    public String getName() {
        return name;
    }

    static public void setUsername(
            String userId, String username,
            @NonNull OnSuccessListener<? super Void> success,
            @NonNull OnFailureListener fail
    ) {
        Map<String, String> data = new HashMap<>();
        data.put(KEY_NAME, username);
        Common.db().collection(COLLECTION_NAME)
                .document(userId)
                .set(data)
                .addOnSuccessListener(success)
                .addOnFailureListener(fail);
    }

    static public void getUsernameByUserId(
            String userId,
            @NonNull OnSuccessListener<? super Username> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db().collection(COLLECTION_NAME)
                .document(userId)
                .get()
                .addOnSuccessListener((user) -> success.onSuccess(new Username(user)))
                .addOnFailureListener(fail);
    }
}
