package com.raising.app.util.recyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.models.stakeholder.BoardMember;

import java.util.ArrayList;

public class StartupProfileBoardMemberAdapter extends RecyclerView.Adapter<StartupProfileBoardMemberAdapter.ViewHolder> {
    private ArrayList<BoardMember> recyclerItems;

    public StartupProfileBoardMemberAdapter(ArrayList<BoardMember> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public StartupProfileBoardMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_startup_public_profile_board_member_recycler_view,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StartupProfileBoardMemberAdapter.ViewHolder holder,
                                 int position) {
        BoardMember recyclerItem = recyclerItems.get(position);

        holder.boardPosition.setText(recyclerItem.getBoardPosition());
        String name = recyclerItem.getFirstName() + " " + recyclerItem.getLastName();
        holder.boardName.setText(name);
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
