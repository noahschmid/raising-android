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

        setupMatchingPercentGraphic(holder, recyclerItem);
    }

    private void setupMatchingPercentGraphic(MatchlistAdapter.ViewHolder holder, MatchlistItem recyclerItem) {
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(recyclerItem.getMatchingPercent(), "MatchingPercent"));
        float remainder = 100 - recyclerItem.getMatchingPercent();
        pieEntries.add(new PieEntry(remainder, "Remainder"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "Matching Percentage");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(1f);

        PieData pieData = new PieData(dataSet);
        holder.matchingPercentGraphic.setData(pieData);
        holder.matchingPercentGraphic.setHoleRadius(0f);
        holder.matchingPercentGraphic.animateY(1000);
        holder.matchingPercentGraphic.invalidate();

        //disable chart description
        Description description = holder.matchingPercentGraphic.getDescription();
        description.setEnabled(false);

        //disable legend
        Legend legend = holder.matchingPercentGraphic.getLegend();
        legend.setEnabled(false);
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
