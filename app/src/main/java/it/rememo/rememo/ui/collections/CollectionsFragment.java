package it.rememo.rememo.ui.collections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.FragmentCollectionsBinding;

public class CollectionsFragment extends Fragment {

    private CollectionsViewModel collectionsViewModel;
    private FragmentCollectionsBinding binding;
    CollectionsGroupPagerAdapter collectionsGroupPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        collectionsViewModel = new ViewModelProvider(this).get(CollectionsViewModel.class);
        binding = FragmentCollectionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ArrayList<String> collectionsGroups = new ArrayList<String>();
        collectionsGroups.add("Mine");
        collectionsGroups.add("Class 4°B");
        collectionsGroups.add("Class 4°A");
        binding.pager.setAdapter(new CollectionsGroupPagerAdapter(this, collectionsGroups));
        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) ->  tab.setText(collectionsGroups.get(position))).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

