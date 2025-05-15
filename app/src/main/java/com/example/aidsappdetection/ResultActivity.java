package com.example.aidsappdetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Float cd40 = getIntent().getFloatExtra("cd4", 0);
        String stage = getIntent().getStringExtra("hiv_stage");
        String status = getIntent().getStringExtra("health_status");

        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(email+"cd4", cd40);
        editor.putString(email+"HIVStage", stage);
        editor.putString(email+"healthStatus", status);
        editor.putString(email+"date", getTodayDate());

        // Update CD4 history
        String oldHistory = sharedPreferences.getString(email+"cd4_history", "");
        String newEntry = cd40 + "," + getTodayDate();

// Prevent duplicate entry for the same day
        if (!oldHistory.contains(newEntry)) {
            String updatedHistory = oldHistory.isEmpty() ? newEntry : oldHistory + ";" + newEntry;
            editor.putString(email+"cd4_history", updatedHistory);
        }
        editor.apply();


        TextView statusView = findViewById(R.id.statusText);
        TextView stageView = findViewById(R.id.stageText);
        Button chartBtn = findViewById(R.id.chartBtn);

        statusView.setText("Health Status: " + status);
        stageView.setText("HIV Stage: " + stage);

        chartBtn.setOnClickListener(v -> {
            Intent i = new Intent(ResultActivity.this, progressActivity.class);
            startActivity(i);
        });
    }
    public String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }


}
