package com.raising.app.util.recyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.raising.app.R;
import com.raising.app.models.MatchlistItem;

import java.util.ArrayList;
import java.util.List;

public class MatchlistAdapter extends RecyclerView.Adapter<MatchlistAdapter.ViewHolder> {
    private ArrayList<MatchlistItem> recyclerItems;
    private int[] colors;
    private OnItemClickListener clickListener;

    public MatchlistAdapter(ArrayList<MatchlistItem> recyclerItems, int[] colors) {
        this.recyclerItems = recyclerItems;
        this.colors = colors.clone();
    }

    @NonNull
    @Override
    public MatchlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matchlist,
                parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchlistAdapter.ViewHolder holder, int position) {
        MatchlistItem recyclerItem = recyclerItems.get(position);

        holder.name.setText(recyclerItem.getName());
        holder.attribute.setText(recyclerItem.getAttribute());
        holder.sentence.setText(recyclerItem.getSentence());
        holder.matchingPercent.setText(recyclerItem.getMatchingPercentString());
        //TODO: replace with actual image
        holder.profileImage.setImageBitmap(recyclerItem.getBitmap());

        setupMatchingPercentGraphic(holder.matchingPercentGraphic, recyclerItem);
    }

    private void setupMatchingPercentGraphic(PieChart percentChart, MatchlistItem recyclerItem) {
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(recyclerItem.getMatchingPercent(), "MatchingPercent"));
        float remainder = 100 - recyclerItem.getMatchingPercent();
        pieEntries.add(new PieEntry(remainder, "Remainder"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "Matching Percentage");
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        //disable values
        pieData.setDrawValues(false);

        percentChart.setData(pieData);
        //disable entry labels
        percentChart.setDrawEntryLabels(false);
        //disable center hole
        percentChart.setHoleRadius(0f);
        percentChart.setTransparentCircleRadius(0f);
        //disable legend and description
        percentChart.getDescription().setEnabled(false);
        percentChart.getLegend().setEnabled(false);

        percentChart.animateY(1000);
        percentChart.invalidate();
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
        private TextView name, attribute, sentence, matchingPercent;
        private ImageView profileImage;
        private PieChart matchingPercentGraphic;

        public ViewHolder(@NonNull View itemView, OnItemClickListener clickListener) {
            super(itemView);

            name = itemView.findViewById(R.id.item_matchlist_name);
            attribute = itemView.findViewById(R.id.item_matchlist_attributes);
            sentence = itemView.findViewById(R.id.item_matchlist_sentence);
            matchingPercent = itemView.findViewById(R.id.item_matchlist_match_percent);
            profileImage = itemView.findViewById(R.id.item_matchlist_profile_image);
            matchingPercentGraphic = itemView.findViewById(R.id.item_matchlist_chart);

            itemView.setOnClickListener(v -> {
                if(clickListener != null) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
