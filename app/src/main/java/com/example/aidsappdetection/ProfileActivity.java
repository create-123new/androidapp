package com.example.aidsappdetection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etAge, etContact, etbloodgroup;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale, rbOther;
    private Button btnSave;
    private Button btnLogOut;

   // @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etContact = findViewById(R.id.etContact);
        etbloodgroup = findViewById(R.id.etBloodGroup);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
        btnSave = findViewById(R.id.btnSave);

        btnLogOut = findViewById(R.id.logout);

        loadData();
        // Set a click listener on the log out button
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        // Set a click listener for the save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

    }

    private void loadData() {
        SharedPreferences sharedPreferences=getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String name=sharedPreferences.getString(email+"name","");
        etName.setText(name);
        String age=sharedPreferences.getString(email+"age","");
        etAge.setText(age);
        String contact=sharedPreferences.getString(email+"contact","");
        etContact.setText(contact);
        String bloodGroup=sharedPreferences.getString(email+"blood","");
        etbloodgroup.setText(bloodGroup);
        String gender=sharedPreferences.getString(email+"gender","");
        for (int i = 0; i < rgGender.getChildCount(); i++) {
            View view = rgGender.getChildAt(i);
            if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                if (radioButton.getText().toString().equals(gender)) {
                    radioButton.setChecked(true); // restore selection
                    break;
                }
            }
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String bloodGroup = etbloodgroup.getText().toString().trim();
        String gender = getSelectedGender();

        if (name.isEmpty() || age.isEmpty() || contact.isEmpty() || bloodGroup.isEmpty() || gender.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Save patient profile logic here (e.g., to a database or API)
            SharedPreferences Profilepref = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            String email = Profilepref.getString("email", null);
            SharedPreferences.Editor editor = Profilepref.edit();
            editor.putString(email+"name", name);
            editor.putString(email+"age",age);
            editor.putString(email+"contact",contact);
            editor.putString(email+"blood",bloodGroup);
            editor.putString(email+"gender",gender);
            editor.apply();
            Toast.makeText(ProfileActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
        }
    }


    private String getSelectedGender() {
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == rbMale.getId()) {
            return "Male";
        } else if (selectedId == rbFemale.getId()) {
            return "Female";
        } else if (selectedId == rbOther.getId()) {
            return "Other";
        }
        return "";
    }

    private void logOut() {
        // Clear the stored user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        // Optionally, you can show a Toast message indicating successful logout
        Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        // Redirect the user back to the login screen
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class); // Assuming LoginActivity is your login screen
        startActivity(intent);
        finish(); // Optional: finish the current activity so the user can't go back to it using the back button
    }


}



