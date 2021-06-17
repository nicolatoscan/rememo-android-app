package it.rememo.rememo.ui.study;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.rememo.rememo.databinding.ActivityTestBinding;
import it.rememo.rememo.models.CollectionWord;

// Test the user with random words
public class TestActivity extends AppCompatActivity {

    public final static String ARG_COLLECTIONS = "collections";
    List<CollectionWord> allWords;
    ActivityTestBinding binding;
    TestRecyclerViewAdapter adapter;
    // Default length of test
    int N = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        List<String> collectionsIds = i.getStringArrayListExtra(TestActivity.ARG_COLLECTIONS);

        binding.list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TestRecyclerViewAdapter(this);
        binding.list.setAdapter(adapter);

        allWords = new ArrayList<>();

        // Load words
        CollectionWord.getAllWordsOfCollections(collectionsIds,
            words -> {
                this.allWords.addAll(words);
                loadTest();
            },
            ex -> {}
        );
    }

    // Choose words
    void loadTest() {
        Collections.shuffle(this.allWords);

        int size = this.allWords.size();
        if (N > size) {
            N = size;
        }
        this.allWords = this.allWords.subList(0, size);
        this.adapter.addAll(this.allWords);
    }
}