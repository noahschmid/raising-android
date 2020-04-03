package com.raising.app.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.stakeholder.Founder;

import java.util.ArrayList;

public class StartupProfileFounderRecyclerViewAdapter extends RecyclerView.Adapter<StartupProfileFounderRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Founder> recyclerItems;

    public StartupProfileFounderRecyclerViewAdapter(ArrayList<Founder> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_startup_public_profile_founder_recylcer_view,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StartupProfileFounderRecyclerViewAdapter.ViewHolder holder,
                                 int position) {
        Founder recyclerItem = recyclerItems.get(position);

        holder.founderPosition.setText(recyclerItem.getPosition());
        holder.founderName.setText(recyclerItem.getTitle());
        holder.founderEducation.setText(recyclerItem.getEducation());
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView founderPosition, founderName, founderEducation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            founderPosition = itemView.findViewById(R.id.text_startup_founder_position);
            founderName = itemView.findViewById(R.id.text_startup_founder_name);
            founderEducation = itemView.findViewById(R.id.text_startup_founder_education);
        }
    }


}
