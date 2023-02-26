package com.example.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.firebaseauth.databinding.ActivityPhoneSignInBinding;

public class PhoneSignInActivity extends AppCompatActivity {
ActivityPhoneSignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPhoneSignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ccp.registerCarrierNumberEditText(binding.mobileNoEt);

        binding.getOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhoneSignInActivity.this,OtpVerificationActivity.class);
                intent.putExtra("mobileNo",binding.ccp.getFullNumberWithPlus().trim());
                startActivity(intent);
                finish();
            }
        });
    }
}