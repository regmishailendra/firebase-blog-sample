package com.example.shailendra.appfire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class PostActivity extends AppCompatActivity {
private ImageView imageView;
    private ImageButton imagebutton;
    private EditText editText,editText2;
    private static int g=2;
    private Button button;
   private Uri uri=null;
   private StorageReference mstoragereference;
   private DatabaseReference mdatabasereference;
   private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mdatabaseforname;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        editText=(EditText)findViewById(R.id.editText);
        editText2=(EditText)findViewById(R.id.editText2);
        imagebutton=(ImageButton)findViewById(R.id.imageButton);
        button=(Button)findViewById(R.id.button);
        mstoragereference= FirebaseStorage.getInstance().getReference();
        mdatabasereference= FirebaseDatabase.getInstance().getReference().child("Blog");
        progressDialog= new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        mdatabaseforname= FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        imagebutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                       startActivityForResult(intent,g);
                    }
                }
        );


        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progressDialog.setCancelable(false);

                         startPosting();
                    }
                }
        );
    }

    private void startPosting() {

      final String title=editText.getText().toString();
        final String body=editText2.getText().toString();

        if(!title.isEmpty() && !body.isEmpty() && uri!=null ) {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            progressDialog.setCancelable(false);

            StorageReference filereference = mstoragereference.child("Blog_Images").child(uri.getLastPathSegment());
            filereference.putFile(uri).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            final DatabaseReference newPost = mdatabasereference.push();
                            final Uri linkurl = taskSnapshot.getDownloadUrl();


mdatabaseforname.addValueEventListener(
        new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newPost.child("title").setValue(title);
                newPost.child("description").setValue(body);
                newPost.child("Uid").setValue(mCurrentUser.getUid());
                newPost.child("image").setValue(linkurl.toString());
                newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(PostActivity.this,MainActivity.class));
                            }
                        }
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }
);




                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Posted to your blog", Toast.LENGTH_LONG).show();

                        }
                    }
            );
        }
         else {
            Toast.makeText(PostActivity.this, "Please input all three fields.", Toast.LENGTH_LONG).show();


        }







    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==g && resultCode==RESULT_OK)
        {
             uri= data.getData();
            imagebutton.setImageURI(uri);
        }



        super.onActivityResult(requestCode, resultCode, data);
    }
}
