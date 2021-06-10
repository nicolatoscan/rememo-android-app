package it.rememo.rememo.ui.shared;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.rememo.rememo.databinding.FragmentCollectionGroupBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.FirebaseModel;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.classes.ClassesRecyclerViewAdapter;
import it.rememo.rememo.ui.collections.CollectionDetailsActivity;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;

// Instances of this class are fragments representing a single
// object in our collection.
public abstract class GroupFragment<T extends FirebaseModel> extends Fragment {
    public static final String ARG_POSITION = "position";
    protected GroupRecyclerViewAdapter adapter;
    protected FragmentCollectionGroupBinding binding;
    protected FirebaseFirestore db;
    protected ArrayList<T> list = new ArrayList<>();
    protected int position;

    protected abstract boolean isFloatingAddVisible(int index);
    protected abstract void setupAdapter();
    protected abstract void updateList();
    protected abstract void onAddClicked();
    protected abstract void parseArgs(Bundle args);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollectionGroupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        Bundle args = getArguments();
        position = args.getInt(ARG_POSITION, -1);
        parseArgs(args);
        if (isFloatingAddVisible(position)) {
            binding.addCollectionFloatingButton.setVisibility(View.VISIBLE);
            binding.addCollectionFloatingButton.setOnClickListener(v -> onAddClicked());
        }

        // Recycler View
        binding.collectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupAdapter();

        // To update collections
        binding.collectionSwipeContainer.setOnRefreshListener(() -> updateList());
        updateList();
    }

}
