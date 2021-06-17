package it.rememo.rememo.utils;

import android.content.Context;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.rememo.rememo.R;

// Common utilities
public class Common {
    private static String userId = null;
    public static String getUserId() {
        if (Common.userId == null) {
            Common.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return userId;
    }

    public static void logout() {
        userId = null;
    }

    public static String resStr(Context context, int id) {
        return context.getResources().getText(id).toString();
    }

    public static void toast(Context ctx, String error) {
        Common.toast(ctx, error, Toast.LENGTH_LONG);
    }
    public static void toast(Context ctx, String error, int lenght) {
        Toast.makeText(ctx, error, lenght).show();
    }

    public static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    public static boolean checkAnswer(String text, String answer) {
        return answer.trim().toLowerCase().equals(text.trim().toLowerCase());
    }

    public static BarLineChartBase setChartStyle(BarLineChartBase chart, boolean showXBar) {
        if (!showXBar)
            chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDescription(null);
        chart.setDrawBorders(false);
        return chart;
    }

    public static LineDataSet setLineDataSetStyle(LineDataSet set, Context ctx) {
        set.setDrawValues(false);
        set.setLineWidth((float)20.0);
        set.setDrawCircles(true);
        set.setDrawCircleHole(false);
        set.setCircleRadius((float)5);
        set.setCircleColors(new int[] { R.color.rememo_dark }, ctx);
        set.setColors(new int[] { R.color.rememo_dark }, ctx);
        return set;
    }

    public static LineData setLineDataStyle(LineData data) {
        data.setHighlightEnabled(false);
        return data;
    }

    public static BarChart setBarChartStyle(BarChart chart) {
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1);
        chart.getXAxis().setGranularityEnabled(true);
        return chart;
    }
}
