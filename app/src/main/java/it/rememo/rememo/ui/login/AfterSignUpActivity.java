package it.rememo.rememo.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import it.rememo.rememo.MainActivity;
import it.rememo.rememo.R;
import it.rememo.rememo.models.Username;
import it.rememo.rememo.utils.Common;

// Ask for username after signup
public class AfterSignUpActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    TextInputLayout txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_signup);
        fAuth = FirebaseAuth.getInstance();
        txtName = findViewById(R.id.afterSignUpTxtLayoutUsername);
        Button btnGoTo = findViewById(R.id.afterLoginBtnGoToRememo);
        btnGoTo.setOnClickListener(v -> onClickOpenRememo());
    }

    // Save username and open home
    public void onClickOpenRememo() {
        String name = txtName.getEditText().getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            txtName.setError(getString(R.string.login_cant_empty_name));
            return;
        }
        if (name.length() > 100) {
            txtName.setError(getString(R.string.login_too_long_name));
            return;
        }

        txtName.setError(null);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        fAuth.getCurrentUser().updateProfile(profileUpdates);

        // Also save username in Firestore so professors can see the users names
        Username.setUsername(Common.getUserId(), name,
            success -> {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            },
            ex -> Common.toast(this, getString(R.string.login_error_saving_name_retry))
        );


    }


}