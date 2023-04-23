package com.a7codes.menu25;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Menu25 extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
