package com.raising.app.util.recyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;

import java.util.ArrayList;

public class PublicProfileMatchingRecyclerViewAdapter extends RecyclerView.Adapter<PublicProfileMatchingRecyclerViewAdapter.ViewHolder> {
    private ArrayList recyclerItems;

    public PublicProfileMatchingRecyclerViewAdapter(ArrayList recyclerItems) {
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
        //TODO: set values to matchingCriteriaIcon, matchingCriteriaText

    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView matchingCriteriaIcon;
        private TextView matchingCriteriaText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            matchingCriteriaIcon = itemView.findViewById(R.id.public_profile_matching_icon);
            matchingCriteriaText = itemView.findViewById(R.id.public_profile_matching_text);
        }
    }
}
