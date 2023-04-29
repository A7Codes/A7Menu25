package com.a7codes.menu25.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;

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

            // Move this initialization to constructor so that its not initalized again and again.
            Animation anim = AnimationUtils.loadAnimation(context,R.anim.grid_anim);

            // By default all grid items will animate together and will look like the gridview is
            // animating as a whole. So, experiment with incremental delays as below to get a
            // wave effect.
            anim.setStartOffset(position * 500);

            listItemView.setAnimation(anim);
            anim.start();
        }

        ClassB itemB = itemsB.get(position);
        ConstraintLayout root = listItemView.findViewById(R.id.am_class_b_row_root);
        root.setMinHeight(root.getMinWidth());

        ConstraintLayout titleRoot = listItemView.findViewById(R.id.am_class_b_row_titleRoot);
        ImageView img = listItemView.findViewById(R.id.am_class_b_row_img);
        TextView title = listItemView.findViewById(R.id.am_class_b_row_title);
        TextView price = listItemView.findViewById(R.id.am_class_b_row_price);
        title.setText(itemB.getTitle());
        price.setText(formatNumberWithCommas(Integer.parseInt(itemB.getPrice())) + " IQD");

        File imgItemMenuFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Items/" + itemB.getId() + ".png");

        if (imgItemMenuFile.exists()){
            Glide.with(context)
                    .load(imgItemMenuFile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(img);
        } else {
            Glide.with(context)
                    .load(itemB.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(img);
        }

        /* General Prefs */
        SharedPreferences GsPrefs = context.getSharedPreferences("gsprefs", MODE_PRIVATE);
        String Color2 = "";
        Color2 = GsPrefs.getString("Color2", "");
        Drawable bgTitleRoot = titleRoot.getBackground();
        bgTitleRoot = DrawableCompat.wrap(bgTitleRoot);
        int tintColor = Color.parseColor(Color2);
        DrawableCompat.setTint(bgTitleRoot, tintColor);
        titleRoot.setBackground(bgTitleRoot);

        price.setTextColor(tintColor);

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

    /* Format Price */
    public static String formatNumberWithCommas(int number) {
        String numberStr = Integer.toString(number);
        StringBuilder formattedNumber = new StringBuilder();
        int count = 0;

        for (int i = numberStr.length() - 1; i >= 0; i--) {
            count++;
            formattedNumber.append(numberStr.charAt(i));
            if (count % 3 == 0 && i != 0) {
                formattedNumber.append(",");
            }
        }

        return formattedNumber.reverse().toString();
    }
}
