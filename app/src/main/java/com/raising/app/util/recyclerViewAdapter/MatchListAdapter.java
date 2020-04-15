package com.raising.app.util.recyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.raising.app.R;
import com.raising.app.models.MatchListItem;

import java.util.ArrayList;
import java.util.List;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.ViewHolder> {
    private ArrayList<MatchListItem> recyclerItems;
    private OnItemClickListener clickListener;

    public MatchListAdapter(ArrayList<MatchListItem> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public MatchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_list,
                parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchListAdapter.ViewHolder holder, int position) {
        MatchListItem recyclerItem = recyclerItems.get(position);

        // array holding background colors of match list items
        int [] cardBackground = {
                ContextCompat.getColor(holder.card.getContext(), R.color.raisingSecondaryLight),
                ContextCompat.getColor(holder.card.getContext(), R.color.raisingWhite)};

        holder.card.setBackgroundColor(cardBackground[position % 2]);

        holder.name.setText(recyclerItem.getName());
        holder.attribute.setText(recyclerItem.getAttribute());
        holder.sentence.setText(recyclerItem.getSentence());
        holder.matchingPercent.setText(recyclerItem.getMatchingPercentString());
        //TODO: replace with actual image
        holder.profileImage.setImageBitmap(recyclerItem.getBitmap());

        setupMatchingPercentGraphic(holder.matchingPercentGraphic, recyclerItem);
    }

    private void setupMatchingPercentGraphic(PieChart percentChart, MatchListItem recyclerItem) {
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(recyclerItem.getMatchingPercent(), "MatchingPercent"));
        // float remainder = 100 - recyclerItem.getMatchingPercent();
        // pieEntries.add(new PieEntry(remainder, "Remainder"));

        // helper array to define colors of the pie chart
        int [] colors = {
                ContextCompat.getColor(percentChart.getContext(), R.color.raisingSecondaryDark),
                ContextCompat.getColor(percentChart.getContext(), R.color.raisingWhite)};

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

        percentChart.setUsePercentValues(true);
        percentChart.setRotationEnabled(false);

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
        private CardView card;
        private TextView name, attribute, sentence, matchingPercent;
        private ImageView profileImage;
        private PieChart matchingPercentGraphic;

        public ViewHolder(@NonNull View itemView, OnItemClickListener clickListener) {
            super(itemView);

            card = itemView.findViewById(R.id.item_matchList_card);

            name = itemView.findViewById(R.id.item_matchList_name);
            attribute = itemView.findViewById(R.id.item_matchList_attributes);
            sentence = itemView.findViewById(R.id.item_matchList_sentence);
            matchingPercent = itemView.findViewById(R.id.item_matchList_match_percent);
            profileImage = itemView.findViewById(R.id.item_matchList_profile_image);
            matchingPercentGraphic = itemView.findViewById(R.id.item_matchList_chart);

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
