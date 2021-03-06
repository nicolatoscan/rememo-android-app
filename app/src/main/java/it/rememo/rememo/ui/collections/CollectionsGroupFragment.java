package it.rememo.rememo.ui.collections;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import it.rememo.rememo.R;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.shared.GroupFragment;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;

// List of collection by class or mine
public class CollectionsGroupFragment extends GroupFragment<Collection> {

    public static final String ARG_CLASS = "class";
    private StudentClass stClass = null;

    @Override
    protected void parseArgs(Bundle args) {
        // Get class or null if mine
        this.stClass = (StudentClass) args.getSerializable(ARG_CLASS);
    }

    protected boolean isFloatingAddVisible(int index) {
        return index == 0;
    }

    protected void setUp() {
        adapter = new CollectionsRecyclerViewAdapter(getContext(), list, this.stClass == null);
        adapter.setClickListener((v, i) -> {
            Intent intent = new Intent(getContext(), CollectionDetailsActivity.class);
            intent.putExtra(CollectionDetailsActivity.ARG_COLLECTION, adapter.getItem(i));
            intent.putExtra(CollectionDetailsActivity.ARG_EDITABLE, this.stClass == null);
            startActivity(intent);
        });
        binding.collectionRecyclerView.setAdapter(adapter);
        binding.txtLoading.setText(getContext().getString(R.string.basic_no_collections));
    }

    protected void updateList() {
        binding.collectionSwipeContainer.setRefreshing(true);

        @NonNull OnSuccessListener<? super List<Collection>> success = colls -> {

            if (colls.size() <= 0) {
                binding.txtLoading.setVisibility(View.VISIBLE);
            } else {
                binding.txtLoading.setVisibility(View.GONE);
            }

            adapter.resetAll(colls);
            binding.collectionSwipeContainer.setRefreshing(false);
        };
        @NonNull OnFailureListener fail = ex -> {
            Common.toast(getContext(), getContext().getString(R.string.colls_cant_update));
            binding.collectionSwipeContainer.setRefreshing(false);
        };

        if (stClass == null) {
            Collection.getMyCollections(success, fail);
        } else {
            stClass.getClassCollections(success, fail);
        }
    }

    // On floating plus click
    protected void onAddClicked() {
        final EditText textInput = new EditText(getContext());
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint(getContext().getString(R.string.coll_name));

        // Alert to choose name
        Alerts
            .getInputTextAlert(getContext(), textInput)
            .setTitle(getContext().getString(R.string.coll_create_new))
            .setPositiveButton(getContext().getString(R.string.basic_create), (dialog, which) -> {
                String title = textInput.getText().toString();
                // validation
                if (title.length() > 0) {
                    createCollection(title);
                }
            })
            .setNegativeButton(getContext().getString(R.string.basic_cancel), (dialog, which) -> dialog.cancel())
            .show();
    }

    private void createCollection(String name) {
        Collection collection = new Collection(name, null, 0);
        collection.addToFirestore(
            doc -> {
                // Add collection to list
                adapter.add(collection);
                binding.txtLoading.setVisibility(View.GONE);
                binding.collectionRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            },
            ex -> Common.toast(getContext(), getContext().getString(R.string.coll_err_creating_retry))
        );
    }
}
