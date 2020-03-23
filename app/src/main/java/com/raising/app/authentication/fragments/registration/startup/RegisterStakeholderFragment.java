package com.raising.app.authentication.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.registration.helper.FragmentStakeholderBoardMember;
import com.raising.app.authentication.fragments.registration.helper.FragmentStakeholderFounder;
import com.raising.app.authentication.fragments.registration.helper.FragmentStakeholderShareholder;
import com.raising.app.models.stakeholder.StakeholderBoardMember;
import com.raising.app.models.stakeholder.StakeholderCorporateShareholder;
import com.raising.app.models.stakeholder.StakeholderFounder;
import com.raising.app.models.stakeholder.StakeholderPrivateShareholder;
import com.raising.app.models.stakeholder.StakeholderRecyclerListItem;
import com.raising.app.util.StakeholderRecyclerViewAdapter;

import java.util.ArrayList;

public class RegisterStakeholderFragment extends RaisingFragment {
    // hold references to the respective recycler views
    private RecyclerView founderRecyclerView, boardMemberRecyclerView, shareholderRecyclerView;

    // hold references to the respective recycler view adapters
    private StakeholderRecyclerViewAdapter founderAdapter, boardMemberAdapter, shareholderAdapter;

    // the lists, that are displayed in the respective recycler views
    private ArrayList<StakeholderRecyclerListItem> founderRecyclerViewList,
            boardMemberRecyclerViewList, shareholderRecyclerViewList;

    private ArrayList<StakeholderFounder> founderList;      // the list of all founders, use this to make backend request
    private ArrayList<StakeholderBoardMember> boardMemberList;      // the list of all members of the board, use this to make backend request
    private ArrayList<StakeholderPrivateShareholder> privateShareholderList;        // the list of private shareholders, use this to make backend request
    private ArrayList<StakeholderCorporateShareholder> corporateShareholderList;        // the list of all corporate shareholders, use this to make backend request

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_stakeholder,
                container, false);

        hideBottomNavigation(true);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //creates a example list for founderRecyclerViewList
        founderRecyclerViewList = new ArrayList<>();
        populateTemplateList(founderRecyclerViewList);
        populateTemplateList(founderList);

        createFounderRecyclerView(view);

        //creates a example list for boardMemberRecyclerViewList
        boardMemberRecyclerViewList = new ArrayList<>();
        populateTemplateList(boardMemberRecyclerViewList);
        populateTemplateList(boardMemberList);

        createBoardMemberRecyclerView(view);

        //creates a example list for shareholderRecyclerViewList
        shareholderRecyclerViewList = new ArrayList<>();
        populateTemplateList(shareholderRecyclerViewList);

        createShareholderRecyclerView(view);


        FloatingActionButton floatingBtnAddFounder = view.findViewById(R.id.floating_button_add_founder);
        floatingBtnAddFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: change to needed fragment and remove again from backstack
                changeFragment(new FragmentStakeholderFounder(), "FragmentStakeholderFounder");
            }
        });

        FloatingActionButton floatingBtnAddBoardMember = view.findViewById(R.id.floating_button_add_board_member);
        floatingBtnAddBoardMember.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: change to needed fragment and remove again from backstack
                changeFragment(new FragmentStakeholderBoardMember(), "FragmentStakeholderBoardMember");
            }
        });

        FloatingActionButton floatingBtnAddShareholder = view.findViewById(R.id.floating_button_add_shareholder);
        floatingBtnAddShareholder.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new FragmentStakeholderShareholder(), "FragmentStakeholderShareholder");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    private void createFounderRecyclerView(View view) {
        founderRecyclerView = view.findViewById(R.id.stakeholder_founder_recycler_view);
        founderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        founderAdapter = new StakeholderRecyclerViewAdapter(founderRecyclerViewList);
        founderRecyclerView.setAdapter(founderAdapter);
        founderAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                Fragment fragment = new FragmentStakeholderFounder();
                Bundle args = new Bundle();
                args.putString("firstName", founderList.get(position).getFirstName());
                args.putString("lastName", founderList.get(position).getLastName());
                args.putString("position", founderList.get(position).getCompanyPosition());
                args.putString("education", founderList.get(position).getEducation());
                fragment.setArguments(args);
            }

            @Override
            public void onClickDelete(int position) {
                founderRecyclerViewList.remove(position);
                founderAdapter.notifyItemRemoved(position);

            }
        });
    }

    private void createBoardMemberRecyclerView(View view) {
        boardMemberRecyclerView = view.findViewById(R.id.stakeholder_board_member_recycler_view);
        boardMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        boardMemberAdapter = new StakeholderRecyclerViewAdapter(boardMemberRecyclerViewList);
        boardMemberRecyclerView.setAdapter(boardMemberAdapter);
        boardMemberAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                //TODO: handle click on edit button
            }

            @Override
            public void onClickDelete(int position) {
                boardMemberRecyclerViewList.remove(position);
                boardMemberAdapter.notifyItemRemoved(position);

            }
        });
    }

    private void createShareholderRecyclerView(View view) {
        shareholderRecyclerView = view.findViewById(R.id.stakeholder_shareholder_recycler_view);
        shareholderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        shareholderAdapter = new StakeholderRecyclerViewAdapter(shareholderRecyclerViewList);
        shareholderRecyclerView.setAdapter(shareholderAdapter);
        shareholderAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                //TODO: handle click on edit button
            }

            @Override
            public void onClickDelete(int position) {
                shareholderRecyclerViewList.remove(position);
                shareholderAdapter.notifyItemRemoved(position);

            }
        });
    }

    private void populateTemplateList(ArrayList list) {
        list.add(new StakeholderRecyclerListItem( "Max Mustermann"));
        list.add(new StakeholderRecyclerListItem( "Maxine Mustermann"));
        list.add(new StakeholderRecyclerListItem( "Maxim Mustermann"));
    }
}
