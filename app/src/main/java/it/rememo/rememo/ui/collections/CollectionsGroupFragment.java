package it.rememo.rememo.ui.collections;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.FragmentCollectionGroupBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.utils.Common;

// Instances of this class are fragments representing a single
// object in our collection.
public class CollectionsGroupFragment extends Fragment {
    public static final String ARG_TITLE = "title";
    public static final String ARG_POSITION = "position";
    CollectionRecyclerViewAdapter adapter;
    private FragmentCollectionGroupBinding binding;
    FirebaseFirestore db;
    String userId;
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
        String title = args.getString(ARG_TITLE, "");
        int position = args.getInt(ARG_POSITION, -1);
        if (position == 0) {
            binding.addCollectionFloatingButton.setVisibility(View.VISIBLE);
            binding.addCollectionFloatingButton.setOnClickListener(v -> onAddCollectionClick());
        }

        RecyclerView recyclerView = view.findViewById(R.id.collectionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CollectionRecyclerViewAdapter(getContext(), collectionList);
        adapter.setClickListener((v, i) -> {
            Toast.makeText(getContext(), "You clicked " + adapter.getItem(i).getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

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
                    collectionList.clear();
                    collectionList.addAll(updatedCollections);
                    adapter.notifyDataSetChanged();
                } else {
                    Common.showToast(getContext(), "Couldn't update collections");
                }
            });
    }

    private void onAddCollectionClick() {
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Collection name");

        FrameLayout container = new FrameLayout(getContext());
        container.setPadding(40, 50, 40, 20);
        container.addView(input);

        new AlertDialog.Builder(getContext())
            .setTitle("Create a new collection")
            .setView(container)
            .setPositiveButton("Create", (dialog, which) -> {
                String title = input.getText().toString();
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
                collectionList.add(collection);
                adapter.notifyItemInserted(collectionList.size() - 1);
            },
            ex -> Common.showToast(getContext(), "Error creating collection, please try again later")
        );
        return collection;
    }
}
