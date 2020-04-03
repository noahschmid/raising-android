package com.raising.app.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.stakeholder.BoardMember;

import java.util.ArrayList;

public class StartupProfileBoardMemberRecyclerViewAdapter extends RecyclerView.Adapter<StartupProfileBoardMemberRecyclerViewAdapter.ViewHolder> {
    private ArrayList<BoardMember> recyclerItems;

    public StartupProfileBoardMemberRecyclerViewAdapter(ArrayList<BoardMember> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public StartupProfileBoardMemberRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_startup_public_profile_board_member_recycler_view,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StartupProfileBoardMemberRecyclerViewAdapter.ViewHolder holder,
                                 int position) {
        BoardMember recyclerItem = recyclerItems.get(position);

        holder.boardPosition.setText(recyclerItem.getBoardPosition());
        holder.boardName.setText(recyclerItem.getTitle());
        holder.boardMemberSince.setText(recyclerItem.getMemberSince());
        holder.boardProfession.setText(recyclerItem.getProfession());
        holder.boardEducation.setText(recyclerItem.getEducation());
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView boardPosition, boardName, boardMemberSince, boardProfession, boardEducation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            boardPosition = itemView.findViewById(R.id.text_startup_board_position);
            boardName = itemView.findViewById(R.id.text_startup_board_member_name);
            boardMemberSince = itemView.findViewById(R.id.text_startup_board_member_since);
            boardProfession = itemView.findViewById(R.id.text_startup_board_profession);
            boardEducation = itemView.findViewById(R.id.text_startup_board_education);
        }
    }
}
