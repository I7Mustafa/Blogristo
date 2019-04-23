package com.example.firebase.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText etMail , etPassword ;
    TextView tvForgot ;
    ImageView ivUserPhoto ;
    Button btnLogin , btnRegister;
    ProgressBar pB_Login;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etMail = findViewById(R.id.et_LogIn_Email);
        etPassword = findViewById(R.id.et_Login_Password);
        tvForgot = findViewById(R.id.tvForgot);
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        btnLogin = findViewById(R.id.btnLogin);
        pB_Login = findViewById(R.id.pB_Login);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        setTitle("Log In");

        pB_Login.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pB_Login.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
                btnRegister.setVisibility(View.INVISIBLE);

                final String Email = etMail.getText().toString();
                final String Password = etPassword.getText().toString();

                if (Email.isEmpty() || Password.isEmpty()) {
                    showMessage("Please Verify All Fields");
                    pB_Login.setVisibility(View.INVISIBLE);
                    btnRegister.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                } else {
                    signIn(Email , Password);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , RegisterActivity.class));
            }
        });

    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email , password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            pB_Login.setVisibility(View.VISIBLE);
                            btnLogin.setVisibility(View.INVISIBLE);
                            btnRegister.setVisibility(View.INVISIBLE);
                            updateUI();
                        } else {
                            showMessage(task.getException().getMessage());
                            pB_Login.setVisibility(View.INVISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private void updateUI() {

        Intent HomeActivity = new Intent(getApplicationContext(), Home.class);
        startActivity(HomeActivity);
        finish();
    }

    private void showMessage(String Text) {

        Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // user is already connected so we need direct him directly to home page
            updateUI();
        }

    }
}
