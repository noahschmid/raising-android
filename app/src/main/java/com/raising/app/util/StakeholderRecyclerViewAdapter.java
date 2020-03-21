package com.raising.app.util;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.stakeholder.StakeholderFounder;
import com.raising.app.models.stakeholder.StakeholderRecyclerListItem;

import java.util.ArrayList;

public class StakeholderRecyclerViewAdapter extends RecyclerView.Adapter<StakeholderRecyclerViewAdapter.ViewHolder> {
    private ArrayList<StakeholderRecyclerListItem> founderRecyclerItems;
    private OnClickListener clickListener;

    public StakeholderRecyclerViewAdapter(ArrayList<StakeholderRecyclerListItem> recyclerItems) {
        this.founderRecyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stakeholder_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StakeholderRecyclerListItem recyclerItem = founderRecyclerItems.get(position);

        holder.stakeholderRecyclerTitle.setText(recyclerItem.getTitle());

    }

    @Override
    public int getItemCount() {
        return founderRecyclerItems.size();
    }

    public interface OnClickListener {
        void onClickEdit(int position);
        void onClickDelete(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        clickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView stakeholderRecyclerTitle;
        private ImageButton btnStakeholderRecyclerEdit, btnStakeholderRecyclerDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            stakeholderRecyclerTitle = itemView.findViewById(R.id.register_stakeholder_recycler_item_title);
            btnStakeholderRecyclerEdit = itemView.findViewById(R.id.stakeholder_recycler_button_edit);
            btnStakeholderRecyclerEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                            clickListener.onClickEdit(getAdapterPosition());
                        }
                    }
                }
            });
            btnStakeholderRecyclerDelete = itemView.findViewById(R.id.stakeholder_recycler_button_delete);
            btnStakeholderRecyclerDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                            clickListener.onClickDelete(getAdapterPosition());
                        }
                    }
                }
            });
        }
    }
}