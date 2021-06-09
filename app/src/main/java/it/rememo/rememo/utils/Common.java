package it.rememo.rememo.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Common {
    private static String userId = null;
    public static String getUserId() {
        if (Common.userId == null) {
            Common.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return userId;
    }

    public static void toast(Context ctx, String error) {
        Common.toast(ctx, error, Toast.LENGTH_LONG);
    }
    public static void toast(Context ctx, String error, int lenght) {
        Toast.makeText(ctx, error, lenght).show();
    }

    public static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }
}
