package it.rememo.rememo.ui.collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityCollectionDetailsBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.utils.Common;

// Details of a collection with its words
public class CollectionDetailsActivity extends AppCompatActivity {
    public final static String ARG_COLLECTION = "collection";
    public final static String ARG_EDITABLE = "isEditable";
    private ActivityCollectionDetailsBinding binding;
    private Collection collection;
    private boolean isEditable;
    WordsRecyclerViewAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        collection = (Collection) intent.getSerializableExtra(CollectionDetailsActivity.ARG_COLLECTION);
        isEditable = intent.getBooleanExtra(CollectionDetailsActivity.ARG_EDITABLE, false);

        setTitle(collection.getName());

        // Recycler View
        binding.wordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordsRecyclerViewAdapter(this, new ArrayList<>());
        adapter.setClickListener((v, i) -> { });
        binding.wordsRecyclerView.setAdapter(adapter);

        // To update words
        binding.wordsSwipeContainer.setOnRefreshListener(this::updateWordList);
        updateWordList();

        binding.btnAddWord.setOnClickListener(this::onAddWordClick);

        if (!isEditable) {
            binding.constraintLayout.setVisibility(View.GONE);
        }
    }

    private void updateWordList() {
        binding.txtLoading.setVisibility(View.GONE);

        // Get all words and update list
        collection.fetchWords(
            words ->  {
                if (words.size() <= 0) {
                    binding.txtLoading.setVisibility(View.VISIBLE);
                }
                adapter.resetAll(words);
                binding.wordsSwipeContainer.setRefreshing(false);
            },
            ex -> {
                Common.toast(this, getString(R.string.words_cant_load));
                binding.wordsSwipeContainer.setRefreshing(false);
            }
        );
    }

    // Add a new word
    private void onAddWordClick(View v) {
        String original = binding.txtOriginalWord.getText().toString();
        String translated = binding.txtTranslatedWord.getText().toString();

        if (original.length() <= 0 || translated.length() <= 0) {
            return;
        }

        binding.btnAddWord.setEnabled(false);
        // Add to database
        collection.addWord(new CollectionWord(original, translated),
            resW -> {
                // Word added
                adapter.add(resW);
                binding.wordsRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                resetInputWord();
                binding.txtLoading.setVisibility(View.GONE);
            },
            ex -> { Common.toast(this, getString(R.string.word_cant_create_new)); resetInputWord(); }
        );
    }

    private void resetInputWord() {
        binding.txtOriginalWord.setText("");
        binding.txtTranslatedWord.setText("");
        binding.btnAddWord.setEnabled(true);
    }

}