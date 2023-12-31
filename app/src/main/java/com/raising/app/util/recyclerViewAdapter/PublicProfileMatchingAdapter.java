package com.raising.app.util.recyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.Model;
import com.raising.app.util.CircleImageDrawable;

import java.util.ArrayList;

public class PublicProfileMatchingAdapter extends RecyclerView.Adapter<PublicProfileMatchingAdapter.ViewHolder> {
    private ArrayList<? extends Model> recyclerItems;

    public PublicProfileMatchingAdapter(ArrayList<? extends Model> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public PublicProfileMatchingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_public_profile_matching_criteria,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicProfileMatchingAdapter.ViewHolder holder, int position) {
        Model recyclerItem = recyclerItems.get(position);
        /*
        int standardBackgroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.raisingSecondaryAccent);
        int standardForegroundColor = R.color.raisingSecondaryDark;

        CircleImageDrawable icon = new CircleImageDrawable(recyclerItem.getImage(), 1,
                standardBackgroundColor, standardForegroundColor);
         */
        holder.matchingCriterionIcon.setImageBitmap(recyclerItem.getImage());
        holder.matchingCriterionText.setText(recyclerItem.getName());
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView matchingCriterionIcon;
        private TextView matchingCriterionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            matchingCriterionIcon = itemView.findViewById(R.id.public_profile_matching_icon);
            matchingCriterionText = itemView.findViewById(R.id.public_profile_matching_text);
        }
    }
}
