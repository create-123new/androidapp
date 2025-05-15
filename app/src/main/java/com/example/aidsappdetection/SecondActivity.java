package com.example.aidsappdetection;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class SecondActivity extends AppCompatActivity {

    private ImageView displayedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        displayedImageView = findViewById(R.id.imageView);

        String imagePath = getIntent().getStringExtra("imagePath");
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                displayedImageView.setImageBitmap(bitmap);
            } else {
                // Handle case where the cached file is not found
                displayedImageView.setImageResource(android.R.drawable.ic_menu_gallery); // Placeholder
            }
        } else {
            // Handle case where no image path is received
            displayedImageView.setImageResource(android.R.drawable.ic_menu_gallery); // Placeholder
        }
    }
}