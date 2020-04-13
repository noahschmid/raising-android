package com.raising.app.util.recyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.HandshakeItem;
import com.raising.app.models.HandshakeState;

import java.util.ArrayList;

public class HandshakeAdapter extends RecyclerView.Adapter<HandshakeAdapter.ViewHolder> {
    private ArrayList<HandshakeItem> recyclerItems;
    private OnClickListener clickListener;
    private Enum<HandshakeState> stateEnum;

    public HandshakeAdapter(ArrayList<HandshakeItem> recyclerItems, Enum<HandshakeState> stateEnum) {
        this.recyclerItems = recyclerItems;
        this.stateEnum = stateEnum;
    }

    @NonNull
    @Override
    public HandshakeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_handshake,
                parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HandshakeAdapter.ViewHolder holder, int position) {
        HandshakeItem recyclerItem = recyclerItems.get(position);

        holder.name.setText(recyclerItem.getName());
        holder.attribute.setText(recyclerItem.getAttribute());
        holder.sentence.setText(recyclerItem.getSentence());
        holder.matchingPercent.setText(recyclerItem.getHandshakePercentString());

        holder.profilePicture.setImageBitmap(recyclerItem.getBitmap());

        // button and statusIcon contain different content, based on HandshakeState
        if(stateEnum.equals(HandshakeState.YOUR_TURN)) {
            holder.interactionButton.setText(R.string.handshake_get_in_touch);

        } else if (stateEnum.equals(HandshakeState.PENDING)) {
            holder.interactionButton.setText(R.string.handshake_get_in_touch);

        } else if (stateEnum.equals(HandshakeState.CLOSED)) {
            holder.interactionButton.setText(R.string.handshake_contact);

        }
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        clickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, attribute, matchingPercent, sentence;
        private ImageView profilePicture, statusIcon;
        private Button interactionButton;

        public ViewHolder(@NonNull View itemView, OnClickListener clickListener) {
            super(itemView);

            name = itemView.findViewById(R.id.item_handshake_name);
            attribute = itemView.findViewById(R.id.item_handshake_attributes);
            matchingPercent = itemView.findViewById(R.id.item_handshake_match_percent);
            sentence = itemView.findViewById(R.id.item_handshake_sentence);

            statusIcon = itemView.findViewById(R.id.item_handshake_status_icon);
            profilePicture = itemView.findViewById(R.id.item_handshake_profile_image);

            interactionButton = itemView.findViewById(R.id.button_handshake_interact);
            interactionButton.setOnClickListener(v -> {
                if(clickListener != null) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION) {
                        clickListener.onClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
