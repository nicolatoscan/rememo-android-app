package it.rememo.rememo.ui.classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;



import it.rememo.rememo.MainActivity;
import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityJoinClassBinding;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.utils.Common;

public class JoinClassActivity extends AppCompatActivity {
    ActivityJoinClassBinding binding;
    StudentClass stClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnJoinClass.setEnabled(false);
        binding.btnJoinClass.setOnClickListener(v -> joinClass());

        Intent appLinkIntent = getIntent();
        if (appLinkIntent != null) {
            String url = appLinkIntent.getData().toString();
            String[] urlParts = url.split("/");
            String id = urlParts[urlParts.length - 1];


            StudentClass.getClassById(
                    id,
                    this::updateUI,
                    ex -> {
                        Common.toast(this, Common.resStr(this, R.string.class_not_found));
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
            );

        }
    }

    void updateUI(StudentClass stClass) {
        this.stClass = stClass;
        binding.txtClassName.setText(stClass.getName());
        binding.btnJoinClass.setEnabled(true);
    }

    void joinClass() {
        if (stClass == null)
            return;

        stClass.joinClass(
            x -> {
                Common.toast(this, Common.resStr(this, R.string.class_joined));
                startActivity(new Intent(this, MainActivity.class));
            },
            ex -> Common.toast(this, Common.resStr(this, R.string.class_cant_join))
        );

    }


}