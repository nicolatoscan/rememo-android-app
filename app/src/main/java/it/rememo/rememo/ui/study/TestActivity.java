package it.rememo.rememo.ui.study;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.rememo.rememo.databinding.ActivityTestBinding;
import it.rememo.rememo.databinding.RowTestItemBinding;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.utils.Common;

public class TestActivity extends AppCompatActivity {

    public final static String ARG_COLLECTIONS = "collections";
    List<CollectionWord> allWords;
    ActivityTestBinding binding;
    TestRecyclerViewAdapter adapter;
    int N = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        List<String> collectionsIds = i.getStringArrayListExtra(ARG_COLLECTIONS);

        binding.list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TestRecyclerViewAdapter(this);
        binding.list.setAdapter(adapter);

        allWords = new ArrayList<>();

        final Counter todo = new TestActivity.Counter(collectionsIds.size());
        for (String id : collectionsIds) {
            CollectionWord.getWordsByCollectionId(
                    id,
                    collectionWords -> {
                        allWords.addAll(collectionWords);
                        if (todo.decrease() == 0) {
                            loadTest();
                        }
                    },
                    ex -> {}
            );
        }
    }

    void loadTest() {
        Collections.shuffle(this.allWords);
        this.allWords = this.allWords.subList(0, N);
        this.adapter.addAll(this.allWords);

        for (int i = 0; i < 100; i++) {
            // RowTestItemBinding row = RowTestItemBinding.inflate(getLayoutInflater());
            // row.txtCollectionRow.setText("Pippo");
            // this.binding.list.addView(row.getRoot());
        }

    }

    class Counter{
        int value;
        Counter(int value) {
            this.value = value;
        }

        public int decrease() {
            return --value;
        }
    }
}