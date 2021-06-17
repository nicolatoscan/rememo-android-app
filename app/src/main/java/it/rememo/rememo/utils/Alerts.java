package it.rememo.rememo.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

public class Alerts {
    public static AlertDialog.Builder getInputTextAlert(Context ctx, EditText... textInputs) {

        LinearLayout container = new LinearLayout(ctx);
        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 50, 40, 20);
        for (EditText txt : textInputs) {
            txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            container.addView(txt);
        }

        return new AlertDialog.Builder(ctx).setView(container);
    }
}
