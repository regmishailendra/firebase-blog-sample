package com.example.shailendra.appfire;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shailendra on 10/30/2016.
 */
public class appfire extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        if(!FirebaseApp.getApps(getApplicationContext()).isEmpty())
        { FirebaseDatabase.getInstance().setPersistenceEnabled(true);}

    }
}
