package com.example.firebase.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebase.Adapters.CommentAdapter;
import com.example.firebase.Adapters.PostAdapter;
import com.example.firebase.Models.Comment;
import com.example.firebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {

    ImageView imgPost , imgUserPost , imgCurrentUser ;
    TextView txtPostDesc , txtPostDateName , txtPostTitle ;
    EditText editTextComment ;
    Button btnAddComment ;

    String postKey ;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    RecyclerView comment_RecyclerView;
    CommentAdapter commentAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference ;
    LinearLayoutManager linearLayoutManager ;

    List<Comment> commentList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detalis);

        // set the statue bar int transparent
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS , WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getSupportActionBar().hide();

        imgPost = findViewById(R.id.post_Details_Image);
        imgUserPost = findViewById(R.id.post_Details_User_Image);
        imgCurrentUser = findViewById(R.id.post_Details_CurrentUser_Img);

        txtPostTitle = findViewById(R.id.post_Details_Title);
        txtPostDesc = findViewById(R.id.post_Details_Description);
        txtPostDateName = findViewById(R.id.post_details_date_name);

        editTextComment = findViewById(R.id.post_Details_Write_Comment);

        btnAddComment = findViewById(R.id.post_Details_Add_Comment);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        comment_RecyclerView = findViewById(R.id.RecyclerView_Comments);
        comment_RecyclerView.setLayoutManager(linearLayoutManager);
        comment_RecyclerView.setHasFixedSize(true);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = firebaseDatabase.getReference("Comment" ).child(postKey).push();
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();

                Comment comment = new Comment(comment_content , uid , uimg , uname);

                databaseReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        showMessage("Comment Added");
                        editTextComment.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Fail to Add Your Comment : " + e.getMessage());
                    }
                });
            }
        });

        // now we need to bind all data into those views
        // first we need to get post data
        // we need to send post details data to this activity first ...

        databaseReference = firebaseDatabase.getReference("Comment");


        String postImage = getIntent().getExtras().getString("PostImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("Description");
        txtPostDesc.setText(postDescription);

        // set Comment user image
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);

        // get post id
        postKey = getIntent().getExtras().getString("PostKey");

        String date = timeStampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // get list of comment from database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                commentList = new ArrayList<>();
                for (DataSnapshot commentSnap : dataSnapshot.getChildren()) {
                    Comment comment = commentSnap.getValue(Comment.class);
                    commentList.add(comment);

                }

                commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
                comment_RecyclerView.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String message) {

        Toast.makeText(this, message , Toast.LENGTH_LONG).show();
    }

    private String timeStampToString (long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy" , calendar).toString();
        return date;

    }
}
