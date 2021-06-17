package it.rememo.rememo.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityEmailSentBinding;


// Activity send user to email inbox
public class EmailSentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEmailSentBinding binding = ActivityEmailSentBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_email_sent);
        String email = PreferenceManager.getDefaultSharedPreferences(EmailSentActivity.this).getString("signinEmail", "NONE");
        binding.emailSentTxtEmail.setText(email);
        binding.emailSentBtnCheckInbox.setOnClickListener(v -> openEmailInbox());
    }

    // Open email app
    public void openEmailInbox() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        startActivity(intent);
    }
}