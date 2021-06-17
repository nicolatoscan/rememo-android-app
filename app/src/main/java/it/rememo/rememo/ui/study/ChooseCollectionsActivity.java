package it.rememo.rememo.ui.study;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityChooseCollectionsBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.EStudyType;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.utils.Common;

// Choose collection to train / learn / study (either multi or single select)
public class ChooseCollectionsActivity extends AppCompatActivity {
    public final static String ARG_STUDY_TYPE = "studyType";
    ActivityChooseCollectionsBinding binding;
    ChooseCollectionsRecyclerViewAdapter adapter;
    // is Learn, Study or Train
    int studyType = -1;
    boolean collectionDone = false;
    boolean classesDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseCollectionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        studyType = i.getIntExtra(ARG_STUDY_TYPE, EStudyType.LEARN);

        // Set different title
        if (studyType == EStudyType.LEARN) {
            setTitle(getString(R.string.title_choose_learn));
        } else if (studyType == EStudyType.TEST) {
            setTitle(getString(R.string.title_choose_test));
        } else if (studyType == EStudyType.TRAIN) {
            setTitle(getString(R.string.title_choose_train));
        }

        binding.colllectionList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChooseCollectionsRecyclerViewAdapter(this, studyType != EStudyType.LEARN);
        binding.colllectionList.setAdapter(adapter);

        // get my collections to lsit
        Collection.getMyCollections(
                collections -> {
                    // List collections
                    if (collections.size() > 0)
                        binding.txtLoading.setVisibility(View.GONE);
                    if (classesDone && collections.size() == 0)
                        binding.txtLoading.setText(getString(R.string.basic_no_collections));
                    collectionDone = true;
                    adapter.addAll(collections);
                    if (classesDone && adapter.getItemCount() - 1 <= 0)
                        binding.txtLoading.setText(getString(R.string.basic_no_collections));
                },
                ex -> Common.toast(this, getString(R.string.colls_cant_load))
        );

        // get collections to list from classes
        StudentClass.getClasses(false,
            classes -> {
                for (StudentClass sc : classes) {
                    sc.getClassCollections(
                            collections -> {
                                if (collections.size() > 0)
                                    binding.txtLoading.setVisibility(View.GONE);
                                classesDone = true;
                                adapter.addAll(collections);
                                if (collectionDone && adapter.getItemCount() - 1 <= 0)
                                    binding.txtLoading.setText(getString(R.string.basic_no_collections));

                            },
                            ex -> Common.toast(this, getString(R.string.colls_cant_load))
                    );
                }
            },
            ex -> Common.toast(this, getString(R.string.colls_cant_load))
        );

        binding.button.setOnClickListener(v -> start());


    }

    // Button start study
    void start() {
        if (studyType < 0 || studyType > 2)
            return;

        ArrayList<String> selectedIds = this.adapter.getSelectedIds();
        if (selectedIds.size() <= 0) {
            Common.toast(this, getString(R.string.study_at_least_collection));
            return;
        }

        Intent i = null;
        if (studyType == EStudyType.LEARN) {
            i = new Intent(this, LearnActivity.class);
        } else if (studyType == EStudyType.TEST) {
            i = new Intent(this, TestActivity.class);
        } else if (studyType == EStudyType.TRAIN) {
            i = new Intent(this, TrainActivity.class);
        }

        if (i != null) {
            i.putExtra(TestActivity.ARG_COLLECTIONS, selectedIds);
            startActivity(i);
            finish();
        }
    }
}