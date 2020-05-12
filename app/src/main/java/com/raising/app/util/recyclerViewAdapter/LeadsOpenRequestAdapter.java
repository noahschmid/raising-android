package com.raising.app.util.recyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.raising.app.R;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.InternalStorageHandler;

import java.util.ArrayList;

public class LeadsOpenRequestAdapter extends RecyclerView.Adapter<LeadsOpenRequestAdapter.ViewHolder> {
    private ArrayList<Lead> recyclerItems;
    private OnClickListener clickListener;
    private OnItemClickListener itemClickListener;

    public LeadsOpenRequestAdapter(ArrayList<Lead> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public LeadsOpenRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.item_lead_open_request, parent, false);
       return new ViewHolder(view, clickListener, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadsOpenRequestAdapter.ViewHolder holder, int position) {
        Lead recyclerItem = recyclerItems.get(position);

        holder.name.setText(recyclerItem.getTitle());
        holder.attribute.setText(recyclerItem.getAttribute());

        if(recyclerItem.getProfilePictureId() > 0) {
            Glide
                    .with(InternalStorageHandler.getContext())
                    .load(ApiRequestHandler.getDomain() + "media/profilepicture/" +
                            recyclerItem.getProfilePictureId())
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.ic_placeholder_24dp)
                    .into(holder.image);
        } else {
            holder.image.setImageDrawable(InternalStorageHandler.getContext()
                    .getResources().getDrawable(R.drawable.ic_person_24dp));
        }
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
        private ImageView image;
        private TextView name, attribute;
        private ImageButton decline;
        private MaterialButton accept;

        public ViewHolder(@NonNull View itemView, OnClickListener clickListener, OnItemClickListener itemClickListener) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if(itemClickListener != null) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });

            image = itemView.findViewById(R.id.leads_open_request_image);

            name = itemView.findViewById(R.id.leads_open_request_name);
            attribute = itemView.findViewById(R.id.leads_open_request_attribute);

            accept = itemView.findViewById(R.id.button_leads_open_request_accept);
            accept.setOnClickListener(v -> {
                if (clickListener != null) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        clickListener.onClickAccept(getAdapterPosition());
                    }
                }
            });
            decline = itemView.findViewById(R.id.button_leads_open_request_decline);
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
