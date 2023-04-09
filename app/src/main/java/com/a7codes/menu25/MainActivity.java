package com.a7codes.menu25;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.a7codes.menu25.Activities.Menu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Basics
        goFullScreen();
        //Set Shared Prefs
        SetSharedPrefs();
        //Set Directory
        SetMenuFolder();
        //splash screen
        Intent intent = new Intent(MainActivity.this, Menu.class);
        startActivity(intent);

        //Testing
//        CheckLic();
    }

    private void goFullScreen(){
        //Full Screen
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void SetSharedPrefs(){

        //Lic shared prefs
        String licID = "";
        int dr = 0;

        SharedPreferences LicPrefs = this.getSharedPreferences("licprefs", MODE_PRIVATE);
//        LicPrefs.edit().putString("licID", licID).apply();
//        LicPrefs.edit().putInt("dr", dr).apply();

        //General Shared prefs
        String RN = "";
        String Logo = "";
        String Color1 = "";
        String Color2 = "";

        SharedPreferences GsPrefs = this.getSharedPreferences("gsprefs", MODE_PRIVATE);
//        GsPrefs.edit().putString("rn", RN).apply();
//        GsPrefs.edit().putString("Logo", Logo).apply();
//        GsPrefs.edit().putString("color1", Color1).apply();
//        GsPrefs.edit().putString("color2", Color2).apply();
    }

    private void CheckLic(){
        SharedPreferences LicPrefs = this.getSharedPreferences("licprefs", MODE_PRIVATE);
        String licIDSP = LicPrefs.getString("licID", "error");
        int drSP = LicPrefs.getInt("dr", 0);

//        Log.d("*******************", "CheckLic: ID " + licIDSP);
//        Log.d("*******************", "CheckLic: DR " + drSP);

        DatabaseReference dbRefLic = FirebaseDatabase.getInstance().getReference("MENU25").child(licIDSP).child("lic");
        dbRefLic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot D0: snapshot.getChildren()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void SetMenuFolder(){
        File A7_Menu_V25 = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25");
        if (!A7_Menu_V25.exists()){
            A7_Menu_V25.mkdir();
        }

        File A7_Menu_V25_General = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/General");
        if (!A7_Menu_V25_General.exists()){
            A7_Menu_V25_General.mkdir();
        }

        File A7_Menu_V25_Categories = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Categories");
        if (!A7_Menu_V25_Categories.exists()){
            A7_Menu_V25_Categories.mkdir();
        }

        File A7_Menu_V25_Items = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Items");
        if (!A7_Menu_V25_Items.exists()){
            A7_Menu_V25_Items.mkdir();
        }

        
    }
}