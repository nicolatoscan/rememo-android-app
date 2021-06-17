package it.rememo.rememo.ui.classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityClassDetailsBinding;
import it.rememo.rememo.models.Stat;
import it.rememo.rememo.models.StatData;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.utils.Common;
import it.rememo.rememo.utils.ShareUrls;

public class ClassDetailsActivity extends AppCompatActivity {
    public final static String ARG_CLASS = "class";
    public final static String ARG_IS_CREATED = "isCreated";
    ActivityClassDetailsBinding binding;
    private StudentClass studentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        studentClass = (StudentClass) intent.getSerializableExtra(ClassDetailsActivity.ARG_CLASS);
        boolean isCreated = intent.getBooleanExtra(ClassDetailsActivity.ARG_IS_CREATED, false);
        setTitle(studentClass.getName());

        if (isCreated) {
            binding.btnCollections.setOnClickListener((v) -> {
                Intent i = new Intent(this, ClassCollectionsActivity.class);
                i.putExtra(ClassCollectionsActivity.ARG_CLASS_ID, studentClass.getId());
                startActivity(i);
            });

            binding.btnStudents.setOnClickListener((v) -> {
                Intent i = new Intent(this, ClassStudentActivity.class);
                i.putExtra(ClassCollectionsActivity.ARG_CLASS_ID, studentClass.getId());
                startActivity(i);
            });

            binding.btnShareClass.setOnClickListener((v) ->
                ShareUrls.shareClass(this, this.studentClass.getId(), this.studentClass.getName())
            );
        } else {
            binding.btnCollections.setVisibility(View.GONE);
            binding.btnStudents.setVisibility(View.GONE);
            binding.btnShareClass.setVisibility(View.GONE);
        }

        binding.chartStudents.setVisibility(View.GONE);
        Common.setChartStyle(binding.chartStudents, true);

        Stat.getClassStats(
                studentClass,
                usersStats -> {
                    if (usersStats.size() == 0) {
                        binding.txtLoadingChart.setText(Common.resStr(ClassDetailsActivity.this, R.string.basic_chart_no_data));
                        return;
                    }
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<>();
                    int i = 0;
                    for (String studentName : usersStats.keySet()) {
                        StatData sd = usersStats.get(studentName);
                        entries.add(new BarEntry(i++, new float[] { sd.getCorrect(), sd.getWrong()  }, studentName));
                        labels.add(studentName);
                    }

                    BarDataSet set = new BarDataSet(entries, "Students");
                    set.setBarBorderWidth(0.1f);
                    set.setColors(new int[] { R.color.rememo_primary, R.color.error_red }, ClassDetailsActivity.this);
                    BarData data = new BarData(set);
                    data.setHighlightEnabled(false);
                    binding.chartStudents.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    Common.setBarChartStyle(binding.chartStudents);
                    binding.chartStudents.setData(data);
                    binding.chartStudents.invalidate();
                    binding.chartStudents.setVisibility(View.VISIBLE);
                    binding.txtLoadingChart.setVisibility(View.GONE);
                },
                ex -> {}
        );

    }
}