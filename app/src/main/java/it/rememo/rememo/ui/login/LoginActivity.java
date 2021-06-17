package it.rememo.rememo.ui.login;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import it.rememo.rememo.MainActivity;
import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityLoginBinding;
import it.rememo.rememo.models.Username;
import it.rememo.rememo.utils.Common;


public class LoginActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 42;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        Common.logout();

        Uri intentData = getIntent().getData();
        if (intentData != null) {
            signInWithEmailLink(intentData.toString());
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.signInWithGoogleButton.setOnClickListener(v -> signInWithGoogle());
        binding.loginBtnSendEmail.setOnClickListener(v -> onClickSignInWithEmail());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                failedLogin(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        openAfterLogin(task.getResult());
                    } else {
                        failedLogin(null);
                    }
                });
    }

    public void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onClickSignInWithEmail() {
        String email = binding.loginEmailTextField.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            binding.loginEmailTextField.setError(Common.resStr(this, R.string.login_cant_empty_mail));
            return;
        }
        binding.loginEmailTextField.setError(null);

        binding.loginProgressSendingEmail.setVisibility(View.VISIBLE);
        binding.loginBtnSendEmail.setEnabled(false);

        ActionCodeSettings acSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://rememo-cb013.web.app")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("it.rememo.rememo", true, "0")
                .build();

        fAuth.sendSignInLinkToEmail(email, acSettings).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("signinEmail", email).apply();
                binding.loginProgressSendingEmail.setVisibility(View.INVISIBLE);
                startActivity(new Intent(LoginActivity.this, EmailSentActivity.class));
            } else {
                binding.loginEmailTextField.setError(task.getException().getMessage());
                binding.loginProgressSendingEmail.setVisibility(View.GONE);
            }
            binding.loginBtnSendEmail.setEnabled(true);
        });
    }

    public void signInWithEmailLink(String emailLink) {
        String email = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("signinEmail", "NONE");
        if (email.equals("None")) {
            failedLogin(Common.resStr(this, R.string.login_failed_use_same_device));
            return;
        }
        fAuth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        openAfterLogin(task.getResult());
                    } else {
                        failedLogin(null);
                    }
                });
    }

    private void failedLogin(String message) {
        Toast.makeText(LoginActivity.this, message == null ? Common.resStr(this, R.string.login_failed_retry) : message, Toast.LENGTH_LONG).show();
    }

    private void openAfterLogin(AuthResult result) {
        FirebaseUser user = result.getUser();
        String name = result.getUser().getDisplayName();
        Username.setUsername(user.getUid(), name == null ? Common.resStr(this, R.string.login_unknown_user) : name,
                success -> {
                    Common.toast(this, String.format(Common.resStr(this, R.string.login_logged_as_STR) , (name == null ? user.getEmail() : name)));
                    startActivity(new Intent(getApplicationContext(), name == null ? AfterSignUpActivity.class : MainActivity.class));
                    finish();
                },
                ex -> Common.toast(this, Common.resStr(this, R.string.login_error))
                );
    }
}