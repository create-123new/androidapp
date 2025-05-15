package com.example.aidsappdetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aidsappdetection.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d("error", "onCreate:pass ");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbh = new DatabaseHelper(this);
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=binding.signupEmail.getText().toString();
                String password=binding.signupPassword.getText().toString();
                String confirmPassword=binding.signupConfirm.getText().toString();

                if(email.equals("")||password.equals("")||confirmPassword.equals(""))
                    Toast.makeText(SignupActivity.this,"All fields are mandatory", Toast.LENGTH_SHORT).show();
                else{
                    if(password.equals(confirmPassword)){
                        Boolean checkUserEmail = dbh.checkEmail(email);
                        if(checkUserEmail==false){
                            Boolean insert = dbh.insertData(email,password);
                            if(insert==true){
                                Toast.makeText(SignupActivity.this,"Signup Successfully",Toast.LENGTH_SHORT).show();
                                SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.putString("email",email); // also save email for future use
                                editor.apply();
                                Intent intent = new Intent(getApplicationContext(),Dashboard.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(SignupActivity.this,"Signup Failed",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(SignupActivity.this,"User Already Exists, please login",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(SignupActivity.this,"Invalid Password",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}