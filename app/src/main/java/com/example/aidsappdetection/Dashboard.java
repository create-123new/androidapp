package com.example.aidsappdetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Dashboard extends AppCompatActivity {
    private TextView hivStageTextView, cd4CountTextView;
    CardView mycard,mycard2,mycard3,mycard4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hivStageTextView = findViewById(R.id.stage);
        cd4CountTextView = findViewById(R.id.cdcount);
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String hivStage = sharedPreferences.getString(email+"HIVStage", "Unknown");
        String status = sharedPreferences.getString(email+"healthStatus", "Unknown");

        hivStageTextView.setText(hivStage);
        cd4CountTextView.setText(status);

        TextView conditionTextView = findViewById(R.id.condition);

        if (status.equalsIgnoreCase("Stable")) {
            conditionTextView.setText("Condition is Good");
        } else if (status.equalsIgnoreCase("Decline")) {
            conditionTextView.setText("Condition is Worsening");
        } else if (status.equalsIgnoreCase("Improved")) {
            conditionTextView.setText("Condition is Excellent");
        } else {
            conditionTextView.setText("Condition Unknown");
        }


        mycard=findViewById(R.id.card1);
        mycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),uploadActivity.class);
                startActivity(intent);
            }
        });

        mycard2=findViewById(R.id.card2);
        mycard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),progressActivity.class);
                startActivity(intent);
            }
        });

        mycard3=findViewById(R.id.card3);
        mycard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),dietActivity.class);
                startActivity(intent);
            }
        });

        mycard4=findViewById(R.id.card4);
        mycard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        ImageView chatbotIcon = findViewById(R.id.chatbotImage);

        chatbotIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, chatbot.class);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Re-fetch SharedPreferences every time user comes back to Dashboard
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String hivStage = sharedPreferences.getString(email+"HIVStage", "Unknown");
        String status = sharedPreferences.getString(email+"healthStatus", "Unknown");

        hivStageTextView.setText(hivStage);
        cd4CountTextView.setText(status);

        TextView conditionTextView = findViewById(R.id.condition);

        if (status.equalsIgnoreCase("Stable")) {
            conditionTextView.setText("Condition is Good");
        } else if (status.equalsIgnoreCase("Decline")) {
            conditionTextView.setText("Condition is Worsening");
        } else if (status.equalsIgnoreCase("Improved")) {
            conditionTextView.setText("Condition is Excellent");
        } else {
            conditionTextView.setText("Condition Unknown");
        }
    }

}