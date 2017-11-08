package com.example.shailendra.appfire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signin extends AppCompatActivity {
    private EditText signinname;
    private EditText signinpass;
    private Button signinbutton;
    private Button signinregisterbutton;
    private ProgressDialog progressdialog1;
private static FirebaseAuth mauth;
    private static DatabaseReference userdabasereference;
    private ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signinname=(EditText)findViewById(R.id.signinname);
        signinpass=(EditText)findViewById(R.id.signinpass);
        signinregisterbutton=(Button)findViewById(R.id.signinregister);
        progressdialog1= new ProgressDialog(this);
        signinbutton=(Button) findViewById(R.id.signinbutton);
        mauth=FirebaseAuth.getInstance();
        userdabasereference= FirebaseDatabase.getInstance().getReference().child("Users");
userdabasereference.keepSynced(true); // keeps data in background
        progressdialog= new ProgressDialog(this);


        signinbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginCheck();
                    }
                }
        );

signinregisterbutton.setOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainregisterIntent= new Intent(Signin.this,RegisterActivity.class);
                mainregisterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainregisterIntent);
            }
        }
);

    }

    private void loginCheck() {
        String email= signinname.getText().toString();
        String password= signinpass.getText().toString();
        if(!email.isEmpty() && !password.isEmpty())
        {
            progressdialog.setMessage("Checking details...");
            progressdialog.setCancelable(false);
            progressdialog.show();

            mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                progressdialog.dismiss();
                                  checkuserexist();


                            }
                            else
                            {
                                progressdialog.dismiss();
                                Toast.makeText(Signin.this,"Error signing in.",Toast.LENGTH_LONG).show();
                            }

                        }
                    }
            );




        }
        else{   Toast.makeText(Signin.this,"Please enter all fields..",Toast.LENGTH_LONG).show();

        }



    }

    public void checkuserexist() {

        userdabasereference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userid= mauth.getCurrentUser().getUid();
                        if(dataSnapshot.hasChild(userid))
                        {
                            Intent mainIntent= new Intent(Signin.this,MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);

                        }

                            else
                        {
                            Intent setupIntent= new Intent(Signin.this,AccountActivity.class);
                            setupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(setupIntent);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );


    }
}
