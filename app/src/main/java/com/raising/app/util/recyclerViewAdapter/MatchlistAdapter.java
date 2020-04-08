package com.raising.app.util.recyclerViewAdapter;

/*
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.raising.app.R;
import com.raising.app.models.MatchlistItem;

import java.util.ArrayList;
import java.util.List;

public class MatchlistAdapter extends RecyclerView.Adapter<MatchlistAdapter.ViewHolder> {
    private ArrayList<MatchlistItem> recyclerItems;

    public MatchlistAdapter(ArrayList<MatchlistItem> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public MatchlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matchlist,
               parent, false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchlistAdapter.ViewHolder holder, int position) {
        MatchlistItem recyclerItem = recyclerItems.get(position);

        holder.name.setText(recyclerItem.getName());
        holder.attributes.setText(recyclerItem.getAttribute());
        holder.sentence.setText(recyclerItem.getSentence());
        holder.matchingPercent.setText(recyclerItem.getMatchingPercentString());
        //TODO: replace with actual image
        holder.profileImage.setImageResource(R.drawable.ic_android_24dp);
/*
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry((float) recyclerItem.getMatchingPercent(), "MatchingPercent"));
        float remainder = (float) (100 - recyclerItem.getMatchingPercent());
        pieEntries.add(new PieEntry(remainder, "Remainder"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "Matching Percentage");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(R.color.raisingPrimary);
        colors.add(R.color.raisingPrimary);
        dataSet.setColors(colors);
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, attributes, sentence, matchingPercent;
        private ImageView profileImage;
        private PieChart matchingPercentGraphic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_matchlist_name);
            attributes = itemView.findViewById(R.id.item_matchlist_attributes);
            sentence = itemView.findViewById(R.id.item_matchlist_sentence);
            matchingPercent = itemView.findViewById(R.id.item_matchlist_match_percent);

            profileImage = itemView.findViewById(R.id.item_matchlist_profile_image);

            matchingPercentGraphic = itemView.findViewById(R.id.item_matchlist_chart);
        }
    }
}*/
