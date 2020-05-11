package com.raising.app.util.matchingCriteriaComponent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.Model;
import com.raising.app.util.CircleImageDrawable;

import java.util.ArrayList;

public class MatchingCriteriaAdapter extends RecyclerView.Adapter<MatchingCriteriaAdapter.ViewHolder> {
    private final String TAG = "MatchingCriteriaAdapter";
    private ArrayList<? extends Model> recyclerItems;
    private OnItemClickListener clickListener;
    private Context context;
    private boolean singleSelect;

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

        int standardBackgroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.raisingSecondaryAccent);
        int standardForegroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.raisingSecondaryDark);
        int selectedBackgroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.raisingPrimaryAccent);
        int selectedForegroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.raisingPrimaryDark);

        holder.name.setText(item.getName());
        holder.name.setMaxWidth(150);
        holder.name.setSingleLine(false);

        BitmapDrawable iconChecked = new BitmapDrawable(holder.itemView.getResources(), item.getImage());
        iconChecked.setTint(selectedForegroundColor);

        BitmapDrawable icon = new BitmapDrawable(holder.itemView.getResources(), item.getImage());
        icon.setTint(standardForegroundColor);

        if (item.isChecked()) {
            holder.icon.setImageDrawable(iconChecked);
            holder.icon.getBackground().setTint(selectedBackgroundColor);
        } else {
            holder.icon.setImageDrawable(icon);
            holder.icon.getBackground().setTint(standardBackgroundColor);
        }

        holder.icon.setOnClickListener(v -> {
            item.setChecked(!item.isChecked());

            if (item.isChecked()) {
                holder.icon.getBackground().setTint(selectedBackgroundColor);
                holder.icon.setImageDrawable(iconChecked);
                if (singleSelect) {
                    recyclerItems.forEach(i -> {
                        if (i.getId() != item.getId()) {
                            i.setChecked(false);
                        }
                    });
                    notifyDataSetChanged();
                }
            } else {
                holder.icon.getBackground().setTint(standardBackgroundColor);
                holder.icon.setImageDrawable(icon);
            }
            clickListener.onItemClick(position);
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
