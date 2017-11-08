package com.example.shailendra.appfire;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    FirebaseAuth mauth;
    private EditText uname;
    private EditText upass;
    private ListView listView;
    private Button button;
    private StorageReference mstorage;
    private FirebaseAuth.AuthStateListener mauthListener;
    private static int GALLERY_INTENT=2;
    ProgressDialog mpogressdialog;
    private RecyclerView mylist;
    private DatabaseReference databaseReference;
    private DatabaseReference likedatabase;
    private DatabaseReference userdabasereference;
    private boolean likeCLick=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mylist=(RecyclerView)findViewById(R.id.myView);
        mylist.setHasFixedSize(true);
        mylist.setLayoutManager(new LinearLayoutManager(this));

        mauth=FirebaseAuth.getInstance();
        mauthListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent loginIntent= new Intent(MainActivity.this,Signin.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);



                }

            }
        };


        databaseReference=FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseReference.keepSynced(true);
        userdabasereference=FirebaseDatabase.getInstance().getReference().child("Users");
        userdabasereference.keepSynced(true);
        likedatabase=FirebaseDatabase.getInstance().getReference().child("Likes");
        likedatabase.keepSynced(true);

    }




    @Override
    protected void onStart() {
        super.onStart();
      //  checkuserexist();
        mauth.addAuthStateListener(mauthListener);

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.row,
                BlogViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, final Blog model, int position) {
             final String key= getRef(position).getKey();


                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeButton( key);



                viewHolder.likeButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                likeCLick=true;

                                likedatabase.addValueEventListener(
                                        new com.google.firebase.database.ValueEventListener() {


                                            @Override
                                            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                                                if (likeCLick) {
                                                    if (dataSnapshot.child(key).hasChild(mauth.getCurrentUser().getUid())) {
                                                        likedatabase.child(key).child(mauth.getCurrentUser().getUid()).removeValue();
                                                        likeCLick = false;
                                                    } else {
                                                        likedatabase.child(key).child(mauth.getCurrentUser().getUid()).setValue("Random value");
                                                        likeCLick = false;
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        }
                                );

                            }
                        }
                );
                viewHolder.mview.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent detailIntent= new Intent(MainActivity.this,detail.class);
                                detailIntent.putExtra("Blog_id",key);
                                startActivity(detailIntent);
                            }
                        }
                );

            }
        };
 mylist.setAdapter(firebaseRecyclerAdapter);

    }
//    private void checkuserexist() {
//
//        final String userid= mauth.getCurrentUser().getUid();
//
//        userdabasereference.addValueEventListener(
//                new com.google.firebase.database.ValueEventListener() {
//                    @Override
//                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//                        if(!dataSnapshot.hasChild(userid))
//                        {
//                            Intent mainIntent= new Intent(MainActivity.this,AccountActivity.class);
//                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(mainIntent);
//
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                }
//        );
//
//
//    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mview;
        TextView posttitle;
       DatabaseReference  mdatalike;
        private  FirebaseAuth mauth;
        DatabaseReference likedata;
        FirebaseAuth likeauth;

        ImageButton likeButton;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mauth=FirebaseAuth.getInstance();
            mview = itemView;
            likedata=FirebaseDatabase.getInstance().getReference().child("Likes");
            likeauth=FirebaseAuth.getInstance();

            posttitle = (TextView) mview.findViewById(R.id.postTitle);
            likeButton=(ImageButton)mview.findViewById(R.id.likebtn);

          mdatalike= FirebaseDatabase.getInstance().getReference().child("Likes");


            mdatalike.keepSynced(true);



        }

        public void setTitle(String title) {

            posttitle.setText(title);

        }

      public void setDescription(String description)
      {
          TextView postdesc=(TextView)mview.findViewById(R.id.postBody);
          postdesc.setText(description);


      }

        public void setImage(Context ctx, String image)

        {
            ImageView imageView=(ImageView)mview.findViewById(R.id.postInage);
            Picasso.with(ctx).load(image).into(imageView);

        }
        public void setUsername(String userName)
        {
            TextView Username=(TextView)mview.findViewById(R.id.userName);
            Username.setText(userName);


        }


        public void setLikeButton(final String key) {

//          try{
//            likedata.addValueEventListener(
//                    new com.google.firebase.database.ValueEventListener() {
//                        @Override
//                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//
//                          if(dataSnapshot.child(key).hasChild(mauth.getCurrentUser().getUid()))
//                            {
//                                likeButton.setImageResource(R.drawable.greenlike);
//
//
//                            }
//                            else  {
//                                likeButton.setImageResource(R.drawable.graylike);
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    }
//            );}
//          catch (Exception e)
//          {
//              e.printStackTrace();
//
//
//
//          }



        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_add)
        {
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId()==R.id.action_logout)
        {
           logout();
        }



        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mauth.signOut();
    }
}

