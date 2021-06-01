package it.rememo.rememo.ui.login;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import it.rememo.rememo.MainActivity;
import it.rememo.rememo.R;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout txtEmail;
    ProgressBar progressSendingEmail;
    FirebaseAuth fAuth;
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        Uri intentData = getIntent().getData();
        if (intentData != null) {
            signInWithEmailLink(intentData.toString());
        }

        txtEmail = findViewById(R.id.loginEmailTextField);
        progressSendingEmail = findViewById(R.id.loginProgressSendingEmail);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton googleBtn = findViewById(R.id.sign_in_with_google_button);
        googleBtn.setOnClickListener(v -> signInWithGoogle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            openAfterLogin(task.getResult());
                            // Sign in success, update UI with the signed-in user's information
                        } else {
                            failedLogin(null);
                        }
                    }
                });
    }

    public void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onClickSignInWithEmail(View v) {
        String email = txtEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            txtEmail.setError("Email can't be empty");
            return;
        }
        txtEmail.setError(null);

        progressSendingEmail.setVisibility(View.VISIBLE);

        ActionCodeSettings acSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://rememo-cb013.web.app")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("it.rememo.rememo", true, "0")
                .build();

        fAuth.sendSignInLinkToEmail(email, acSettings).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("signinEmail", email).apply();
                    progressSendingEmail.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(LoginActivity.this, EmailSentActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signInWithEmailLink(String emailLink) {
        String email = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("signinEmail", "NONE");
        if (email == "None") {
            failedLogin("Failed login, use the same device you send your email with");
            return;
        }
        fAuth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            openAfterLogin(task.getResult());
                            // You can access the new user via result.getUser()
                            // Additional user info profile *not* available via:
                            // result.getAdditionalUserInfo().getProfile() == null
                            // You can check if the user is new or existing:
                            // result.getAdditionalUserInfo().isNewUser()
                        } else {
                            failedLogin(null);
                        }
                    }
                });
    }

    private void failedLogin(String message) {
        Toast.makeText(LoginActivity.this, message == null ? "Failed login, please try again" : message, Toast.LENGTH_LONG).show();
    }

    private void openAfterLogin(AuthResult result) {
        FirebaseUser user = result.getUser();
        String name = result.getUser().getDisplayName();
        Toast.makeText(LoginActivity.this, "Logged in as " + (name == null ? user.getEmail() : name), Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), name == null ? AfterSignUpActivity.class : MainActivity.class));
        finish();
    }
}