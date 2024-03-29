package com.a7codes.menu25.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;

import com.a7codes.menu25.Adapters.AdapterClassA;
import com.a7codes.menu25.Adapters.AdapterClassB;
import com.a7codes.menu25.Classes.ClassA;
import com.a7codes.menu25.Classes.ClassB;
import com.a7codes.menu25.MainActivity;
import com.a7codes.menu25.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class Menu extends AppCompatActivity implements AdapterClassB.OnItemClickListener {

    //Vars
    ArrayList<ClassA> itemsA = new ArrayList<>();
    boolean itemATapped = false;
    ArrayList<ClassB> itemsB = new ArrayList<>();
    ArrayList<ClassB> itemsBFiltered = new ArrayList<>();
    DatabaseReference dbRef;
    StorageReference stRef;
    AdapterClassA adapter1;
    AdapterClassB adapter2;

    //Views
    ImageView imgLogo;
    LinearLayout ssl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //Set DB
        SharedPreferences appPrefs = this.getSharedPreferences("apPrefs", MODE_PRIVATE);
        String lic = appPrefs.getString("lic", "");
        dbRef = FirebaseDatabase.getInstance().getReference("MENU25").child(lic);
        dbRef.keepSynced(false);
        stRef = FirebaseStorage.getInstance().getReference().child("Menu25").child(lic);

        //Basics
        goFullScreen();

        //Assigner
        Assigner();

        //Start UI

        //Read Items from DB
        ReadDB();

        //Recycler View
        RecyclerView recyclerView = findViewById(R.id.am_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter1 = new AdapterClassA(this, itemsA);
        recyclerView.setAdapter(adapter1);

        //Grid view
        GridView grid = findViewById(R.id.am_grid);
        adapter2 = new AdapterClassB(this, itemsBFiltered, this);
        grid.setAdapter(adapter2);

        //Clicker
        Clicker();

        //Set Up From Shared Prefs
        setUpFromSP();


    }

    /* Basics */
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

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemATapped = false;
                itemsA.clear();
                itemsB.clear();
                ReadDB();
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
        });

        adapter1.setOnItemClickListener(new AdapterClassA.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                itemATapped = true;
                itemsBFiltered.clear();

                for (ClassB item : itemsB){
                    if (item.getParent() == position + 1){
                        itemsBFiltered.add(item);
                    }
                }
                adapter2.notifyDataSetChanged();
            }
        });
    }

    private void setUpFromSP(){
        String Logo = "";
        String Color1 = "";
        String Color2 = "";

        /* General Prefs */
        SharedPreferences GsPrefs = this.getSharedPreferences("gsprefs", MODE_PRIVATE);

        Logo = GsPrefs.getString("Logo", "");
        Color1 = GsPrefs.getString("Color1", "");

        if (!Logo.equals("")){

            File imgLogoGeneralFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/General/logo.png");
            if (imgLogoGeneralFile.exists()){
                Glide.with(Menu.this)
                        .load(imgLogoGeneralFile)
                        .into(imgLogo);
            } else {
                Glide.with(Menu.this)
                        .load(Logo)
                        .into(imgLogo);
            }
        } else {
            dbRef.child("gs").child("logo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.getValue().toString().equals("")){
                        Glide.with(Menu.this)
                                .load(snapshot.getValue().toString())
                                .into(imgLogo);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        ssl.setBackgroundColor(Color.parseColor(Color1));

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

                if (!itemATapped){
                    itemsBFiltered.clear();
                    for (ClassB item : itemsB){
                        itemsBFiltered.add(item);
                    }
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
        showItemDetails(item);
    }

    private void showItemDetails(ClassB item){
        Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.item_details_dialog);
        customDialog.show();

        // Get views from the custom layout
//        EditText customInput = customDialog.findViewById(R.id.);
//        Button customButton = customDialog.findViewById(R.id.custom_button);

        TextView title = customDialog.findViewById(R.id.idd_title);
        TextView price = customDialog.findViewById(R.id.idd_price);
        TextView desc  = customDialog.findViewById(R.id.idd_desc);
        ImageView img  = customDialog.findViewById(R.id.idd_img);
        ConstraintLayout root = customDialog.findViewById(R.id.idd_root);

        title.setText(item.getTitle());
        price.setText(item.getPrice());
        desc.setText(item.getDesc());

        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL));
        Glide.with(Menu.this)
                .load(item.getImage())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(img);

        // Set Background Color
        SharedPreferences GsPrefs = Menu.this.getSharedPreferences("gsprefs", MODE_PRIVATE);
        String Color2 = GsPrefs.getString("Color2", "");
        int tintColor = Color.parseColor(Color2);
        Drawable bgTitleRoot = ContextCompat.getDrawable(this, R.drawable.rounded_corners_2).mutate();
        DrawableCompat.setTint(bgTitleRoot, tintColor);
        root.setBackground(bgTitleRoot);


        // Set a click listener for the button
//        customButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String inputText = customInput.getText().toString();
//                // Handle the button click and process the input text
//                // ...
//
//                // Dismiss the Dialog
//                customDialog.dismiss();
//            }
//        });
    }

}