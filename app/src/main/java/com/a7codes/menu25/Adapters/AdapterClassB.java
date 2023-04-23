package com.a7codes.menu25.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.a7codes.menu25.Classes.ClassB;
import com.a7codes.menu25.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

public class AdapterClassB extends ArrayAdapter<ClassB> {

    ArrayList<ClassB> itemsB = new ArrayList<>();
    Context context;
    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ClassB item);
    }

    public AdapterClassB(Context context, ArrayList<ClassB> itemsB, OnItemClickListener onItemClickListener) {
        super(context, 0, itemsB);
        this.context = context;
        this.itemsB = itemsB;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.am_class_b_row, parent, false);
        }

        ClassB itemB = itemsB.get(position);
        ConstraintLayout root = listItemView.findViewById(R.id.am_class_b_row_root);
        root.setMinHeight(root.getMinWidth());

        ImageView img = listItemView.findViewById(R.id.am_class_b_row_img);
        TextView title = listItemView.findViewById(R.id.am_class_b_row_title);
        TextView price = listItemView.findViewById(R.id.am_class_b_row_price);
        title.setText(itemB.getTitle());
        price.setText(itemB.getPrice());

        File imgItemMenuFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Items/" + itemB.getId() + ".png");

        if (imgItemMenuFile.exists()){
            Glide.with(context)
                    .load(imgItemMenuFile)
                    .into(img);
        } else {
            Glide.with(context)
                    .load(itemB.getImage())
                    .into(img);
        }



        /* General Prefs */
        SharedPreferences GsPrefs = context.getSharedPreferences("gsprefs", MODE_PRIVATE);
        String Color2 = "";
        Color2 = GsPrefs.getString("Color2", "");

//        img.setForeground(new ColorDrawable(Color.parseColor(Color2)));
        img.setForeground(new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[] {Color.parseColor(Color2), Color.TRANSPARENT}));


        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(itemB);
                }
            }
        });

        return listItemView;
    }
}
