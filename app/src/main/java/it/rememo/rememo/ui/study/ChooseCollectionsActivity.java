package it.rememo.rememo.ui.study;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import it.rememo.rememo.databinding.ActivityChooseCollectionsBinding;
import it.rememo.rememo.models.EStudyType;

public class ChooseCollectionsActivity extends AppCompatActivity {
    public final static String ARG_STUDY_TYPE = "studyType";
    ActivityChooseCollectionsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseCollectionsBinding.inflate(getLayoutInflater());

        Intent i = getIntent();
        i.getIntExtra(ARG_STUDY_TYPE, EStudyType.LEARN);
    }
}