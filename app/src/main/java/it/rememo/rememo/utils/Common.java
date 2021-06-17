package it.rememo.rememo.utils;

import android.content.Context;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.rememo.rememo.R;

public class Common {
    private static String userId = null;
    public static String getUserId() {
        if (Common.userId == null) {
            Common.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return userId;
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

    public static LineChart setChartStyle(LineChart chart) {
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

}
