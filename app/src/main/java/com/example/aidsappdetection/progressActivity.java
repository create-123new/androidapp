package com.example.aidsappdetection;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class progressActivity extends AppCompatActivity {

    private LineChart lineChart;
    private TextView textViewLastChecked, textViewHealthStatus, textViewAdvice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        lineChart = findViewById(R.id.lineChart);
        textViewLastChecked = findViewById(R.id.textViewLastChecked);
        textViewHealthStatus = findViewById(R.id.textViewHealthStatus);
        textViewAdvice = findViewById(R.id.textViewAdvice);
        Button DietRecommendation=findViewById(R.id.buttonDietRecommendation);

        setupChart();
        updateTextViews();

        DietRecommendation.setOnClickListener(v -> {
            Intent intent = new Intent(progressActivity.this, dietActivity.class);
            startActivity(intent);
        });

    }

    private void setupChart() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);
        String history = prefs.getString(email+"cd4_history", "");

        List<Entry> cd4Entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        if (!history.isEmpty()) {
            String[] reports = history.split(";");
            for (int i = 0; i < reports.length; i++) {
                String[] parts = reports[i].split(",");
                if (parts.length == 2) {
                    try {
                        float cd4 = Float.parseFloat(parts[0]);
                        String date = parts[1];
                        cd4Entries.add(new Entry(i, cd4));
                        labels.add(date);
                    } catch (Exception e) {
                        e.printStackTrace(); // Avoid crashing
                    }
                }
            }
        }

        LineDataSet cd4DataSet = new LineDataSet(cd4Entries, "CD4 Count");
        cd4DataSet.setColor(ContextCompat.getColor(this,R.color.cd4_blue));
        cd4DataSet.setCircleColor(ContextCompat.getColor(this,R.color.cd4_blue));
        cd4DataSet.setLineWidth(2f);
        cd4DataSet.setCircleRadius(4f);
        cd4DataSet.setValueTextSize(10f);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(cd4DataSet);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);

        // X-Axis formatting
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        // Y-Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.getDescription().setText("CD4 Count Progress");
        lineChart.animateX(1000);
        lineChart.invalidate(); // refresh
    }

    private void updateTextViews() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);
        float cd4 = prefs.getFloat(email+"cd4", 0);
        String status = prefs.getString(email+"healthStatus", "Unknown");
        String date = prefs.getString(email+"date", getTodayDate());

        textViewLastChecked.setText("Last Checked: " + date);
        textViewHealthStatus.setText("Health Status: " + status);
        textViewAdvice.setText("Advice: " + getAdviceForHealthStatus(status));
    }

    private String getAdviceForHealthStatus(String status) {
        switch (status) {
            case "Improved":
                return "Keep up your current treatment and lifestyle.";
            case "Stable":
                return "Maintain regular monitoring and healthy habits.";
            case "Decline":
                return "Consult your doctor for necessary adjustments.";
            default:
                return "No advice available.";
        }
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
