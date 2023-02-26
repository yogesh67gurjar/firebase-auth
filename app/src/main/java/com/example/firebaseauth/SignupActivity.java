package com.example.firebaseauth;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

import com.example.firebaseauth.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    private FirebaseAuth auth;
    String fullName, mobile, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.goToLogInTv.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }

        binding.signupBtn.setOnClickListener(v -> {
            if (binding.fullnameEt.getText().toString().isEmpty()) {
                binding.fullnameEt.setError("enter name");
                binding.fullnameEt.requestFocus();
            } else if (binding.emailEt.getText().toString().isEmpty()) {
                binding.emailEt.setError("enter email");
                binding.emailEt.requestFocus();
            } else if (binding.mobileEt.getText().toString().isEmpty()) {
                binding.mobileEt.setError("enter mobile no");
                binding.mobileEt.requestFocus();
            } else if (binding.passwordEt.getText().toString().isEmpty()) {
                binding.passwordEt.setError("enter password");
                binding.passwordEt.requestFocus();
            } else {
                createUserWithEmailPassword();
            }
        });
    }

    private void createUserWithEmailPassword() {
        fullName = binding.fullnameEt.getText().toString();
        email = binding.emailEt.getText().toString();
        mobile = binding.mobileEt.getText().toString();
        password = binding.passwordEt.getText().toString();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
            if (task.isSuccessful()) {
                Log.i("signup email password", "Success");
                FirebaseUser user = auth.getCurrentUser();
                updateUI(user);

            } else {
                Log.i("signup email password", "Failed");
                Log.i("signup email password", Objects.requireNonNull(task.getException()).toString());
                Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
        }
    }

    private void reload() {
    }
}