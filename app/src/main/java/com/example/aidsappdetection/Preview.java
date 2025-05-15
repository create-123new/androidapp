package com.example.aidsappdetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aidsappdetection.api.FastApiService;
import com.example.aidsappdetection.api.PredictionRequest;
import com.example.aidsappdetection.api.PredictionResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Preview extends AppCompatActivity {

    private ImageView reportPreviewImage;
    private EditText wbcInput, lymphAbsInput, lymphPercentInput;
    private Button submitBtn;
    private AdapterView<Adapter> wbcUnitSpinner;
    private AdapterView<Adapter> lymphAbsUnitSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        wbcUnitSpinner = findViewById(R.id.wbcUnitSpinner);
        lymphAbsUnitSpinner = findViewById(R.id.lymphAbsUnitSpinner);
        reportPreviewImage = findViewById(R.id.reportPreviewImage);
        wbcInput = findViewById(R.id.wbcInput);
        lymphAbsInput = findViewById(R.id.lymphAbsInput);
        lymphPercentInput = findViewById(R.id.lymphPercentInput);
        submitBtn = findViewById(R.id.submitBtn);

        // Get URI passed from UploadActivity
        Intent intent = getIntent();
        Uri imageUri = intent.getParcelableExtra("imageUri");

        if (imageUri != null) {
            reportPreviewImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
        }

        // Handle Submit
        submitBtn.setOnClickListener(v -> {
            try {
                String wbcText = wbcInput.getText().toString().trim();
                String lymphAbsText = lymphAbsInput.getText().toString().trim();
                String lymphPercentText = lymphPercentInput.getText().toString().trim();

                // Validation: WBC must not be empty
                if (wbcText.isEmpty()) {
                    Toast.makeText(this, "WBC count is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validation: Either lymphAbs or lymphPercent must be provided, not both empty
                if (lymphAbsText.isEmpty() && lymphPercentText.isEmpty()) {
                    Toast.makeText(this, "Provide either Lymphocyte Absolute or Percentage", Toast.LENGTH_SHORT).show();
                    return;
                }

                double wbc = Double.parseDouble(wbcText);
                String wbcUnit = wbcUnitSpinner.getSelectedItem().toString();
                String lymphAbsUnit = lymphAbsUnitSpinner.getSelectedItem().toString();

                // Convert WBC to standard unit
                wbc = convertToCellsPerMicroliter(wbc, wbcUnit);

                double lymphAbs = 0, lymphPercent = 0;

                // If lymphAbs is provided
                if (!lymphAbsText.isEmpty()) {
                    lymphAbs = Double.parseDouble(lymphAbsText);
                    lymphAbs = convertToCellsPerMicroliter(lymphAbs, lymphAbsUnit);

                    // If lymphPercent is empty, calculate and autofill
                    if (lymphPercentText.isEmpty()) {
                        lymphPercent = (lymphAbs / wbc) * 100.0;
                        lymphPercentInput.setText(String.format("%.2f", lymphPercent));
                    } else {
                        lymphPercent = Double.parseDouble(lymphPercentText);
                    }

                } else {
                    // lymphAbs is empty but percent is given, so calculate abs and autofill
                    lymphPercent = Double.parseDouble(lymphPercentText);
                    lymphAbs = wbc * lymphPercent / 100.0;
                    lymphAbsInput.setText(String.format("%.2f", lymphAbs));
                }

                // Log CD marker values
                double cd40 = calculateCD4(wbc, lymphAbs);
                double cd420 = calculateCD420(wbc, lymphAbs);
                double cd80  = calculateCD80(wbc, lymphAbs);
                double cd820 = calculateCD820(wbc, lymphAbs);

                Log.d("CDMarkers", "CD4: " + cd40 + ", CD420: " + cd420 +
                        ", CD8: " + cd80 + ", CD820: " + cd820);


                Toast.makeText(this, "Values Submitted", Toast.LENGTH_SHORT).show();

                PredictionRequest request = new PredictionRequest((float) cd40, (float) cd420, (float) cd80, (float) cd820);

                FastApiService service = RetrofitClient.getFastApiService();

                service.getPrediction(request).enqueue(new Callback<PredictionResult>() {
                    @Override
                    public void onResponse(Call<PredictionResult> call, Response<PredictionResult> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Move to ResultActivity
                            PredictionResult result = response.body();
                            Intent intent = new Intent(Preview.this, ResultActivity.class);
                            intent.putExtra("cd4", (float)cd40);
                            intent.putExtra("hiv_stage", result.getHIVStage());
                            intent.putExtra("health_status", result.getHealthStatus());
                            startActivity(intent);
                        } else {
                            Toast.makeText(Preview.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PredictionResult> call, Throwable t) {
                        Toast.makeText(Preview.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



                // Optionally: send to next activity or API

            } catch (Exception e) {
                Toast.makeText(this, "Error: Invalid input", Toast.LENGTH_SHORT).show();
                Log.e("SubmitError", e.getMessage());
            }
        });
    }


        // Helper function to convert units to cells/mcL
    public double convertToCellsPerMicroliter(double value, String unit) {
        if (unit.equals("cells/mm³")) {
            return value * 1; // No conversion needed for cells/mm³ (same as cells/mcL)
        } else if (unit.equals("x10³/µL")) {
            return value * 1000; // Convert from x10³/µL to cells/mcL
        }
        return value;
    }

    // Calculate CD4 = 50% of Lymphocyte Abs
    public double calculateCD4(double wbc, double lymphAbs) {
        return lymphAbs * 0.50; // 50% of total lymphs
    }

    // CD420 = 30% of Lymphocyte Abs
    public double calculateCD420(double wbc, double lymphAbs) {
        return lymphAbs * 0.30;
    }

    // CD8 = 30% of Lymphocyte Abs
    public double calculateCD80(double wbc, double lymphAbs) {
        return lymphAbs * 0.30;
    }

    // CD820 = 15% of Lymphocyte Abs
    public double calculateCD820(double wbc, double lymphAbs) {
        return lymphAbs * 0.15;
    }


}
