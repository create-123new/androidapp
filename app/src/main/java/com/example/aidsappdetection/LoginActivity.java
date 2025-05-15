package com.example.aidsappdetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aidsappdetection.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    int loginAttempts = 0;
    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbh=new DatabaseHelper(this);

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                if (email.equals("") || password.equals(""))
                    Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                else {
                    Boolean checkCredentials = dbh.checkEmailPassword(email, password);

                    if (checkCredentials == true) {
                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("email",email); // also save email for future use
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        loginAttempts++;

                        if (loginAttempts == 1) {
                            binding.forgotPassword.setVisibility(View.VISIBLE);

                        }
                    }
                }
            }
            });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String email = binding.loginEmail.getText().toString();
                    dbh.deleteUser(email);  // remove from DB
                    Toast.makeText(LoginActivity.this, "User deleted. Please Sign Up again.", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(i);
                    finish();
            }
        });


        binding.SignupRedirectText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
            });
        }
    }