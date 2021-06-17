package it.rememo.rememo.ui.classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;

import it.rememo.rememo.MainActivity;
import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityJoinClassBinding;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.login.LoginActivity;
import it.rememo.rememo.utils.Common;

public class JoinClassActivity extends AppCompatActivity {
    ActivityJoinClassBinding binding;
    StudentClass stClass;
    boolean cantJoin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnJoinClass.setEnabled(false);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        binding.btnJoinClass.setOnClickListener(v -> joinClass());

        binding.btnJoinClass.setVisibility(View.GONE);
        binding.txtClassName.setVisibility(View.GONE);
        binding.txtTitle.setVisibility(View.GONE);

        Intent appLinkIntent = getIntent();
        if (appLinkIntent != null) {
            String url = appLinkIntent.getData().toString();
            String[] urlParts = url.split("/");
            String id = urlParts[urlParts.length - 1];


            StudentClass.getClassById(
                    id,
                    this::updateUI,
                    ex -> {
                        Common.toast(this, getString(R.string.class_not_found));
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
            );

        }
    }

    void updateUI(StudentClass stClass) {

         if (stClass.getOwnerId().equals(Common.getUserId())) {
            binding.txtTitle.setText(getString(R.string.classes_my_class_join_error));
            binding.btnJoinClass.setText(getString(R.string.login_go_to_rememo));
        } else if (stClass.getStudentsIds().contains(Common.getUserId())) {
            binding.txtTitle.setText(getString(R.string.classes_already_in));
            binding.btnJoinClass.setText(getString(R.string.login_go_to_rememo));
        } else {
            cantJoin = false;
        }

        binding.txtClassName.setText(stClass.getName());

        binding.btnJoinClass.setVisibility(View.VISIBLE);
        binding.txtClassName.setVisibility(View.VISIBLE);
        binding.txtTitle.setVisibility(View.VISIBLE);


        this.stClass = stClass;
        binding.btnJoinClass.setEnabled(true);
    }

    void joinClass() {
        if (stClass == null)
            return;

        if (cantJoin) {
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        binding.btnJoinClass.setEnabled(false);
        binding.progressJoin.setVisibility(View.VISIBLE);

        stClass.joinClass(
            x -> {
                Common.toast(this, getString(R.string.class_joined));
                startActivity(new Intent(this, MainActivity.class));
            },
            ex -> Common.toast(this, getString(R.string.class_cant_join))
        );

    }


}