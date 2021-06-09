package it.rememo.rememo.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import it.rememo.rememo.utils.Common;

public class Username extends FirebaseModel {

    public final static String KEY_NAME = "name";
    public final static String KEY_USERID = "userid";
    public final static String COLLECTION_NAME = "usernames";

    private String name;
    private String userId;

    public Username(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        Init(doc.getId(), (String) data.get(KEY_NAME), (String) data.get(KEY_USERID));
    }

    public void Init(String id, String name, String userId) {
        setId(id);
        this.name = name;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    static public void getUsernameByUserId(
            String userId,
            @NonNull OnSuccessListener<? super Username> success,
            @NonNull OnFailureListener fail
    ) {
        Common.db().collection(COLLECTION_NAME)
                .whereEqualTo(KEY_USERID, userId)
                .get()
                .addOnSuccessListener((docs) -> {
                    for (QueryDocumentSnapshot document : docs) {
                        success.onSuccess(new Username(document));
                        break;
                    }
                })
                .addOnFailureListener(fail);
    }
}
