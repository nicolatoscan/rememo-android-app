package it.rememo.rememo.ui.study;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.databinding.ActivityChooseCollectionsBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.models.EStudyType;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.collections.WordsRecyclerViewAdapter;
import it.rememo.rememo.utils.Common;

public class ChooseCollectionsActivity extends AppCompatActivity {
    public final static String ARG_STUDY_TYPE = "studyType";
    ActivityChooseCollectionsBinding binding;
    ChooseCollectionsRecyclerViewAdapter adapter;
    int learnType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseCollectionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        learnType = i.getIntExtra(ARG_STUDY_TYPE, EStudyType.LEARN);

        binding.colllectionList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChooseCollectionsRecyclerViewAdapter(this, learnType != EStudyType.LEARN);
        binding.colllectionList.setAdapter(adapter);

        Collection.getMyCollections(
                collections -> adapter.addAll(collections),
                ex -> Common.toast(this, "Couldn't load collections")
        );

        StudentClass.getClasses(false,
            classes -> {
                for (StudentClass sc : classes) {
                    sc.getClassCollections(
                            collections -> adapter.addAll(collections),
                            ex -> Common.toast(this, "Couldn't load collections")
                    );
                }
            },
            ex -> Common.toast(this, "Couldn't load collections")
        );

        binding.button.setOnClickListener(v -> start());


    }

    void start() {
        if (learnType < 0 || learnType > 2)
            return;

        Intent i = null;
        if (learnType == EStudyType.LEARN) {
        } else if (learnType == EStudyType.TEST) {
            i = new Intent(this, TestActivity.class);
            i.putExtra(TestActivity.ARG_COLLECTIONS, this.adapter.getSelectedIds());
        } else if (learnType == EStudyType.TRAIN) {
        }

        if (i != null) {
            startActivity(i);
            finish();
        }
    }
}