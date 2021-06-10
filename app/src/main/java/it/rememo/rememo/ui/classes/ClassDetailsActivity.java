package it.rememo.rememo.ui.classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import it.rememo.rememo.databinding.ActivityClassDetailsBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.collections.CollectionDetailsActivity;
import it.rememo.rememo.utils.Common;

public class ClassDetailsActivity extends AppCompatActivity {
    public static String ARG_CLASS = "class";
    public static String ARG_IS_CREATED = "isCreated";
    ActivityClassDetailsBinding binding;
    private StudentClass studentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        studentClass = (StudentClass) intent.getSerializableExtra(ClassDetailsActivity.ARG_CLASS);
        boolean isCreated = intent.getBooleanExtra(ClassDetailsActivity.ARG_IS_CREATED, false);
        setTitle(studentClass.getName());

        if (isCreated) {
            binding.btnCollections.setOnClickListener((v) -> {
                Intent i = new Intent(this, ClassCollectionsActivity.class);
                i.putExtra(ClassCollectionsActivity.ARG_CLASS_ID, studentClass.getId());
                startActivity(i);
            });

            binding.btnStudents.setOnClickListener((v) -> {
                Intent i = new Intent(this, ClassStudentActivity.class);
                i.putExtra(ClassCollectionsActivity.ARG_CLASS_ID, studentClass.getId());
                startActivity(i);
            });
        } else {
            binding.btnCollections.setVisibility(View.GONE);
            binding.btnStudents.setVisibility(View.GONE);
        }
    }
}