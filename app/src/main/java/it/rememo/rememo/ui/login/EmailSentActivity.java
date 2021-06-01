package it.rememo.rememo.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import it.rememo.rememo.R;

public class EmailSentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sent);
        String email = PreferenceManager.getDefaultSharedPreferences(EmailSentActivity.this).getString("signinEmail", "NONE");
        TextView txtEmail = findViewById(R.id.emailSentTxtEmail);
        txtEmail.setText(email);
    }

    public void openEmailInbox(View v) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        startActivity(intent);
    }
}