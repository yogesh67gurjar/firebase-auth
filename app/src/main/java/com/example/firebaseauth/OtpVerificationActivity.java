package com.example.firebaseauth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseauth.databinding.ActivityOtpVerificationBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {

    String mobileNo;
    ActivityOtpVerificationBinding binding;

    FirebaseAuth auth;
    String otpId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mobileNo = getIntent().getStringExtra("mobileNo");
        auth = FirebaseAuth.getInstance();
        getOtp(mobileNo);

        binding.verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.otpEt.getText().toString().isEmpty()) {
                    Toast.makeText(OtpVerificationActivity.this, "Blank field cannot be processed", Toast.LENGTH_SHORT).show();
                }
                else if(binding.otpEt.getText().toString().length()!=6)
                {
                    Toast.makeText(OtpVerificationActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(otpId,binding.otpEt.getText().toString());
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }
        });

    }

    private void getOtp(String mobileNo) {
        if (mobileNo != null) {
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(mobileNo)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    signInWithPhoneAuthCredential(phoneAuthCredential);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Log.w("signin failed", e.getMessage());
                                    Toast.makeText(OtpVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    otpId = s;
                                }
                            })
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("phone se signin", "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                        updateUI(user);
                    } else {
                        Log.w("phone se signin", "signInWithCredential:failure", task.getException());

                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(OtpVerificationActivity.this, MainActivity.class));
            finish();
        }
    }

}