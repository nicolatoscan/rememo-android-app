package it.rememo.rememo.ui.collections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.rememo.rememo.R;

// Instances of this class are fragments representing a single
// object in our collection.
public class CollectionsGroupFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    CollectionRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");
        animalNames.add("Pippo");
        animalNames.add("Pluto");
        animalNames.add("Paperino");
        animalNames.add("Ornitorinco");
        animalNames.add("Anatra");
        animalNames.add("Pinguino");
        animalNames.add("Panda");
        animalNames.add("Koala");

        RecyclerView recyclerView = view.findViewById(R.id.collectionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CollectionRecyclerViewAdapter(getContext(), animalNames);
        adapter.setClickListener((v, position) -> {
            Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

    }
}
