package it.rememo.rememo.ui.collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityCollectionDetailsBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.utils.Common;

public class CollectionDetailsActivity extends AppCompatActivity {
    public final static String ARG_COLLECTION = "collection";
    private ActivityCollectionDetailsBinding binding;
    private Collection collection;
    WordsRecyclerViewAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        collection = (Collection) getIntent().getSerializableExtra(CollectionDetailsActivity.ARG_COLLECTION);
        setTitle(collection.getName());

        // Recycler View
        binding.wordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordsRecyclerViewAdapter(this, new ArrayList<CollectionWord>());
        adapter.setClickListener((v, i) -> { });
        binding.wordsRecyclerView.setAdapter(adapter);

        // To update words
        binding.wordsSwipeContainer.setOnRefreshListener(() -> updateWordList());
        updateWordList();

        binding.btnAddWord.setOnClickListener(v -> onAddWordClick(v));
    }

    private void updateWordList() {
        collection.fetchWords(
            words ->  {
                adapter.resetAll(words);
                binding.wordsSwipeContainer.setRefreshing(false);
            },
            ex -> {
                Common.toast(this, "Couldn't load words");
                binding.wordsSwipeContainer.setRefreshing(false);
            }
        );
    }

    private void onAddWordClick(View v) {
        String original = binding.txtOriginalWord.getText().toString();
        String translated = binding.txtTranslatedWord.getText().toString();
        binding.btnAddWord.setEnabled(false);
        collection.addWord(new CollectionWord(original, translated),
                resW -> { adapter.add(resW); resetInputWord(); },
                ex -> { Common.toast(this, "Couldn't create a new word"); resetInputWord(); }
        );
    }
    private void resetInputWord() {
        binding.txtOriginalWord.setText("");
        binding.txtTranslatedWord.setText("");
        binding.btnAddWord.setEnabled(true);
    }

}