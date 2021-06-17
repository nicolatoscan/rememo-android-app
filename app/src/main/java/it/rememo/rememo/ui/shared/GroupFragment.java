package it.rememo.rememo.ui.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import it.rememo.rememo.databinding.FragmentCollectionGroupBinding;
import it.rememo.rememo.models.FirebaseModel;

// Instances of this class are fragments representing a single
// object in our collection.
public abstract class GroupFragment<T extends FirebaseModel> extends Fragment {
    public final static String ARG_POSITION = "position";
    protected GroupRecyclerViewAdapter adapter;
    protected FragmentCollectionGroupBinding binding;
    protected FirebaseFirestore db;
    final protected ArrayList<T> list = new ArrayList<>();
    protected int position;

    protected abstract boolean isFloatingAddVisible(int index);
    protected abstract void setUp();
    protected abstract void updateList();
    protected abstract void onAddClicked();
    protected abstract void parseArgs(Bundle args);

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        setUp();

        // To update collections
        binding.collectionSwipeContainer.setOnRefreshListener(this::updateList);
        updateList();
    }

}
