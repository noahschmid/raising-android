package com.raising.app.util.recyclerViewAdapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.raising.app.R;
import com.raising.app.models.leads.Lead;
import com.raising.app.models.leads.LeadState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.InternalStorageHandler;

import java.util.ArrayList;

public class LeadsAdapter extends RecyclerView.Adapter<LeadsAdapter.ViewHolder> {
    private ArrayList<Lead> recyclerItems;
    private OnItemClickListener itemClickListener;
    private OnClickListener clickListener;
    private LeadState stateEnum;

    public LeadsAdapter(ArrayList<Lead> recyclerItems, LeadState stateEnum) {
        this.recyclerItems = recyclerItems;
        this.stateEnum = stateEnum;
    }

    @NonNull
    @Override
    public LeadsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lead,
                parent, false);
        return new ViewHolder(view, itemClickListener, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadsAdapter.ViewHolder holder, int position) {
        Lead recyclerItem = recyclerItems.get(position);

        int drawableId = R.drawable.ic_raising_handshake_full, tintColor = R.color.raisingSecondaryDark;

        switch (recyclerItem.getHandshakeState()) {
            case STARTUP_ACCEPTED:
            case INVESTOR_ACCEPTED:
                //TODO: insert one hand
                tintColor = R.color.raisingSecondaryDark;
                drawableId = R.drawable.ic_raising_handshake_left;
                break;
            case HANDSHAKE:
                if(stateEnum == LeadState.YOUR_TURN || stateEnum == LeadState.PENDING) {
                    tintColor = R.color.raisingSecondaryDark;
                    drawableId = R.drawable.ic_raising_handshake_full;
                } else if(stateEnum == LeadState.CLOSED) {
                    tintColor = R.color.raisingPositive;
                    drawableId = R.drawable.ic_check_circle_24dp;
                }
                break;
            case STARTUP_DECLINED:
            case INVESTOR_DECLINED:
                tintColor = R.color.raisingNegative;
                drawableId = R.drawable.ic_cancel_outline_24dp;
                break;
        }

        Drawable drawable = ContextCompat.getDrawable(holder.statusIcon.getContext(), drawableId);
        drawable = DrawableCompat.wrap(drawable);
        drawable.setTint(ContextCompat.getColor(holder.statusIcon.getContext(), tintColor));
        holder.statusIcon.setImageDrawable(drawable);

        // Default: set visibility of warning to gone
        holder.warning.setVisibility(View.GONE);

        holder.name.setText(recyclerItem.getTitle());
        holder.attribute.setText(recyclerItem.getAttribute());
        holder.matchingPercent.setText(recyclerItem.getHandshakePercentString());


        if(recyclerItem.getProfilePictureId() > 0) {
            Glide
                    .with(InternalStorageHandler.getContext())
                    .load(ApiRequestHandler.getDomain() + "media/profilepicture/" +
                            recyclerItem.getProfilePictureId())
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.ic_placeholder_24dp)
                    .into(holder.profilePicture);
        } else {
            holder.profilePicture.setImageDrawable(InternalStorageHandler.getContext()
                    .getResources().getDrawable(R.drawable.ic_placeholder_24dp));
        }

        switch (stateEnum) {
            case YOUR_TURN:
                //holder.statusIcon.setVisibility(View.GONE);
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

    public interface OnClickListener {
        void onClick(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        clickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, attribute, matchingPercent;
        private ImageView profilePicture, statusIcon, warning;

        public ViewHolder(@NonNull View itemView, OnItemClickListener itemClickListener, OnClickListener clickListener) {
            super(itemView);

            attribute = itemView.findViewById(R.id.item_leads_attributes);
            name = itemView.findViewById(R.id.item_leads_name);

            statusIcon = itemView.findViewById(R.id.item_leads_status_icon);
            warning = itemView.findViewById(R.id.item_leads_warning);

            matchingPercent = itemView.findViewById(R.id.item_leads_match_percent);
            matchingPercent.setOnClickListener(v -> {
                if(clickListener != null) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        clickListener.onClick(position);
                    }
                }
            });

            profilePicture = itemView.findViewById(R.id.item_leads_profile_image);
            profilePicture.setOnClickListener(v -> {
                if(clickListener != null) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        clickListener.onClick(position);
                    }
                }
            });

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
