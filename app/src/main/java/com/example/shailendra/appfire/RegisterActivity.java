package com.example.shailendra.appfire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password;
    private Button registerbutton;
    private FirebaseAuth mauth;
    private ProgressDialog progressdialog;
    private DatabaseReference mdatabase;
    private DatabaseReference userdabasereference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mauth=FirebaseAuth.getInstance();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");


        name=(EditText)findViewById(R.id.name);
        email =(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        registerbutton=(Button) findViewById(R.id.registerbutton);
        progressdialog= new ProgressDialog(this);



        registerbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startRegister();
                    }
                }
        );


    }

    private void startRegister() {
        String mname=name.getText().toString();
        String memail=email.getText().toString();
        final String mpassword=password.getText().toString();

        if(!mname.isEmpty() && !memail.isEmpty() && !mpassword.isEmpty())
        {
            progressdialog.setMessage("Signing up...");
            progressdialog.setCancelable(false);
            progressdialog.show();
    mauth.createUserWithEmailAndPassword(memail,mpassword).addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {

                        String userid= mauth.getCurrentUser().getUid();
                       DatabaseReference currentUserDb=  mdatabase.child("userId");
                        currentUserDb.child("name").setValue(name);
                        currentUserDb.child("image").setValue("default");
                       // currentUserDb.child("name").setValue(name);
                        progressdialog.dismiss();

                        Intent mainIntent= new Intent(RegisterActivity.this,AccountActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);


                    }
                }
            }
    );

        }
 else{   Toast.makeText(RegisterActivity.this,"Please enter all fields..",Toast.LENGTH_LONG).show();

        }

    }
}
