package it.rememo.rememo.ui.classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import it.rememo.rememo.databinding.FragmentClassesBinding;
import it.rememo.rememo.ui.collections.CollectionsGroupPagerAdapter;

public class ClassesFragment extends Fragment {

    private ClassesViewModel classesViewModel;
    private FragmentClassesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        classesViewModel = new ViewModelProvider(this).get(ClassesViewModel.class);
        binding = FragmentClassesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ClassesGroupPagerAdapter adapter = new ClassesGroupPagerAdapter(this);
        binding.pager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.pager,
                (tab, position) ->  tab.setText(adapter.getTitle(position))
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}