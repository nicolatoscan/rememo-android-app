package it.rememo.rememo.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import it.rememo.rememo.MainActivity;
import it.rememo.rememo.R;
import it.rememo.rememo.models.Username;
import it.rememo.rememo.utils.Common;

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

    public void onClickOpenRememo() {
        String name = txtName.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            txtName.setError("Name can't be empty");
            return;
        }
        if (name.length() > 100) {
            txtName.setError("Name can't be this long");
            return;
        }

        txtName.setError(null);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        fAuth.getCurrentUser().updateProfile(profileUpdates);

        Username.setUsername(Common.getUserId(), name,
            success -> {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            },
            ex -> {
                Common.toast(this, "Error saving name, please try again");
            }
        );


    }


}