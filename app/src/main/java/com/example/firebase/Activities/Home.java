package com.example.firebase.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebase.Fragments.HomeFragment;
import com.example.firebase.Fragments.ProfileFragment;
import com.example.firebase.Fragments.SettingsFragment;
import com.example.firebase.Models.Post;
import com.example.firebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PReqCode = 2 ;
    private static final int REQUESCODE = 2;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    ImageView popUpUserImage, popUpPostImage, popUpAddBtn;
    TextView etTitle, etDescription;
    ProgressBar popUpProgressBar;

    Dialog popAddPost;
    private Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // ini popup
        iniPopup();

        setupPopUpImageClicked();

        getSupportFragmentManager().beginTransaction().replace(R.id.container , new  HomeFragment()).commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
    }

    // when user clicked on the popUpImage
    private void setupPopUpImageClicked() {

        popUpPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });

    }

    // check user permission
    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

               showMessage("Please Accept For Required Permission");
            } else {

                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }
    }

    // when user clicked on popUpPostImage we open the Gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);

    }

    // sample Method to show Toast message
    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }

    // when user picked an Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        assert data != null;
        pickedImgUri = data.getData();
        popUpPostImage.setImageURI(pickedImgUri);
    }

    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_edittext);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //ini widgets
        popUpPostImage = popAddPost.findViewById(R.id.popup_image);
        popUpUserImage = popAddPost.findViewById(R.id.popUp_userPhoto);
        etDescription = popAddPost.findViewById(R.id.popUp_Description);
        etTitle = popAddPost.findViewById(R.id.popUp_Title);
        popUpAddBtn = popAddPost.findViewById(R.id.popup_add);
        popUpProgressBar = popAddPost.findViewById(R.id.popup_progressBar);

        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popUpUserImage);

        // add post click listener
        popUpAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUpAddBtn.setVisibility(View.INVISIBLE);
                popUpProgressBar.setVisibility(View.VISIBLE);

                // Testing all input fields
                if (!etTitle.getText().toString().isEmpty()
                        && !etDescription.getText().toString().isEmpty()
                        && pickedImgUri != null) {
                    // everything is ok
                    // TODO Create a post Object and add it to fireBase dataBase
                    // first we need to upload the image
                    // access fireBase Storage

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_image");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imageDownloadLink = uri.toString();

                                    //create post Object
                                    Post post = new Post(etTitle.getText().toString() ,
                                            etDescription.getText().toString() ,
                                            imageDownloadLink ,
                                            currentUser.getUid() ,
                                            currentUser.getPhotoUrl().toString());

                                    // add post to fireBase dataBase
                                    addPost(post);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    // something went wrong while uploading the picture
                                    showMessage(e.getMessage());
                                    popUpProgressBar.setVisibility(View.INVISIBLE);
                                    popUpAddBtn.setVisibility(View.VISIBLE);

                                }
                            });
                        }
                    });
                } else {
                    showMessage("Please Verify All Input Fields And Choose Post Image");
                    popUpAddBtn.setVisibility(View.VISIBLE);
                    popUpProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addPost(Post post) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Posts").push();

        // get post unique id and upload key
        String key = databaseReference.getKey();
        post.setPostKey(key);

        // add post to fireBase DataBase
        databaseReference.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added Successfully");
                popUpProgressBar.setVisibility(View.INVISIBLE);
                popUpAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getSupportActionBar().setTitle("Home");

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        } else if (id == R.id.nav_profile) {

            getSupportActionBar().setTitle("Profile");

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();

        } else if (id == R.id.nav_settings) {

            getSupportActionBar().setTitle("Sittings");

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();

        } else if (id == R.id.nav_SignOut) {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateNavHeader() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_userName = headerView.findViewById(R.id.nav_userName);
        TextView nav_userEmail = headerView.findViewById(R.id.nav_userEmail);
        ImageView nav_userPhoto = headerView.findViewById(R.id.nav_userPhoto);

        nav_userEmail.setText(currentUser.getEmail());
        nav_userName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(nav_userPhoto);

    }
}
