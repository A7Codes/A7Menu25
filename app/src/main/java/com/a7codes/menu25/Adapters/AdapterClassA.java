package com.a7codes.menu25.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a7codes.menu25.Classes.ClassA;
import com.a7codes.menu25.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterClassA extends RecyclerView.Adapter<AdapterClassA.ViewHolder> {

    Context context;
    List<ClassA> itemsA;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;

    public AdapterClassA(Context context, List<ClassA> itemsA) {
        this.context = context;
        this.itemsA = itemsA;
        this.mInflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.am_class_a_row, parent, false);
        return new ViewHolder(view, mListener);
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

    private int iconSelector(String iconName) {

        final ArrayList<String> ClassAIcons = new ArrayList<>();
        ClassAIcons.add("ic_starters");
        ClassAIcons.add("ic_salad");
        ClassAIcons.add("ic_dishes");
        ClassAIcons.add("ic_sandwich");
        ClassAIcons.add("ic_fastfood");
        ClassAIcons.add("ic_dessert");
        ClassAIcons.add("ic_hotdrink");
        ClassAIcons.add("ic_colddrink");

        switch (iconName) {
            case "ic_starters":
                return R.drawable.ic_starters;
            case "ic_salad":
                return R.drawable.ic_salad;
            case "ic_dishes":
                return R.drawable.ic_dishes;
            case "ic_sandwich":
                return R.drawable.ic_sandwich;
            case "ic_fastfood":
                return R.drawable.ic_fastfood;
            case "ic_dessert":
                return R.drawable.ic_dessert;
            case "ic_hotdrink":
                return R.drawable.ic_hotdrink;
            case "ic_colddrink":
                return R.drawable.ic_colddrink;
            default:
                return R.drawable.ic_main;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            icon = itemView.findViewById(R.id.am_class_a_row_icon);
            title = itemView.findViewById(R.id.am_class_a_row_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
