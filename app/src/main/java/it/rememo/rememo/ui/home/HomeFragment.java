package it.rememo.rememo.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.rememo.rememo.databinding.FragmentHomeBinding;
import it.rememo.rememo.models.EStudyType;
import it.rememo.rememo.ui.study.ChooseCollectionsActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        /*
        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        binding.btnLearn.setOnClickListener(v -> startStudy(EStudyType.LEARN));
        binding.btnTest.setOnClickListener(v -> startStudy(EStudyType.TEST));
        binding.btnTrain.setOnClickListener(v -> startStudy(EStudyType.TRAIN));
        return binding.getRoot();
    }

    void startStudy(int type) {
        Intent i = new Intent(getContext(), ChooseCollectionsActivity.class);
        i.getIntExtra(ChooseCollectionsActivity.ARG_STUDY_TYPE, type);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}