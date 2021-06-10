package it.rememo.rememo.ui.collections;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.shared.GroupFragment;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;

public class CollectionsGroupFragment extends GroupFragment<Collection> {

    public static final String ARG_CLASS = "class";
    private StudentClass stClass = null;

    @Override
    protected void parseArgs(Bundle args) {
        this.stClass = (StudentClass) args.getSerializable(ARG_CLASS);
    }

    protected boolean isFloatingAddVisible(int index) {
        return index == 0;
    }

    protected void setupAdapter() {
        adapter = new CollectionsRecyclerViewAdapter(getContext(), list, this.stClass == null);
        adapter.setClickListener((v, i) -> {
            Intent intent = new Intent(getContext(), CollectionDetailsActivity.class);
            intent.putExtra(CollectionDetailsActivity.ARG_COLLECTION, adapter.getItem(i));
            intent.putExtra(CollectionDetailsActivity.ARG_EDITABLE, this.stClass == null);
            startActivity(intent);
        });
        binding.collectionRecyclerView.setAdapter(adapter);
    }

    protected void updateList() {
        @NonNull OnSuccessListener<? super List<Collection>> success = colls -> {
            adapter.resetAll(colls);
            binding.collectionSwipeContainer.setRefreshing(false);
        };
        @NonNull OnFailureListener fail = ex -> {
            Common.toast(getContext(), "Couldn't update collections");
            binding.collectionSwipeContainer.setRefreshing(false);
        };

        if (stClass == null) {
            Collection.getMyCollections(success, fail);
        } else {
            stClass.getClassCollections(success, fail);
        }
    }

    protected void onAddClicked() {
        final EditText textInput = new EditText(getContext());
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint("Collection name");

        Alerts
            .getInputTextAlert(getContext(), textInput)
            .setTitle("Create a new collection")
            .setPositiveButton("Create", (dialog, which) -> {
                String title = textInput.getText().toString();
                if (title.length() > 0) {
                    createCollection(title);
                }
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
            .show();
    }

    private Collection createCollection(String name) {
        Collection collection = new Collection(name, null, 0);
        collection.addToFirestore(
            doc -> {
                adapter.add(collection);
                binding.collectionRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            },
            ex -> Common.toast(getContext(), "Error creating collection, please try again later")
        );
        return collection;
    }
}
