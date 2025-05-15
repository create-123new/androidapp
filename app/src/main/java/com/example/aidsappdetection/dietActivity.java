package com.example.aidsappdetection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class dietActivity extends AppCompatActivity {

    TextView healthStatusText, morningDiet, afternoonDiet, eveningDiet, nightDiet, quoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        // Initialize views
        healthStatusText = findViewById(R.id.healthStatusText);
        morningDiet = findViewById(R.id.morningDiet);
        afternoonDiet = findViewById(R.id.afternoonDiet);
        eveningDiet = findViewById(R.id.eveningDiet);
        nightDiet = findViewById(R.id.nightDiet);
        quoteText = findViewById(R.id.quoteText); // Make sure this ID exists in XML for quotation

        // Get health status from Intent
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String healthstatus = sharedPreferences.getString(email+"healthStatus", "Unknown");

        if (healthstatus == null) healthstatus = "Unknown";  // Default fallback

        // Set health status in UI
        healthStatusText.setText("Health Status: " + healthstatus);

        // Set diet and quotation according to health status
        switch (healthstatus) {
            case "Improved":
                morningDiet.setText("Oatmeal with fruits + 1 boiled egg + green tea");
                afternoonDiet.setText("Brown rice + grilled chicken/fish + salad + buttermilk");
                eveningDiet.setText("Sprouts + fruit juice or herbal tea");
                nightDiet.setText("Vegetable soup + multigrain roti + paneer or tofu + light salad");
                quoteText.setText("“Progress is progress, no matter how small.”");
                break;

            case "Stable":
                morningDiet.setText("Cornflakes with milk + banana + tea/coffee");
                afternoonDiet.setText("Rice or roti + dal + seasonal vegetables + curd");
                eveningDiet.setText("Biscuit + milk or roasted chana");
                nightDiet.setText("Khichdi or daliya + boiled vegetables + curd");
                quoteText.setText("“Stability is not a weakness. It’s a strength.”");
                break;

            case "Decline":
                morningDiet.setText("Suji upma or poha + boiled egg (optional) + warm water");
                afternoonDiet.setText("Soft khichdi + mashed veggies + curd");
                eveningDiet.setText("Boiled moong or lentil soup");
                nightDiet.setText("Oats porridge + boiled veggies or scrambled paneer");
                quoteText.setText("“Every setback is a setup for a comeback.”");
                break;

            default:
                // Default case for first-time users or invalid input
                morningDiet.setText("Boiled eggs + toast + fruit juice");
                afternoonDiet.setText("Roti/rice + dal + seasonal vegetables + salad");
                eveningDiet.setText("Fruits or nuts + green tea");
                nightDiet.setText("Light dal soup + boiled vegetables + roti or oats");
                quoteText.setText("“Your health is an investment, not an expense.” *Default plan shown. Please upload your report to get personalized suggestions.*");
                break;

        }
    }
}



