package com.raising.app.util.matchingCriteriaComponent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.Model;
import com.raising.app.util.CircleImageDrawable;

import java.util.ArrayList;

public class MatchingCriteriaAdapter extends RecyclerView.Adapter<MatchingCriteriaAdapter.ViewHolder> {
    private ArrayList<? extends Model> recyclerItems;
    private OnItemClickListener clickListener;
    private Context context;
    private boolean singleSelect = false;

    public MatchingCriteriaAdapter(Context context, ArrayList<? extends Model> recyclerItems, boolean singleSelect) {
        this.recyclerItems = recyclerItems;
        this.context = context;
        this.singleSelect = singleSelect;
    }

    @NonNull
    @Override
    public MatchingCriteriaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matching_criteria,
                parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchingCriteriaAdapter.ViewHolder holder, int position) {
        Model item = recyclerItems.get(position);

        //TODO: Enter real colors
        int standardBackgroundColor = Color.parseColor("#CCD5D9");
        int standardForegroundColor = Color.parseColor("#82ABC2");
        int selectedForegroundColor = Color.parseColor("#941E23");
        int selectedBackgroundColor = Color.parseColor("#DDA7AB");

        CircleImageDrawable icon = new CircleImageDrawable(item.getImage(), 1, standardBackgroundColor,
                standardForegroundColor);
        CircleImageDrawable iconChecked = new CircleImageDrawable(item.getImage(), 1,
                selectedBackgroundColor, selectedForegroundColor);

        holder.name.setText(item.getName());
        holder.name.setMaxWidth(150);
        holder.name.setSingleLine(false);

        if(item.isChecked()) {
            holder.icon.setImageDrawable(iconChecked);
        } else {
            holder.icon.setImageDrawable(icon);
        }

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setChecked(!item.isChecked());

                if(item.isChecked()) {
                    holder.icon.setImageDrawable(iconChecked);
                    if(singleSelect) {
                        recyclerItems.forEach(i -> {
                            if (i.getId() != item.getId()) {
                                i.setChecked(false);
                            }
                        });

                        notifyDataSetChanged();
                    }
                } else {
                    holder.icon.setImageDrawable(icon);
                }

                clickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView icon;

        public ViewHolder(@NonNull View itemView, OnItemClickListener clickListener) {
            super(itemView);

            name = itemView.findViewById(R.id.criteria_name);
            icon = itemView.findViewById(R.id.criteria_icon);
        }
    }
}
