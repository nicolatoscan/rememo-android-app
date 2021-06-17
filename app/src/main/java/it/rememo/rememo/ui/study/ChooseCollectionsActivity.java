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

public class ChooseCollectionsActivity extends AppCompatActivity {
    public final static String ARG_STUDY_TYPE = "studyType";
    ActivityChooseCollectionsBinding binding;
    ChooseCollectionsRecyclerViewAdapter adapter;
    int learnType = -1;
    boolean collectionDone = false;
    boolean classesDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseCollectionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        learnType = i.getIntExtra(ARG_STUDY_TYPE, EStudyType.LEARN);

        if (learnType == EStudyType.LEARN) {
            setTitle("Choose a collection to learn");
        } else if (learnType == EStudyType.TEST) {
            setTitle("Choose collections to test");
        } else if (learnType == EStudyType.TRAIN) {
            setTitle("Choose collections to train");
        }

        binding.colllectionList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChooseCollectionsRecyclerViewAdapter(this, learnType != EStudyType.LEARN);
        binding.colllectionList.setAdapter(adapter);

        Collection.getMyCollections(
                collections -> {
                    if (collections.size() > 0)
                        binding.txtLoading.setVisibility(View.GONE);
                    if (classesDone && collections.size() == 0)
                        binding.txtLoading.setText(Common.resStr(this, R.string.basic_no_collections));
                    collectionDone = true;
                    adapter.addAll(collections);
                    if (classesDone && adapter.getItemCount() - 1 <= 0)
                        binding.txtLoading.setText(Common.resStr(this, R.string.basic_no_collections));
                },
                ex -> Common.toast(this, Common.resStr(this, R.string.colls_cant_load))
        );

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
                                    binding.txtLoading.setText(Common.resStr(this, R.string.basic_no_collections));

                            },
                            ex -> Common.toast(this, Common.resStr(this, R.string.colls_cant_load))
                    );
                }
            },
            ex -> Common.toast(this, Common.resStr(this, R.string.colls_cant_load))
        );

        binding.button.setOnClickListener(v -> start());


    }

    void start() {
        if (learnType < 0 || learnType > 2)
            return;

        ArrayList<String> selectedIds = this.adapter.getSelectedIds();
        if (selectedIds.size() <= 0) {
            Common.toast(this, "You need to select at least a collection");
            return;
        }

        Intent i = null;
        if (learnType == EStudyType.LEARN) {
            i = new Intent(this, LearnActivity.class);
        } else if (learnType == EStudyType.TEST) {
            i = new Intent(this, TestActivity.class);
        } else if (learnType == EStudyType.TRAIN) {
            i = new Intent(this, TrainActivity.class);
        }

        if (i != null) {
            i.putExtra(TestActivity.ARG_COLLECTIONS, selectedIds);
            startActivity(i);
            finish();
        }
    }
}