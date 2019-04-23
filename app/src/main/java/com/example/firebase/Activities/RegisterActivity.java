package com.example.firebase.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView ivPhoto;
    EditText etName, etMail, etPassword, etConfirmPassword;
    Button btnRegister;
    ProgressBar progressBar;

    Uri pickedImageUri;

    static int PReqCode = 1;
    private final static int REQUESCODE = 100;

    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /**
         * linking all of :
         * 1- userName
         * 2- userEmail
         * 3- Password
         * 4- Confirm Password
         * 5- register button
         * 5- userPhoto
         *
         * with ID in XML
         */
        ivPhoto = findViewById(R.id.ivUserPhoto);
        etName = findViewById(R.id.etName);
        etMail = findViewById(R.id.et_LogIn_Email);
        etPassword = findViewById(R.id.et_Login_Password);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        progressBar = findViewById(R.id.progressBar);
        btnRegister = findViewById(R.id.btnLogin);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

         // set the title of the activity
        setTitle("Create An Account");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnRegister.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                final String name = etName.getText().toString();
                final String email = etMail.getText().toString();
                final String password = etPassword.getText().toString();
                final String ConfirmPassword = etConfirmPassword.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || !password.equals(ConfirmPassword)) {
                    /*
                    something went wrong : all fields must be filled
                    we need to display Error message .
                     */
                    showMessage("Please Verify All Fields");
                    btnRegister.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                } else {
                    /*
                     everyThing is ok , now we can start creating userAccount
                     CreateUserAccount method going to try creating an account
                     if the email is valid .
                     */
                    CreateUserAccount(name , email , password);
                }

            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });
    }

    private void CreateUserAccount(final String name, String email, String password) {
         // this method create user account with specific email and password
        mAuth.createUserWithEmailAndPassword(email ,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // account created successfully
                            showMessage("Account Created");
                            // after account created we need to update his anther info
                            updateUserInfo(name , pickedImageUri ,mAuth.getCurrentUser());

                        } else {
                            // account creation filled
                            showMessage("Account Creation Filled " + task.getException().getMessage());
                            btnRegister.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

    }

    // this method responsible of updating userInfo
    private void updateUserInfo(final String name, final Uri pickedImageUri, final FirebaseUser currentUser) {

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = storageReference.child(pickedImageUri.getLastPathSegment());


        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image successfully uploaded
                // new we can get image uri
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                                .Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // user info update successfully
                                            showMessage("Register Complete");
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateUI() {

        Intent HomeActivity = new Intent(getApplicationContext(), Home.class);
        startActivity(HomeActivity);
        finish();

    }


    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Please Accept For Required Permission", Toast.LENGTH_SHORT).show();
            } else {

                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        assert data != null;
        pickedImageUri = data.getData();
        ivPhoto.setImageURI(pickedImageUri);
    }

    // sample Method to show Toast message
    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }
}
