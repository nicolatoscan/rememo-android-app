package it.rememo.rememo.utils;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;

public class Alerts {
    public static AlertDialog.Builder getInputTextAlert(Context ctx, EditText textInput) {

        FrameLayout container = new FrameLayout(ctx);
        container.setPadding(40, 50, 40, 20);
        container.addView(textInput);

        return new AlertDialog.Builder(ctx).setView(container);
    }
}
