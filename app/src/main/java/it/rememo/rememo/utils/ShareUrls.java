package it.rememo.rememo.utils;

import android.content.Context;
import android.content.Intent;

public class ShareUrls {
    public static void shareClass(Context ctx, String id, String className) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Join class " + className + " on Rememo");
        i.putExtra(Intent.EXTRA_TEXT, "https://www.rememo.it/joinclass/" + id);
        ctx.startActivity(Intent.createChooser(i, "Share URL"));
    }
}
