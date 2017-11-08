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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AccountActivity extends AppCompatActivity {
    private ImageButton myimage;
    EditText name;
    EditText college;
    EditText hometown;
    Button submit;
    public Uri mimageUri=null;
    private static final int gc=1;
    DatabaseReference mdatabaseusers;
    private FirebaseAuth mauth;
private StorageReference mstorage;
    ProgressDialog progressDIalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account2);
        name=(EditText)findViewById(R.id.name);
        college=(EditText)findViewById(R.id.college);
        hometown=(EditText)findViewById(R.id.hometown);
        myimage=(ImageButton)findViewById(R.id.myimage);
        submit=(Button)findViewById(R.id.submitbutton);
        progressDIalog= new ProgressDialog(this);
        mauth=FirebaseAuth.getInstance();
        mstorage= FirebaseStorage.getInstance().getReference().child("Profile_Images");
        mdatabaseusers= FirebaseDatabase.getInstance().getReference().child("Users");
        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSetupAccount();
                    }
                }
        );

        myimage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent gintent= new Intent();
                        gintent.setAction(Intent.ACTION_GET_CONTENT);
                        gintent.setType("image/*");
                        startActivityForResult(gintent,gc);



                    }
                }
        );



    }

    private void startSetupAccount() {
        final String profileName=name.getText().toString();
        final String profileCollege =college.getText().toString();
        final String profileHometown=hometown.getText().toString();

        if(!profileName.isEmpty() && !profileCollege.isEmpty() && !profileHometown.isEmpty() && mimageUri!=null)
        {
            progressDIalog.setMessage("Finishing Account Setup...");
            progressDIalog.show();
    StorageReference  filepath = mstorage.child(mimageUri.getLastPathSegment());
             filepath.putFile(mimageUri).addOnSuccessListener(
                     new OnSuccessListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             String duri= taskSnapshot.getDownloadUrl().toString();

                             String userId= mauth.getCurrentUser().getUid().toString();
                             mdatabaseusers.child(userId).child("name").setValue(profileName);
                             mdatabaseusers.child(userId).child("college").setValue(profileCollege);
                             mdatabaseusers.child(userId).child("hometown").setValue(profileHometown);
                             mdatabaseusers.child(userId).child("image").setValue(duri);
                             progressDIalog.dismiss();
                             Intent mainIntent= new Intent(AccountActivity.this,MainActivity.class);
                             mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                             startActivity(mainIntent);

                         }
                     }
             );














        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode== gc && resultCode==RESULT_OK)
        {
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mimageUri = result.getUri();
                myimage.setImageURI(mimageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
