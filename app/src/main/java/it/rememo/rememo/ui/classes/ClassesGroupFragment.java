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

    protected boolean isFloatingAddVisible(int index) {
        return index == 1;
    }

    protected void setupAdapter() {
        adapter = new ClassesRecyclerViewAdapter(getContext(), list);
        adapter.setClickListener((v, i) -> {
            // Intent intent = new Intent(getContext(), CollectionDetailsActivity.class);
            // intent.putExtra(CollectionDetailsActivity.ARG_COLLECTION, adapter.getItem(i));
            // startActivity(intent);
        });
        binding.collectionRecyclerView.setAdapter(adapter);
    }

    protected void updateList() {
        db
            .collection(Collection.COLLECTION_NAME)
            .whereEqualTo(Collection.KEY_OWNER_ID, Common.getUserId())
            .get()
            .addOnSuccessListener(docs -> {
                ArrayList<Collection> updatedCollections = new ArrayList();
                for (QueryDocumentSnapshot document : docs) {
                    updatedCollections.add(new Collection(document));
                }
                adapter.resetAll(updatedCollections);
                binding.collectionSwipeContainer.setRefreshing(false);
            })
            .addOnFailureListener(ex -> {
                Common.toast(getContext(), "Couldn't update collections");
                binding.collectionSwipeContainer.setRefreshing(false);
            });
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
