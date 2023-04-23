package com.a7codes.menu25.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.a7codes.menu25.Adapters.AdapterClassA;
import com.a7codes.menu25.Adapters.AdapterClassB;
import com.a7codes.menu25.Classes.ClassA;
import com.a7codes.menu25.Classes.ClassB;
import com.a7codes.menu25.Classes.ImageDownloader;
import com.a7codes.menu25.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AdminPanel extends AppCompatActivity implements AdapterClassB.OnItemClickListener{

    //Views
    ConstraintLayout constraintLayout1;
    ImageView btnHome;
    ImageView btnItemsB;
    ImageView btnItemsA;
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
    SharedPreferences GsPrefs;

    //Const 2
    DatabaseReference dbRef;
    StorageReference stRef;
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

    //Database View
    Button UploadImages;
    Button DownloadImages;

    //Menu A
    AdapterClassA adapter1;
    RecyclerView recA;
    int viewMode = 0;

    EditText IDEta;

    EditText TitleEta;

    Spinner IconSpA;

    Button AddA;
    Button updateA;

    ArrayList<String> ClassAIcons = new ArrayList<>();
    String ClassASelectedIcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        //Setup DB
        SharedPreferences appPrefs = this.getSharedPreferences("apPrefs", MODE_PRIVATE);
        String lic = appPrefs.getString("lic", "");
        dbRef = FirebaseDatabase.getInstance().getReference("MENU25").child(lic);
        stRef = FirebaseStorage.getInstance().getReference().child("Menu25").child(lic);

        //Basics
        goFullScreen();
        GsPrefs = this.getSharedPreferences("gsprefs", MODE_PRIVATE);

        //Assigner
        Assigner();

        //Clicker
        Clicker();

        //Start Up
        setupGeneralView();

        //Sync DB
        dbRef.keepSynced(true);
    }

    /*
        View Basics
     */
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
        btnItemsB = findViewById(R.id.admin_panel_const_itemsb);
        btnItemsA = findViewById(R.id.admin_panel_const_itemsa);
        btnDatabase = findViewById(R.id.admin_panel_const_db);

        //constraintLayouts 2
        constraintLayout2 = findViewById(R.id.admin_panel_const_2);

    }

    private void Clicker(){
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupGeneralView();
                viewMode = 1;
            }
        });

        btnItemsA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupMenuViewA();
                viewMode = 2;
            }
        });

        btnItemsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupMenuViewB();
                viewMode = 3;
            }
        });

        btnDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupDatabaseView();
                viewMode = 4;
            }
        });
    }

    /*
        General Settings View
     */

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
                        colorScheme1.setBackgroundColor(Color.parseColor(color));
                        GsPrefs.edit().putString("Color1", color).apply();
                    }
                });
            }
        });

        colorScheme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog(AdminPanel.this, new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(String color) {
                        colorScheme2.setBackgroundColor(Color.parseColor(color));
                        GsPrefs.edit().putString("Color2", color).apply();
                    }
                });
            }
        });
    }

    private void logoGeneralTapped(){
        //Logo image was Tapped
        //Start to select image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 200);
    }

    private void setLogoBtnTapped(){
        File imgLogoGeneralFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/General/logo.png");

        if (imgLogoGeneralFile.exists()){
            imgLogoGeneralFile.delete();

            try (FileOutputStream out = new FileOutputStream(imgLogoGeneralFile)) {
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e){
                e.printStackTrace();
            }

        } else {
            try (FileOutputStream out = new FileOutputStream(imgLogoGeneralFile)) {
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        SharedPreferences GsPrefs = this.getSharedPreferences("gsprefs", MODE_PRIVATE);
        GsPrefs.edit().putString("Logo", "Logo.png").apply();

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
        } else if (resultCode == RESULT_OK && requestCode == 201){
            Uri selectedImageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                ImageIV.setImageBitmap(selectedImage);
            }
            catch (IOException e) {
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
                "#E6173261",
                "#E6ffbe76",
                "#E6badc58",
                "#E6e056fd",
                "#E67ed6df"
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

    public interface OnColorSelectedListener {
        void onColorSelected(String color);
    }

    /*
        Menu A Settings View
     */


    private void setupMenuViewA(){
        constraintLayout2.removeAllViews();
        View menuConst = getLayoutInflater().inflate(R.layout.admin_panel_menua, constraintLayout2, false);
        constraintLayout2.addView(menuConst);


        //Assigner
        recA = findViewById(R.id.ama_rec);

        IDEta = findViewById(R.id.ama_etID);
        TitleEta = findViewById(R.id.ama_etTitle);
        IconSpA = findViewById(R.id.ama_spIcon);
        AddA = findViewById(R.id.ama_btnAdd);
        updateA = findViewById(R.id.ama_btnUpdate);

        //setup recycler view
        adapter1 = new AdapterClassA(this, itemsA);
        recA.setLayoutManager(new LinearLayoutManager(this));
        recA.setAdapter(adapter1);
        ReadDB();
        setupSpinner();
        MenuAClicker();

        //Clicker
        adapter1.setOnItemClickListener(new AdapterClassA.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                setInfo(position);
            }
        });

    }

    private void setInfo(int pos){
        selectedClassAItem = itemsA.get(pos);

        IDEta.setText(String.valueOf(selectedClassAItem.getId()));
        TitleEta.setText(selectedClassAItem.getTitle());
    }

    private void setupSpinner() {
        final ArrayList<String> ClassAIcons = new ArrayList<>();
        ClassAIcons.add("ic_starters");
        ClassAIcons.add("ic_salad");
        ClassAIcons.add("ic_dishes");
        ClassAIcons.add("ic_sandwich");
        ClassAIcons.add("ic_fastfood");
        ClassAIcons.add("ic_dessert");
        ClassAIcons.add("ic_hotdrink");
        ClassAIcons.add("ic_colddrink");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ClassAIcons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IconSpA.setAdapter(adapter);
        IconSpA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ClassASelectedIcon = ClassAIcons.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void MenuAClicker(){
        AddA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!IDEta.getText().toString().equals("")){
                    ClassA item = new ClassA(Integer.parseInt(IDEta.getText().toString()), TitleEta.getText().toString(), ClassASelectedIcon);
                    dbRef.child("menu").child("ClassA").child(String.valueOf(item.getId())).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            itemsA.clear();
                            ReadDB();
                            adapter1.notifyDataSetChanged();
                        }
                    });
                } else {
                    Log.d("Error", "onClick: No ID Input");
                }

                adapter1.notifyDataSetChanged();
            }
        });

        updateA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassA item = selectedClassAItem;

                item.setTitle(TitleEta.getText().toString());
                item.setIcon(ClassASelectedIcon);

                dbRef.child("menu").child("ClassA").child(String.valueOf(item.getId())).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        itemsA.clear();
                        ReadDB();
                        adapter1.notifyDataSetChanged();
                    }
                });


            }
        });
    }




    /*
        Menu B Settings View
     */
    private void setupMenuViewB() {
        constraintLayout2.removeAllViews();
        View menuConst = getLayoutInflater().inflate(R.layout.admin_panel_menub, constraintLayout2, false);
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

        //Clicker
        MenuBClicker();

        //Setup
        ReadDB();
        adapter2 = new AdapterClassB(this, itemsB, this);
        grid.setAdapter(adapter2);

    }

    private void MenuBClicker(){
        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassB tmpB = new ClassB(Integer.parseInt(IDEt.getText().toString()), TitleEt.getText().toString(),
                        PriceEt.getText().toString(), DescEt.getText().toString(), Integer.parseInt(ParentEt.getText().toString()),
                        "To Be Codded");

                dbRef.child("menu").child("ClassB").child(String.valueOf(tmpB.getId())).setValue(tmpB).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Refresh();
                    }
                });
            }
        });

        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassB tmpItemB = new ClassB(Integer.parseInt(IDEt.getText().toString()), TitleEt.getText().toString(),
                        PriceEt.getText().toString(), DescEt.getText().toString(), Integer.parseInt(ParentEt.getText().toString()),
                        "");

                dbRef.child("menu").child("ClassB").child(String.valueOf(tmpItemB.getId())).setValue(tmpItemB).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Refresh();
                    }
                });



            }
        });

        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef.child("menu").child("ClassB").child(String.valueOf(selectedClassBItem.getId())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Refresh();
                    }
                });
                Refresh();
            }
        });

        ImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 201);
            }
        });

        UpdateImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateImageBtnTapped();
            }
        });

        DeleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassB item = selectedClassBItem;

                File imgItemMenuFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Items/" + item.getId() + ".png");

                if (imgItemMenuFile.exists()){
                    boolean deleted = imgItemMenuFile.delete();

                    if (deleted){
                        Refresh();
                    }

                } else {
                    stRef.child("/Items").child(item.getId() + ".png").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Refresh();
                        }
                    });

                }
            }
        });


    }

    private void UpdateImageBtnTapped(){
        File imgItemMenuFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Items/" + selectedClassBItem.getId() + ".png");

        if (imgItemMenuFile.exists()){
            imgItemMenuFile.delete();

            try (FileOutputStream out = new FileOutputStream(imgItemMenuFile)) {
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, out);

            } catch (IOException e){
                e.printStackTrace();
            }

        } else {
            try (FileOutputStream out = new FileOutputStream(imgItemMenuFile)) {
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, out);

            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Refresh();
            }
        }, 500);


    }

    private void Refresh() {
        itemsB.clear();
        itemsA.clear();
        ReadDB();
        adapter2.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(ClassB item) {
        selectedClassBItem = item;

        IDEt.setText(String.valueOf(item.getId()));
        TitleEt.setText(item.getTitle());
        PriceEt.setText(item.getPrice());
        DescEt.setText(item.getDesc());
        ParentEt.setText(String.valueOf(item.getParent()));

        File imgItemMenuFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Items/" + item.getId() + ".png");

        if (imgItemMenuFile.exists()){
            Glide.with(AdminPanel.this)
                    .load(imgItemMenuFile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ImageIV);
        } else {
            Glide.with(AdminPanel.this)
                    .load(item.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ImageIV);
        }

    }


    private void ReadDB(){

//        Read ClassA DB
        itemsA.clear();
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
                    if (viewMode == 2){
                        adapter1.notifyDataSetChanged();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //Read ClassB DB
        itemsB.clear();

        ValueEventListener listenerB = dbRef.child("menu").child("ClassB").addValueEventListener(new ValueEventListener() {
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

                }

                if (viewMode == 3){
                    adapter2.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

        /* Database Settings View */
        private void setupDatabaseView() {
            constraintLayout2.removeAllViews();
            View DBConst = getLayoutInflater().inflate(R.layout.admin_panel_sync, constraintLayout2, false);
            constraintLayout2.addView(DBConst);


            //Set Up Grid
            //Assigner
            UploadImages = findViewById(R.id.apuploadimgsebtn);
            DownloadImages = findViewById(R.id.apdownloadimgsebtn);

            //Clicker
            DBClicker();

        }

        private void DBClicker(){
            UploadImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UploadImagesTapped();
                    Log.d("*****///////***********??????????", "onSuccess: Tapped");
                }
            });

            DownloadImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadImagesTapped();
                }
            });
        }
        private void UploadImagesTapped(){

            ArrayList<String> imagesExists = new ArrayList<>();

            for (ClassB item: itemsB){
                File imgItemMenuFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Items/" + item.getId() + ".png");

                if (imgItemMenuFile.exists()){
                    imagesExists.add(String.valueOf(item.getId()));
                }
            }

            for (String id : imagesExists) {
                File imgItemMenuFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Items/" + id + ".png");
                StorageReference imgRef = stRef.child("/Items").child(id + ".png");
                Uri file = Uri.fromFile(imgItemMenuFile);
                UploadTask uploadTask = imgRef.putFile(file);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return imgRef.getDownloadUrl();
                    }
                });

                urlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        for (int i = 0; i < itemsB.size(); i++) {
                            if (itemsB.get(i).getId() == Integer.parseInt(id)) {
                                itemsB.get(i).setImage(task.getResult().toString());
                            }
                        }
                    }
                });
            }

            for (int i = 0; i < itemsB.size(); i++){
                dbRef.child("menu").child("ClassB").child(String.valueOf(itemsB.get(i).getId())).setValue(itemsB.get(i));
            }

            /* Upload Logo */
            File imgLogo = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/General/logo.png");
            StorageReference logoRef = stRef.child("/General").child( "logo.png");
            Uri file = Uri.fromFile(imgLogo);
            UploadTask uploadTask = logoRef.putFile(file);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return logoRef.getDownloadUrl();
                }
            });

            urlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    String url = task.getResult().toString();
                    SharedPreferences GsPrefs = AdminPanel.this.getSharedPreferences("gsprefs", MODE_PRIVATE);
                    GsPrefs.edit().putString("Logo", url).apply();
                }
            });

        }

        private void DownloadImagesTapped(){
            for (ClassB item : itemsB){
                ImageDownloader.downloadImage(item.getImage(), String.valueOf(item.getId()));
            }


        }





}