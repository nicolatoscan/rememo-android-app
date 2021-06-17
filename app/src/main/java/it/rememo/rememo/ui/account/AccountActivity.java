package it.rememo.rememo.ui.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityAccountBinding;
import it.rememo.rememo.models.Username;
import it.rememo.rememo.ui.login.LoginActivity;
import it.rememo.rememo.utils.Common;

// Activity to change username and logout
public class AccountActivity extends AppCompatActivity {
    ActivityAccountBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        binding.txtEmail.setText(user.getEmail());
        binding.txtUsername.setText(user.getDisplayName());


        binding.btnSave.setOnClickListener(v -> {
            // Save new username
            String name = binding.txtUsername.getText().toString();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
            Username.setUsername(Common.getUserId(), name,
                    success -> finish(),
                    ex -> Common.toast(this, getString(R.string.login_error_saving_name_retry))
            );

        });

        // Logout
        binding.btnLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}