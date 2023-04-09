package com.a7codes.menu25.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AdminPanel extends AppCompatActivity implements AdapterClassB.OnItemClickListener{

    //Views
    ConstraintLayout constraintLayout1;
    ImageView btnHome;
    ImageView btnItems;
    ImageView btnDatabase;
    ConstraintLayout constraintLayout2;

    //General Screen
//Views
    ImageView logoImageGeneral;
    Button setLogoBtn;
    Button delLogoBtn;
    ConstraintLayout colorScheme1;
    ConstraintLayout colorScheme2;

    //Vars
    Bitmap selectedImage;
    SharedPreferences MenuPrefs;

    //Const 2
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("MENU25").child("a7codestest");
    ArrayList<ClassA> itemsA = new ArrayList<>();
    ArrayList<ClassB> itemsB = new ArrayList<>();
    AdapterClassB adapter2;
    GridView grid;
    EditText IDEt;
    EditText TitleEt;
    EditText PriceEt;
    EditText DescEt;
    EditText ParentEt;
    ImageView ImageIV;
    Button AddBtn;
    Button EditBtn;
    Button DeleteBtn;
    Button UpdateImageBtn;
    Button DeleteImageBtn;

    ClassA selectedClassAItem;
    ClassB selectedClassBItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        //Basics
        goFullScreen();
        MenuPrefs = this.getSharedPreferences("MenuPrefs", MODE_PRIVATE);

        //Assigner
        Assigner();
        //

        Assigner();

        //Clicker
        Clicker();


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
        //constraintLayouts 1
        constraintLayout1 = findViewById(R.id.admin_panel_const_1);
        btnHome = findViewById(R.id.admin_panel_const_home);
        btnItems = findViewById(R.id.admin_panel_const_items);
        btnDatabase = findViewById(R.id.admin_panel_const_db);

        //constraintLayouts 2
        constraintLayout2 = findViewById(R.id.admin_panel_const_2);

    }

    private void Clicker(){
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupGeneralView();
            }
        });

        btnItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupMenuView();
            }
        });
    }

    private void setupGeneralView() {
        constraintLayout2.removeAllViews();
        View generalConst = getLayoutInflater().inflate(R.layout.admin_panel_general, null);
        constraintLayout2.addView(generalConst);

        //Assign
        logoImageGeneral = generalConst.findViewById(R.id.imgLogoGeneral);
        setLogoBtn = generalConst.findViewById(R.id.BtnLogoGeneralSet);
        delLogoBtn = generalConst.findViewById(R.id.BtnLogoGeneralDelete);
        colorScheme1 = generalConst.findViewById(R.id.BtnCSGeneral1);
        colorScheme2 = generalConst.findViewById(R.id.BtnCSGeneral2);

        // Set click listeners after assigning the views
        logoImageGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoGeneralTapped();
            }
        });

        setLogoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogoBtnTapped();
            }
        });

        delLogoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delLogoBtnTapped();
            }
        });

        colorScheme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog(AdminPanel.this, new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(String color) {
                        Log.d("*************************", "onColorSelected: " + color);
                        colorScheme1.setBackgroundColor(Color.parseColor(color));
                        MenuPrefs.edit().putString("ColorScheme1", color).apply();
                    }
                });
            }
        });
    }


    private void setupMenuView() {
        constraintLayout2.removeAllViews();
        View menuConst = getLayoutInflater().inflate(R.layout.admin_panel_menu, constraintLayout2, false);
        constraintLayout2.addView(menuConst);
        //Set Up Grid
        //Assigner
        grid = findViewById(R.id.apgridview);
        IDEt = findViewById(R.id.apidet);
        TitleEt = findViewById(R.id.aptitleet);
        PriceEt = findViewById(R.id.appriceet);
        DescEt = findViewById(R.id.apdescet);
        ParentEt = findViewById(R.id.apparentet);
        ImageIV = findViewById(R.id.apimageiv);
        AddBtn = findViewById(R.id.apaddbtn);
        EditBtn = findViewById(R.id.apeditbtn);
        DeleteBtn = findViewById(R.id.apdeletebtn);
        UpdateImageBtn = findViewById(R.id.apupdateimagebtn);
        DeleteImageBtn = findViewById(R.id.apdeleteimagebtn);

        ReadDB();
        adapter2 = new AdapterClassB(this, itemsB, this);
        grid.setAdapter(adapter2);

        // Add any click listeners for the menu view here
    }



    //General Screen
    private void logoGeneralTapped(){
        //Logo image was Tapped
        //Start to select image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 200){
            Uri selectedImageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                logoImageGeneral.setImageBitmap(selectedImage);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLogoBtnTapped(){
        File imgLogoGeneralFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/General/logo.png");

        if (imgLogoGeneralFile.exists()){
            imgLogoGeneralFile.delete();

        } else {
            try (FileOutputStream out = new FileOutputStream(imgLogoGeneralFile)) {
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    private void delLogoBtnTapped(){
        File imgLogoGeneralFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/General/logo.png");
        if (imgLogoGeneralFile.exists()){
            imgLogoGeneralFile.delete();
        }
    }

    public void showColorPickerDialog(Context context, final OnColorSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View colorPickerView = inflater.inflate(R.layout.color_picker_dialog, null);

        int[] colorButtonsIds = {
                R.id.colorButton1,
                R.id.colorButton2,
                R.id.colorButton3,
                R.id.colorButton4,
                R.id.colorButton5
        };

        final String[] colors = {
                "#FF0000",
                "#00FF00",
                "#0000FF",
                "#FFFF00",
                "#FF00FF"
        };

        for (int i = 0; i < colorButtonsIds.length; i++) {
            Button colorButton = colorPickerView.findViewById(colorButtonsIds[i]);
            colorButton.setBackgroundColor(Color.parseColor(colors[i]));
            final String color = colors[i];
            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onColorSelected(color);
                }
            });
        }

        builder.setTitle("Select Color")
                .setView(colorPickerView)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onItemClick(ClassB item) {
        selectedClassBItem = item;

        IDEt.setText(String.valueOf(item.getId()));
        TitleEt.setText(item.getTitle());
        PriceEt.setText(item.getPrice());
        DescEt.setText(item.getDesc());
        ParentEt.setText(String.valueOf(item.getParent()));
        Glide.with(AdminPanel.this).load(item.getImage()).into(ImageIV);

    }


    public interface OnColorSelectedListener {
        void onColorSelected(String color);
    }

    private void ReadDB(){

        //Read ClassA DB
//        dbRef.child("menu").child("ClassA").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                //Looping inside the ClassA Tuple in Database
//                for (DataSnapshot D0 : snapshot.getChildren()){
//                    //Making a Holder Item and Filling it by looping inside each tuple in the Object.
//                    ClassA tmpItemA = new ClassA(0, "Test", "Test");
//
//                    for (DataSnapshot D1 : D0.getChildren()){
//                        switch (D1.getKey()){
//                            case "icon":
//                                String icon = D1.getValue().toString();
//                                tmpItemA.setIcon(icon);
//                                break;
//                            case "title":
//                                String title = D1.getValue().toString();
//                                tmpItemA.setTitle(title);
//                                break;
//                            case "id":
//                                int id = Integer.parseInt(Objects.requireNonNull(D1.getValue()).toString());
//                                tmpItemA.setId(id);
//                                break;
//                        }
//                    }
//                    itemsA.add(tmpItemA);
//                    adapter1.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });

        //Read ClassB DB
        itemsB.clear();

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




}