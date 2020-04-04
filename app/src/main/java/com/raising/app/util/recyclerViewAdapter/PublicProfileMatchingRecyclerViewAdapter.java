package com.raising.app.util.recyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.MatchingCriterion;

import java.util.ArrayList;

public class PublicProfileMatchingRecyclerViewAdapter extends RecyclerView.Adapter<PublicProfileMatchingRecyclerViewAdapter.ViewHolder> {
    private ArrayList<MatchingCriterion> recyclerItems;

    public PublicProfileMatchingRecyclerViewAdapter(ArrayList<MatchingCriterion> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public PublicProfileMatchingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_public_profile_matching_criteria,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicProfileMatchingRecyclerViewAdapter.ViewHolder holder, int position) {
        // TODO: replace with actual icon
        holder.matchingCriterionIcon.setImageResource(R.drawable.ic_android_24dp);
        holder.matchingCriterionText.setText(recyclerItems.get(position).getTitle());
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
