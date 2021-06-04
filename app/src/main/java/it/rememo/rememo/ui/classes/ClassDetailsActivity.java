package it.rememo.rememo.ui.classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import it.rememo.rememo.databinding.ActivityClassDetailsBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.collections.CollectionDetailsActivity;
import it.rememo.rememo.utils.Common;

public class ClassDetailsActivity extends AppCompatActivity {
    public static String ARG_CLASS = "class";
    ActivityClassDetailsBinding binding;
    private StudentClass studentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        studentClass = (StudentClass) getIntent().getSerializableExtra(ClassDetailsActivity.ARG_CLASS);
        setTitle(studentClass.getName());

        binding.btnCollections.setOnClickListener((v) -> {
            Intent i = new Intent(this, ClassCollectionsActivity.class);
            i.putExtra(ClassCollectionsActivity.ARG_COLLECTIONS, studentClass.getId());
            startActivity(i);
        });
    }
}