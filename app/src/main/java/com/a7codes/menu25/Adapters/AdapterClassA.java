package com.a7codes.menu25.Adapters;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a7codes.menu25.Classes.ClassA;
import com.a7codes.menu25.R;

import java.util.List;

public class AdapterClassA  extends RecyclerView.Adapter<AdapterClassA.ViewHolder>{

    Context context;
    List<ClassA> itemsA;
    private LayoutInflater mInflater;

    public AdapterClassA(Context context, List<ClassA> itemsA) {
        this.context = context;
        this.itemsA = itemsA;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.am_class_a_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(itemsA.get(position).getTitle());
        holder.icon.setImageResource(iconSelector(itemsA.get(position).getIcon()));
    }

    @Override
    public int getItemCount() {
        return itemsA.size();
    }

    private int iconSelector (String iconName) {
        switch (iconName) {
            case "Burger":
                return R.drawable.ic_burger;
            case "ColdDrink":
                return R.drawable.ic_colddrink;
            case "HotDrink":
                return R.drawable.ic_hotdrink;
            case "MainDish":
            default:
                return R.drawable.ic_main;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.am_class_a_row_icon);
            title = itemView.findViewById(R.id.am_class_a_row_title);
        }
    }
}
