package com.raising.app.util.matchingCriteriaComponent;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.MatchingCriteriaItem;
import com.raising.app.models.Model;

import java.util.ArrayList;

public class MatchingCriteriaAdapter extends RecyclerView.Adapter<MatchingCriteriaAdapter.ViewHolder> {
    private final String TAG = "MatchingCriteriaAdapter";
    private ArrayList<MatchingCriteriaItem> recyclerItems;
    private OnItemClickListener clickListener;
    private Context context;
    private boolean singleSelect;
    private boolean isLabelsLayout;

    public MatchingCriteriaAdapter(Context context, ArrayList<MatchingCriteriaItem> recyclerItems, boolean singleSelect, boolean isLabelsLayout) {
        this.recyclerItems = recyclerItems;
        this.context = context;
        this.singleSelect = singleSelect;
        this.isLabelsLayout = isLabelsLayout;
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
        Log.d(TAG, "onBindViewHolder: RecylcerItem" + recyclerItems.get(position).getName() + " " + recyclerItems.get(position).isChecked());

        int standardBackgroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.raisingSecondaryAccent);
        int standardForegroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.raisingSecondaryDark);
        int selectedBackgroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.raisingPrimaryAccent);
        int selectedForegroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.raisingPrimaryDark);

        holder.name.setText(item.getName());
        holder.name.setMaxWidth(150);
        holder.name.setSingleLine(false);

        // checked icon
        BitmapDrawable iconChecked = new BitmapDrawable(holder.itemView.getResources(), item.getImage());
        if (!isLabelsLayout)
            iconChecked.setTint(selectedForegroundColor);

        // unchecked icon
        BitmapDrawable icon = new BitmapDrawable(holder.itemView.getResources(), item.getImage());
        if (!isLabelsLayout)
            icon.setTint(standardForegroundColor);

        if (item.isChecked()) {
            holder.icon.setImageDrawable(iconChecked);
            holder.background.getBackground().setTint(selectedBackgroundColor);
        } else {
            holder.icon.setImageDrawable(icon);
            holder.background.getBackground().setTint(standardBackgroundColor);
        }

        holder.itemView.setOnClickListener(v -> {
            item.setChecked(!item.isChecked());

            if (item.isChecked()) {
                if (singleSelect) {
                    recyclerItems.forEach(i -> {
                        if (i.getId() != item.getId()) {
                            i.setChecked(false);
                        }
                    });
                }
            }
            clickListener.onItemClick(position);
            notifyDataSetChanged();
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
        private ImageView icon, background;

        public ViewHolder(@NonNull View itemView, OnItemClickListener clickListener) {
            super(itemView);

            name = itemView.findViewById(R.id.matching_criteria_name);
            icon = itemView.findViewById(R.id.matching_criteria_icon);
            background = itemView.findViewById(R.id.matching_criteria_background);
        }
    }
}
