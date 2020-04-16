package com.raising.app.util.recyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.HandshakeOpenRequestItem;

import java.util.ArrayList;

public class HandshakeOpenRequestAdapter extends RecyclerView.Adapter<HandshakeOpenRequestAdapter.ViewHolder> {
    private ArrayList<HandshakeOpenRequestItem> recyclerItems;
    private OnClickListener clickListener;
    private OnItemClickListener itemClickListener;

    public HandshakeOpenRequestAdapter(ArrayList<HandshakeOpenRequestItem> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public HandshakeOpenRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.item_handshake_open_request, parent, false);
       return new ViewHolder(view, clickListener, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HandshakeOpenRequestAdapter.ViewHolder holder, int position) {
        HandshakeOpenRequestItem recyclerItem = recyclerItems.get(position);

        holder.name.setText(recyclerItem.getName());
        holder.attribute.setText(recyclerItem.getAttribute());

        holder.image.setImageBitmap(recyclerItem.getBitmap());
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public interface OnClickListener {
        void onClickAccept(int position);
        void onClickDecline(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        clickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private ImageView image;
        private TextView name, attribute;
        private Button accept, decline;

        public ViewHolder(@NonNull View itemView, OnClickListener clickListener, OnItemClickListener itemClickListener) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if(itemClickListener != null) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });

            image = itemView.findViewById(R.id.handshake_open_request_image);

            name = itemView.findViewById(R.id.handshake_open_request_name);
            attribute = itemView.findViewById(R.id.handshake_open_request_attribute);

            accept = itemView.findViewById(R.id.button_handshake_open_request_accept);
            accept.setOnClickListener(v -> {
                if (clickListener != null) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        clickListener.onClickAccept(getAdapterPosition());
                    }
                }
            });
            decline = itemView.findViewById(R.id.button_handshake_open_request_decline);
            decline.setOnClickListener(v -> {
                if (clickListener != null) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        clickListener.onClickDecline(getAdapterPosition());
                    }
                }
            });

        }
    }
}
