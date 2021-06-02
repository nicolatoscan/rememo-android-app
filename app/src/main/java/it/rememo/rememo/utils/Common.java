package it.rememo.rememo.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Common {
    private static String userId = null;
    public static String getUserId() {
        if (Common.userId == null) {
            Common.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return userId;
    }

    public static void showToast(Context ctx, String error) {
        Common.showToast(ctx, error, Toast.LENGTH_LONG);
    }
    public static void showToast(Context ctx, String error, int lenght) {
        Toast.makeText(ctx, error, lenght).show();
    }
}
