package com.a7codes.menu25.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.a7codes.menu25.Adapters.AdapterClassA;
import com.a7codes.menu25.Adapters.AdapterClassB;
import com.a7codes.menu25.Classes.ClassA;
import com.a7codes.menu25.Classes.ClassB;
import com.a7codes.menu25.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Menu extends AppCompatActivity implements AdapterClassB.OnItemClickListener {

    //Vars
    ArrayList<ClassA> itemsA = new ArrayList<>();
    ArrayList<ClassB> itemsB = new ArrayList<>();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("MENU25").child("a7codestest");
    StorageReference stRef = FirebaseStorage.getInstance().getReference().child("Menu25");
    AdapterClassA adapter1;
    AdapterClassB adapter2;

    //Views
    ImageView imgLogo;
    LinearLayout ssl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Basics
        goFullScreen();

        //Assigner
        Assigner();

        //Clicker
        Clicker();

        //Start UI


        //Recycler View
        RecyclerView recyclerView = findViewById(R.id.am_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter1 = new AdapterClassA(this, itemsA);
        recyclerView.setAdapter(adapter1);

        //Grid view
        GridView grid = findViewById(R.id.am_grid);

        adapter2 = new AdapterClassB(this, itemsB, this);
        grid.setAdapter(adapter2);

        //Read Items from DB
        ReadDB();

        //Set Up From Memory

        //Testing
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

    private void Assigner(){
        imgLogo = findViewById(R.id.am_logo);
        ssl = findViewById(R.id.menu_SLL);
    }

    private void Clicker(){
        imgLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ShowLoginAlert();
                return false;
            }
        });
    }

    private void ShowLoginAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Menu.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.admin_alert, null);
        alertDialog.setView(customLayout);
        EditText passEt = customLayout.findViewById(R.id.admin_alert_etPass);
        Button enterBtn = customLayout.findViewById(R.id.admin_alert_btnEnter);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passEt.getText().toString().equals("Root")){
                    Intent intent = new Intent(Menu.this, AdminPanel.class);
                    startActivity(intent);

                }
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void ReadDB(){

        //Read ClassA DB
        dbRef.child("menu").child("ClassA").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping inside the ClassA Tuple in Database
                for (DataSnapshot D0 : snapshot.getChildren()){
                //Making a Holder Item and Filling it by looping inside each tuple in the Object.
                    ClassA tmpItemA = new ClassA(0, "Test", "Test");

                    for (DataSnapshot D1 : D0.getChildren()){
                        switch (D1.getKey()){
                            case "icon":
                                String icon = D1.getValue().toString();
                                tmpItemA.setIcon(icon);
                                break;
                            case "title":
                                String title = D1.getValue().toString();
                                tmpItemA.setTitle(title);
                                break;
                            case "id":
                                int id = Integer.parseInt(Objects.requireNonNull(D1.getValue()).toString());
                                tmpItemA.setId(id);
                                break;
                        }
                    }
                    itemsA.add(tmpItemA);
                    adapter1.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //Read ClassB DB
        dbRef.child("menu").child("ClassB").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot D0: snapshot.getChildren()){

                    ClassB tmpB = new ClassB(0, "", "", "", 0, "");

                    for (DataSnapshot D1: D0.getChildren()){

                        if (Objects.equals(D1.getKey(), "desc")){tmpB.setDesc(D1.getValue().toString());}
                        if (Objects.equals(D1.getKey(), "id")){tmpB.setId(Integer.parseInt(D1.getValue().toString()));}
                        if (Objects.equals(D1.getKey(), "parent")){tmpB.setParent(Integer.parseInt(D1.getValue().toString()));}
                        if (Objects.equals(D1.getKey(), "price")){tmpB.setPrice(D1.getValue().toString());}
                        if (Objects.equals(D1.getKey(), "title")){tmpB.setTitle(D1.getValue().toString());}
                        if (Objects.equals(D1.getKey(), "image")){tmpB.setImage(D1.getValue().toString());}
                    }

                    itemsB.add(tmpB);

                    adapter2.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onItemClick(ClassB item) {

    }
}