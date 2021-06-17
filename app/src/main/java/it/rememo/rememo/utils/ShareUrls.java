package it.rememo.rememo.utils;

import android.content.Context;
import android.content.Intent;

import it.rememo.rememo.R;

public class ShareUrls {
    public static void shareClass(Context ctx, String id, String className) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, String.format(ctx.getString(R.string.join_class_invite), className));
        i.putExtra(Intent.EXTRA_TEXT, "https://www.rememo.it/joinclass/" + id);
        ctx.startActivity(Intent.createChooser(i, "Share Class"));
    }

    public static void shareCollection(Context ctx, String id, String collectionName) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, String.format(ctx.getString(R.string.import_collection_invite), collectionName));
        i.putExtra(Intent.EXTRA_TEXT, "https://www.rememo.it/import/" + id);
        ctx.startActivity(Intent.createChooser(i, "Share Collection"));
    }
}
