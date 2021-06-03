package it.rememo.rememo.ui.collections;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.FragmentCollectionGroupBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;

// Instances of this class are fragments representing a single
// object in our collection.
public class CollectionsGroupFragment extends Fragment {
    public static final String ARG_POSITION = "position";
    CollectionsRecyclerViewAdapter adapter;
    private FragmentCollectionGroupBinding binding;
    FirebaseFirestore db;
    ArrayList<Collection> collectionList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollectionGroupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        Bundle args = getArguments();
        int position = args.getInt(ARG_POSITION, -1);
        if (position == 0) {
            binding.addCollectionFloatingButton.setVisibility(View.VISIBLE);
            binding.addCollectionFloatingButton.setOnClickListener(v -> onAddCollectionClick());
        }

        // Recycler View
        binding.collectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CollectionsRecyclerViewAdapter(getContext(), collectionList);
        adapter.setClickListener((v, i) -> {
            Intent intent = new Intent(getContext(), CollectionDetailsActivity.class);
            intent.putExtra(CollectionDetailsActivity.ARG_COLLECTION, adapter.getItem(i));
            startActivity(intent);
        });
        binding.collectionRecyclerView.setAdapter(adapter);

        // To update collections
        binding.collectionSwipeContainer.setOnRefreshListener(() -> updateCollectionList());
        updateCollectionList();
    }

    private void updateCollectionList() {
        db
            .collection(Collection.COLLECTION_NAME)
            .whereEqualTo(Collection.KEY_OWNER_ID, Common.getUserId())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<Collection> updatedCollections = new ArrayList();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        updatedCollections.add(new Collection(document));
                    }
                    adapter.resetAll(updatedCollections);
                } else {
                    Common.toast(getContext(), "Couldn't update collections");
                }
                binding.collectionSwipeContainer.setRefreshing(false);
            });
    }

    private void onAddCollectionClick() {
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
