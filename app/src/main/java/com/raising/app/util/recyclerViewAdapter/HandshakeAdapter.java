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

import com.raising.app.R;
import com.raising.app.models.HandshakeItem;
import com.raising.app.models.LeadState;

import java.util.ArrayList;

public class HandshakeAdapter extends RecyclerView.Adapter<HandshakeAdapter.ViewHolder> {
    private ArrayList<HandshakeItem> recyclerItems;
    private OnItemClickListener itemClickListener;
    private LeadState stateEnum;

    public HandshakeAdapter(ArrayList<HandshakeItem> recyclerItems, LeadState stateEnum) {
        this.recyclerItems = recyclerItems;
        this.stateEnum = stateEnum;
    }

    @NonNull
    @Override
    public HandshakeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_handshake,
                parent, false);
        return new ViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HandshakeAdapter.ViewHolder holder, int position) {
        HandshakeItem recyclerItem = recyclerItems.get(position);

        switch (recyclerItem.getHandshakeState()) {
            case HANDSHAKE_REQUESTED:
                //TODO: insert one hand
                break;
            case HANDSHAKE_ACCEPTED:
                holder.statusIcon.setImageDrawable(
                        ContextCompat.getDrawable(holder.statusIcon.getContext(),
                                R.drawable.ic_handshake_24dp));
                holder.statusIcon.setBackgroundColor(
                        ContextCompat.getColor(holder.statusIcon.getContext(),
                                R.color.raisingSecondaryDark));
                break;
            case HANDSHAKE_DECLINED:
                holder.statusIcon.setImageDrawable(
                        ContextCompat.getDrawable(holder.statusIcon.getContext(),
                                R.drawable.ic_cancel_24dp));
                holder.statusIcon.setBackgroundColor(
                        ContextCompat.getColor(holder.statusIcon.getContext(),
                                R.color.raisingNegative));
                break;
            case CONTACT_SUCCESS:
            case CONTACT_ACCEPTED:
                holder.statusIcon.setImageDrawable(
                        ContextCompat.getDrawable(holder.statusIcon.getContext(),
                                R.drawable.ic_check_circle_24dp));
                holder.statusIcon.setBackgroundColor(
                        ContextCompat.getColor(holder.statusIcon.getContext(),
                        R.color.raisingPositive));
                break;
            case CONTACT_REQUESTED:
        }

        // Default: set visibility of warning to gone
        holder.warning.setVisibility(View.GONE);

        holder.name.setText(recyclerItem.getName());
        holder.attribute.setText(recyclerItem.getAttribute());
        holder.matchingPercent.setText(recyclerItem.getHandshakePercentString());

        holder.profilePicture.setImageBitmap(recyclerItem.getBitmap());

        switch (stateEnum) {
            case YOUR_TURN:
                holder.statusIcon.setVisibility(View.GONE);
                break;
            case PENDING:
            case CLOSED:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private TextView name, attribute, matchingPercent;
        private ImageView profilePicture, statusIcon, warning;

        public ViewHolder(@NonNull View itemView, OnItemClickListener itemClickListener) {
            super(itemView);

            card = itemView.findViewById(R.id.item_handshake_card);

            name = itemView.findViewById(R.id.item_handshake_name);
            attribute = itemView.findViewById(R.id.item_handshake_attributes);
            matchingPercent = itemView.findViewById(R.id.item_handshake_match_percent);

            statusIcon = itemView.findViewById(R.id.item_handshake_status_icon);
            profilePicture = itemView.findViewById(R.id.item_handshake_profile_image);
            warning = itemView.findViewById(R.id.item_handshake_warning);

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
