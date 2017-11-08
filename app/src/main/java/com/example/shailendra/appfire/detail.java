package com.example.shailendra.appfire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class detail extends AppCompatActivity {
    private String postKey=null;
    private DatabaseReference mDatabse;

    private ImageView singleimage;
    private TextView singleTitle;
    private TextView singlename;
    private TextView singleDesc;
    private Button button;
    FirebaseAuth mauth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mauth=FirebaseAuth.getInstance();
        mDatabse= FirebaseDatabase.getInstance().getReference().child("Blog");
        button=(Button)findViewById(R.id.delButton);
         postKey= getIntent().getExtras().getString("Blog_id");
       singleTitle=(TextView)findViewById(R.id.singleTitle);
        singlename=(TextView)findViewById(R.id.singleName);
        singleDesc=(TextView)findViewById(R.id.singleBody);
        singleimage=(ImageView) findViewById(R.id.singleImage);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabse.child(postKey).removeValue();
                        Intent backIntent= new Intent(detail.this,MainActivity.class);
                        startActivity(backIntent);
                    }
                }
        );


        mDatabse.child(postKey).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String postTitle= (String) dataSnapshot.child("title").getValue();
                        String postDesc= (String) dataSnapshot.child("description").getValue();
                        String postImage= (String) dataSnapshot.child("image").getValue();
                       String postUserid= (String) dataSnapshot.child("Uid").getValue();
                        String postUsername= (String) dataSnapshot.child("username").getValue();

                        singleTitle.setText(postTitle);
                        singleDesc.setText(postDesc);
                        singlename.setText(postUsername);
                        Picasso.with(detail.this).load(postImage).into(singleimage);
if(mauth.getCurrentUser().getUid().equals(postUserid))
{
    button.setVisibility(View.VISIBLE);


}

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        postKey = getIntent().getExtras().getString("Blog_id");

      //  Toast.makeText(detail.this,postKey,Toast.LENGTH_LONG).show();


    }
}
