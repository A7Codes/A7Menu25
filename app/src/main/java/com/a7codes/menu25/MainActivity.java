package com.a7codes.menu25;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    DatabaseReference dbRef0 = FirebaseDatabase.getInstance().getReference("MENU25");
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
        logoAnimation();


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


        SharedPreferences appPrefs = this.getSharedPreferences("apPrefs", MODE_PRIVATE);
        boolean firstLaunch = false;
        boolean licValidated = false;

        licValidated = appPrefs.getBoolean("licValidated", false);
        firstLaunch = appPrefs.getBoolean("firstLaunch", true);

        if (firstLaunch){

            String Logo = "";
            String Color1 = "#E6173261";
            String Color2 = "#E6173261";

            /* General Prefs */
            SharedPreferences GsPrefs = this.getSharedPreferences("gsprefs", MODE_PRIVATE);
            GsPrefs.edit().putString("Logo", Logo).apply();
            GsPrefs.edit().putString("Color1", Color1).apply();
            GsPrefs.edit().putString("Color2", Color2).apply();

            appPrefs.edit().putBoolean("firstLaunch", false).apply();


        }

        if (!licValidated){
            showLicenseInputDialog();
        } else {
            Intent intent = new Intent(MainActivity.this, Menu.class);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 3000);

        }



    }




    private void showLicenseInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter License");

        // Inflate the layout with EditText
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set the positive button to check the license
        builder.setPositiveButton("Check", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String license = input.getText().toString();
                if (!TextUtils.isEmpty(license)) {
                    // Implement your logic to check the license
                    boolean isLicenseValid = checkLicense(license);
                    if (isLicenseValid) {
                        Toast.makeText(getApplicationContext(), "License is valid", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Menu.class);
                        SharedPreferences appPrefs = MainActivity.this.getSharedPreferences("apPrefs", MODE_PRIVATE);
                        appPrefs.edit().putBoolean("licValidated", true).apply();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                            }
                        }, 3000);

                    } else {
                        Toast.makeText(getApplicationContext(), "Restart App please", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a license", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        // Set the negative button to dismiss the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the AlertDialog
        builder.show();
    }

    private boolean checkLicense(String license) {
        dbRef0.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot D0 : snapshot.getChildren()){
                    if (D0.getKey().equals(license)){
                        if (D0.getValue().toString().equals("")){
                            /* Set New GS */
                            dbRef0.child(license).child("gs").child("color1").setValue("#E6173261");
                            dbRef0.child(license).child("gs").child("color2").setValue("#E6173261");
                            dbRef0.child(license).child("gs").child("logo").setValue("");
                            dbRef0.child(license).child("gs").child("rn").setValue("A7Codes");

                            /* Set New Lic */
                            dbRef0.child(license).child("lic").child("dr").setValue(5);
                            dbRef0.child(license).child("lic").child("id").setValue(license);

                        } else {
                            for (DataSnapshot D1: D0.getChildren()){
                                if (D1.getKey().equals("gs")){
                                    for (DataSnapshot D2 : D1.getChildren()){
                                        if (D2.getKey().equals("color1")){
                                            SharedPreferences GsPrefs = MainActivity.this.getSharedPreferences("gsprefs", MODE_PRIVATE);
                                            GsPrefs.edit().putString("Color1", D2.getValue().toString()).apply();
                                        } else if (D2.getKey().equals("color2")){
                                            SharedPreferences GsPrefs = MainActivity.this.getSharedPreferences("gsprefs", MODE_PRIVATE);
                                            GsPrefs.edit().putString("Color2", D2.getValue().toString()).apply();
                                        } else if (D2.getKey().equals("logo")){
                                            SharedPreferences GsPrefs = MainActivity.this.getSharedPreferences("gsprefs", MODE_PRIVATE);
                                            GsPrefs.edit().putString("Logo", D2.getValue().toString()).apply();
                                        } else if (D2.getKey().equals("rn")){
                                            SharedPreferences GsPrefs = MainActivity.this.getSharedPreferences("gsprefs", MODE_PRIVATE);
                                            GsPrefs.edit().putString("rn", D2.getValue().toString()).apply();
                                        }
                                    }
                                }
                            }

                        }
                        SharedPreferences appPrefs = MainActivity.this.getSharedPreferences("apPrefs", MODE_PRIVATE);
                        appPrefs.edit().putString("lic", license).apply();

                    } else {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbRef0.child(license).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    SharedPreferences appPrefs = MainActivity.this.getSharedPreferences("apPrefs", MODE_PRIVATE);
                    appPrefs.edit().putBoolean("licValidated", true).apply();
                } else {
                    SharedPreferences appPrefs = MainActivity.this.getSharedPreferences("apPrefs", MODE_PRIVATE);
                    appPrefs.edit().putBoolean("licValidated", false).apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SharedPreferences appPrefs = MainActivity.this.getSharedPreferences("apPrefs", MODE_PRIVATE);
        return appPrefs.getBoolean("licValidated", false);
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


    private void logoAnimation(){
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this,R.anim.splash_anim);
        ImageView logo = findViewById(R.id.main_logo_img);
        logo.setAnimation(anim);
        anim.start();
    }

}