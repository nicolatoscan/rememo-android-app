package it.rememo.rememo.ui.classes;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.rememo.rememo.databinding.FragmentCollectionGroupBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.collections.CollectionDetailsActivity;
import it.rememo.rememo.ui.collections.CollectionsRecyclerViewAdapter;
import it.rememo.rememo.ui.shared.GroupFragment;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;
public class ClassesGroupFragment extends GroupFragment<Collection> {

    @Override
    protected void parseArgs(Bundle args) { }

    protected boolean isFloatingAddVisible(int index) {
        return index == 1;
    }

    protected void setupAdapter() {
        adapter = new ClassesRecyclerViewAdapter(getContext(), list);
        adapter.setClickListener((v, i) -> {
            Intent intent = new Intent(getContext(), ClassDetailsActivity.class);
            StudentClass c = (StudentClass) adapter.getItem(i);
            intent.putExtra(ClassDetailsActivity.ARG_CLASS, c);
            intent.putExtra("CIAO", "CIAO");
            startActivity(intent);
        });
        binding.collectionRecyclerView.setAdapter(adapter);
    }

    protected void updateList() {
        if (!(position == 0 || position == 1)) {
            return;
        }
        StudentClass.getClasses(position == 1,
            (updatedCollections) -> {
                adapter.resetAll(updatedCollections);
                binding.collectionSwipeContainer.setRefreshing(false);
            },
            (ex) -> {
                Common.toast(getContext(), "Couldn't update collections");
                binding.collectionSwipeContainer.setRefreshing(false);
            });
    }

    protected void onAddClicked() {
        final EditText textInput = new EditText(getContext());
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint("Class name");

        Alerts
                .getInputTextAlert(getContext(), textInput)
                .setTitle("Create a new class")
                .setPositiveButton("Create", (dialog, which) -> {
                    String title = textInput.getText().toString();
                    if (title.length() > 0) {
                        createClass(title);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private StudentClass createClass(String name) {
        StudentClass cl = new StudentClass(name);
        cl.addToFirestore(
                doc -> {
                    adapter.add(cl);
                    binding.collectionRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                },
                ex -> Common.toast(getContext(), "Error creating collection, please try again later")
        );
        return cl;
    }
}
