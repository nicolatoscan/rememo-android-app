package it.rememo.rememo.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.FragmentHomeBinding;
import it.rememo.rememo.models.EStudyType;
import it.rememo.rememo.models.Stat;
import it.rememo.rememo.models.StatData;
import it.rememo.rememo.ui.classes.ClassDetailsActivity;
import it.rememo.rememo.ui.study.ChooseCollectionsActivity;
import it.rememo.rememo.utils.Common;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        Common.setChartStyle(binding.chartProgress, false);
        Common.setChartStyle(binding.chartCollections, true);
        binding.chartProgress.setVisibility(View.GONE);
        binding.chartCollections.setVisibility(View.GONE);

        Stat.getLastMonthRatio(
                ratios -> {
                    if (ratios.size() == 0) {
                        binding.txtLoadingChartProgresses.setText(Common.resStr(getContext(), R.string.basic_chart_no_data));
                        return;
                    }
                    if (ratios.size() == 1) {
                        ratios.add(ratios.get(0));
                    }
                    ArrayList<Entry> entries = new ArrayList<>();
                    int i = 0;
                    for (double s : ratios) {
                        entries.add(new Entry(i++, (int)(s * 100)));
                    }

                    LineDataSet set = new LineDataSet(entries, "Percentage");
                    LineData data = new LineData(Common.setLineDataSetStyle(set, getContext()));
                    binding.chartProgress.setData(Common.setLineDataStyle(data));
                    binding.chartProgress.invalidate();
                    binding.txtLoadingChartProgresses.setVisibility(View.GONE);
                    binding.chartProgress.setVisibility(View.VISIBLE);
                },
                ex -> Common.toast(getContext(), "Couldn't load chart")
        );

        Stat.fetchCollectionsWithNames(Common.getUserId(),
            collectionsStats -> {
                if (collectionsStats.size() == 0) {
                    binding.txtLoadingChartCollections.setText(Common.resStr(getContext(), R.string.basic_chart_no_data));
                    return;
                }
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                int i = 0;
                for (String s : collectionsStats.keySet()) {
                    StatData sd = collectionsStats.get(s);
                    entries.add(new BarEntry(i++, new float[] { sd.getCorrect(), sd.getWrong()  }, s));
                    labels.add(s);
                }

                BarDataSet set = new BarDataSet(entries, "Collections");
                set.setBarBorderWidth(0.1f);
                set.setColors(new int[] { R.color.rememo_primary, R.color.error_red }, getContext());
                BarData data = new BarData(set);
                data.setHighlightEnabled(false);
                binding.chartCollections.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                Common.setBarChartStyle(binding.chartCollections);
                binding.chartCollections.setData(data);
                binding.chartCollections.invalidate();
                binding.txtLoadingChartCollections.setVisibility(View.GONE);
                binding.chartCollections.setVisibility(View.VISIBLE);
            },
            ex -> {}
        );

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