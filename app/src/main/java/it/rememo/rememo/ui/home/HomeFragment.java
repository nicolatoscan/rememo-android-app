package it.rememo.rememo.ui.home;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Map;

import it.rememo.rememo.R;
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

/*
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.textcolor, typedValue, true);
        @ColorInt int color = typedValue.data;
        l.setTextColor(color);*/


        LineChart chart = binding.homeChart;
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 10));
        entries.add(new Entry(1, 8));
        entries.add(new Entry(2, 12));
        entries.add(new Entry(3, 10));
        entries.add(new Entry(4, 5));
        entries.add(new Entry(5, 7));
        LineDataSet set = new LineDataSet(entries, "Coll 1");
        ArrayList<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 12));
        entries1.add(new Entry(1, 10));
        entries1.add(new Entry(2, 10));
        entries1.add(new Entry(3, 18));
        entries1.add(new Entry(4, 5));
        entries1.add(new Entry(5, 10));
        LineDataSet set1 = new LineDataSet(entries1, "Coll 2");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        chart.setData(data);

        binding.btnLearn.setOnClickListener(v -> startStudy(EStudyType.LEARN));
        binding.btnTest.setOnClickListener(v -> startStudy(EStudyType.TEST));
        binding.btnTrain.setOnClickListener(v -> startStudy(EStudyType.TRAIN));
        return binding.getRoot();
    }

    void startStudy(int type) {
        Intent i = new Intent(getContext(), ChooseCollectionsActivity.class);
        i.putExtra(ChooseCollectionsActivity.ARG_STUDY_TYPE, type);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}