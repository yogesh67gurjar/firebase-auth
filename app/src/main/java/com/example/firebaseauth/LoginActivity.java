package com.example.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.firebaseauth.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private static final String TAG = "GoogleSignin";
    private static final int GOOGLE_SIGNIN_REQUEST_CODE = 1001;
    String email,password;


    FirebaseAuth auth;
    GoogleSignInClient gsc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);


        binding.goToSignUpTv.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            finish();
        });

        auth=FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=auth.getCurrentUser();
        if(currentUser!=null)
        {
            updateUI(currentUser);
        }

        binding.loginBtn.setOnClickListener(v -> {
            if(binding.emailEt.getText().toString().isEmpty())
            {
                binding.emailEt.setError("enter email");
                binding.emailEt.requestFocus();
            }

            else if(binding.passwordEt.getText().toString().isEmpty())
            {
                binding.passwordEt.setError("enter password");
                binding.passwordEt.requestFocus();
            }
            else
            {
                SignInWithEmailPassword();
            }
        });

        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = gsc.getSignInIntent();
                startActivityForResult(ii, GOOGLE_SIGNIN_REQUEST_CODE);
            }
        });

    }

    private void SignInWithEmailPassword() {
        email=binding.emailEt.getText().toString();
        password=binding.passwordEt.getText().toString();

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, task -> {
            if(task.isSuccessful())
            {
                Log.d("SignInWithEmailPw", "success");
                FirebaseUser user = auth.getCurrentUser();
                updateUI(user);
            }
            else
            {
                Log.w("SignInWithEmailPw", "failed", task.getException());
                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GOOGLE_SIGNIN_REQUEST_CODE)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Log.d(TAG, "success");
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                }
                else
                {
                    updateUI(null);
                    Log.w(TAG, Objects.requireNonNull(task.getException()).toString());
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null)
        {
            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
    private void reload() { }
}