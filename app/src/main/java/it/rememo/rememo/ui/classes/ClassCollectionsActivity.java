package it.rememo.rememo.ui.classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.stream.Collectors;

import it.rememo.rememo.databinding.ActivityListWithAddBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.FirebaseModel;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.shared.ListWithRemoveAdapter;
import it.rememo.rememo.utils.Common;

public class ClassCollectionsActivity extends AppCompatActivity {
    public static String ARG_COLLECTIONS;
    ActivityListWithAddBinding binding;
    StudentClass stClass = null;
    ArrayList<FirebaseModel> collList;
    ListWithRemoveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListWithAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addBtn.setText("Add collection");

        String classId = getIntent().getStringExtra(ARG_COLLECTIONS);
        collList = new ArrayList<>();
        adapter = new ListWithRemoveAdapter(this, collList);
        adapter.setDeleteClickListener((v, i) -> {

        });
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(adapter);

        StudentClass.getClassById(
                classId,
                (cl) -> {
                    stClass = cl;
                    cl.getClassCollections(
                            collections -> adapter.addAll(collections),
                            ex -> Common.toast(this, "Couldn't load collections")
                    );
                },
                (ex) -> Common.toast(this, "Couldn't load collections")
        );

        binding.addBtn.setOnClickListener(v -> onAddCollectionClick() );

    }

    void onAddCollectionClick() {
        Collection.getMyCollections(
                colls -> {
                    colls = colls
                            .stream()
                            .filter(c -> collList.stream().noneMatch(streamC -> streamC.getId().equals(c.getId())))
                            .collect(Collectors.toList());

                    if (colls.size() > 0) {
                        new AddCollectionDialogFragment(colls,
                            selectedCollections -> {
                                stClass.addCollections(
                                        selectedCollections,
                                        s -> adapter.addAll(selectedCollections),
                                        ex -> Common.toast(this, "Couldn't add collections")
                                );
                            }
                        ).show(getSupportFragmentManager(), "PIPPO");
                    } else {
                        Common.toast(this, "No other collection to add");
                    }
                },
                ex -> Common.toast(this, "Couldn't load collections")
        );

    }
}