package com.raising.app.authentication.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.raising.app.authentication.fragments.registration.helper.viewModels.BoardMemberViewModel;
import com.raising.app.authentication.fragments.registration.helper.viewModels.FounderViewModel;
import com.raising.app.authentication.fragments.registration.helper.viewModels.ShareholderViewModel;
import com.raising.app.models.stakeholder.StakeholderBoardMember;
import com.raising.app.models.stakeholder.StakeholderFounder;
import com.raising.app.models.stakeholder.StakeholderShareholder;
import com.raising.app.models.stakeholder.StakeholderRecyclerListItem;
import com.raising.app.util.StakeholderRecyclerViewAdapter;

import java.util.ArrayList;

public class RegisterStakeholderFragment extends RaisingFragment {
    private FounderViewModel founderViewModel;
    private BoardMemberViewModel boardMemberViewModel;
    private ShareholderViewModel shareholderViewModel;

    // hold references to the respective recycler views
    private RecyclerView founderRecyclerView, boardMemberRecyclerView, shareholderRecyclerView;

    // hold references to the respective recycler view adapters
    private StakeholderRecyclerViewAdapter founderAdapter, boardMemberAdapter, shareholderAdapter;

    // the lists, that are displayed in the respective recycler views
    private ArrayList<StakeholderRecyclerListItem> founderRecyclerViewList,
            boardMemberRecyclerViewList, shareholderRecyclerViewList;

    private ArrayList<StakeholderFounder> founderList;      // the list of all founders, use this to make backend request
    private ArrayList<StakeholderBoardMember> boardMemberList;      // the list of all members of the board, use this to make backend request
    private ArrayList<StakeholderShareholder> shareholderList;        // the list of all shareholders, use this to make backend request

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

        if (savedInstanceState == null) {
            founderRecyclerViewList = new ArrayList<>();
            founderList = new ArrayList<>();

            boardMemberRecyclerViewList = new ArrayList<>();
            boardMemberList = new ArrayList<>();

            shareholderRecyclerViewList = new ArrayList<>();
            shareholderList = new ArrayList<>();
        }

        // initialize the different recycler views
        setupFounderRecyclerView(view);
        setupBoardMemberRecyclerView(view);
        setupShareholderRecyclerView(view);

        FloatingActionButton floatingBtnAddFounder
                = view.findViewById(R.id.floating_button_add_founder);
        floatingBtnAddFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new FragmentStakeholderFounder(),
                        "FragmentStakeholderFounder");
            }
        });

        FloatingActionButton floatingBtnAddBoardMember
                = view.findViewById(R.id.floating_button_add_board_member);
        floatingBtnAddBoardMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new FragmentStakeholderBoardMember(),
                        "FragmentStakeholderBoardMember");
            }
        });

        FloatingActionButton floatingBtnAddShareholder
                = view.findViewById(R.id.floating_button_add_shareholder);
        floatingBtnAddShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new FragmentStakeholderShareholder(),
                        "FragmentStakeholderShareholder");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Delegate creating of recycler view and create observer for FounderViewModel
     * @param view The view containing the recycler view
     */
    private void setupFounderRecyclerView(View view) {
        createFounderRecyclerView(view);
        founderViewModel = new ViewModelProvider(requireActivity()).get(FounderViewModel.class);
        founderViewModel.getSelectedFounder().observe(getViewLifecycleOwner(),
                stakeholderFounder -> {
            // add item to founderList
            founderList.add(stakeholderFounder);

            // add item to recycler view
            String founderTitle
                    = stakeholderFounder.getFirstName() + " " + stakeholderFounder.getLastName();
            StakeholderRecyclerListItem founderListItem
                    = new StakeholderRecyclerListItem(founderTitle);
            founderRecyclerViewList.add(founderListItem);
            founderAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Create the recycler view for the founders by linking the UI components and setting the adapter
     * @param view The view containing this recycler view
     */
    private void createFounderRecyclerView(View view) {
        founderRecyclerView = view.findViewById(R.id.stakeholder_founder_recycler_view);
        founderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        founderAdapter = new StakeholderRecyclerViewAdapter(founderRecyclerViewList);
        founderRecyclerView.setAdapter(founderAdapter);
        founderAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                Fragment founderFragment = new FragmentStakeholderFounder();
                Bundle args = new Bundle();
                args.putString("firstName", founderList.get(position).getFirstName());
                args.putString("lastName", founderList.get(position).getLastName());
                args.putString("position", founderList.get(position).getCompanyPosition());
                args.putString("education", founderList.get(position).getEducation());
                founderFragment.setArguments(args);
                changeFragment(founderFragment, "StakeholderFounder");

                founderViewModel.getSelectedFounder().observe(getViewLifecycleOwner(),
                        stakeholderFounder -> {
                    // set updated item into founderList
                    founderList.set(position, stakeholderFounder);

                    // set updated item into recycler view
                    String founderTitle
                            = stakeholderFounder.getFirstName() + " " + stakeholderFounder.getLastName();
                    StakeholderRecyclerListItem founderListItem
                            = new StakeholderRecyclerListItem(founderTitle);
                    founderRecyclerViewList.set(position, founderListItem);
                    founderAdapter.notifyItemChanged(position);
                });
            }

            @Override
            public void onClickDelete(int position) {
                founderRecyclerViewList.remove(position);
                founderList.remove(position);
                founderAdapter.notifyItemRemoved(position);

            }
        });
    }

    /**
     * Delegate creating of the recycler view and create observer for BoardMemberViewModel
     * @param view The view containing the recycler view
     */
    private void setupBoardMemberRecyclerView(View view) {
        createBoardMemberRecyclerView(view);
        boardMemberViewModel = new ViewModelProvider(requireActivity()).get(BoardMemberViewModel.class);
        boardMemberViewModel.getSelectedBoardMember().observe(getViewLifecycleOwner(),
                stakeholderBoardMember -> {
            // add item to boardMemberList
            boardMemberList.add(stakeholderBoardMember);

            // add item to recycler view
            String boardMemberTitle
                    = stakeholderBoardMember.getFirstName() + " " + stakeholderBoardMember.getLastName();
            StakeholderRecyclerListItem boardMemberListItem
                    = new StakeholderRecyclerListItem(boardMemberTitle);
            boardMemberRecyclerViewList.add(boardMemberListItem);
            boardMemberAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Create the recycler view for the board members by linking the UI components and setting the adapter
     * @param view The view containing this recycler view
     */
    private void createBoardMemberRecyclerView(View view) {
        boardMemberRecyclerView = view.findViewById(R.id.stakeholder_board_member_recycler_view);
        boardMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        boardMemberAdapter = new StakeholderRecyclerViewAdapter(boardMemberRecyclerViewList);
        boardMemberRecyclerView.setAdapter(boardMemberAdapter);
        boardMemberAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                Fragment boardMemberFragment = new FragmentStakeholderBoardMember();
                Bundle args = new Bundle();
                args.putString("firstName", boardMemberList.get(position).getFirstName());
                args.putString("lastName", boardMemberList.get(position).getLastName());
                args.putString("profession", boardMemberList.get(position).getProfession());
                args.putString("position", boardMemberList.get(position).getBoardPosition());
                args.putString("memberSince", boardMemberList.get(position).getMemberSince());
                args.putString("education", boardMemberList.get(position).getEducation());
                boardMemberFragment.setArguments(args);
                changeFragment(boardMemberFragment, "StakeholderBoardMember");

                boardMemberViewModel.getSelectedBoardMember().observe(getViewLifecycleOwner(),
                        stakeholderBoardMember -> {
                    // set updated item into boardMemberList
                    boardMemberList.set(position, stakeholderBoardMember);

                    // set updated item into recycler view
                    String boardMemberTitle
                            = stakeholderBoardMember.getFirstName() + " " + stakeholderBoardMember.getLastName();
                    StakeholderRecyclerListItem boardMemberListItem
                            = new StakeholderRecyclerListItem(boardMemberTitle);
                    boardMemberRecyclerViewList.set(position, boardMemberListItem);
                    boardMemberAdapter.notifyItemChanged(position);
                });
            }

            @Override
            public void onClickDelete(int position) {
                boardMemberRecyclerViewList.remove(position);
                boardMemberList.remove(position);
                boardMemberAdapter.notifyItemRemoved(position);

            }
        });
    }

    /**
     * Delegate creating of ShareholderRecycler view and create observer for ShareholderViewModel
     * @param view The view containing the recycler view
     */
    private void setupShareholderRecyclerView(View view) {
        createShareholderRecyclerView(view);
        shareholderViewModel = new ViewModelProvider(requireActivity()).get(ShareholderViewModel.class);
        shareholderViewModel.getSelectedShareholder().observe(getViewLifecycleOwner(),
                stakeholderShareholder -> {
            // add item to shareholderList
            shareholderList.add(stakeholderShareholder);

            // add item to recycler view
            String shareholderTitle;
            if (stakeholderShareholder.isPrivateShareholder()) {
                shareholderTitle
                        = stakeholderShareholder.getFirstName() + " " + stakeholderShareholder.getLastName();
            } else {
                shareholderTitle = stakeholderShareholder.getName();
            }
            StakeholderRecyclerListItem shareholderListItem
                    = new StakeholderRecyclerListItem(shareholderTitle);
            shareholderRecyclerViewList.add(shareholderListItem);
            shareholderAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Creating the recycler view for the shareholder by linking the UI components and setting the adapter
     * @param view
     */
    private void createShareholderRecyclerView(View view) {
        shareholderRecyclerView = view.findViewById(R.id.stakeholder_shareholder_recycler_view);
        shareholderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        shareholderAdapter = new StakeholderRecyclerViewAdapter(shareholderRecyclerViewList);
        shareholderRecyclerView.setAdapter(shareholderAdapter);
        shareholderAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                Fragment shareholderFragment = new FragmentStakeholderShareholder();
                Bundle args = new Bundle();
                args.putBoolean("privateShareholder", shareholderList.get(position).isPrivateShareholder());
                args.putString("firstName", shareholderList.get(position).getFirstName());
                args.putString("lastName", shareholderList.get(position).getLastName());
                args.putString("country", shareholderList.get(position).getCountry());
                args.putString("name", shareholderList.get(position).getName());
                args.putString("corporateBody", shareholderList.get(position).getCorporateBody());
                args.putString("website", shareholderList.get(position).getWebsite());
                args.putString("equityShare", shareholderList.get(position).getEquityShare());
                shareholderFragment.setArguments(args);
                changeFragment(shareholderFragment, "StakeholderShareholder");

                shareholderViewModel.getSelectedShareholder().observe(getViewLifecycleOwner(),
                        stakeholderShareholder -> {
                    // set updated item to shareholderList
                    shareholderList.set(position, stakeholderShareholder);

                    // add item to recycler view
                    String shareholderTitle;
                    if (stakeholderShareholder.isPrivateShareholder()) {
                        shareholderTitle
                                = stakeholderShareholder.getFirstName() + " " + stakeholderShareholder.getLastName();
                    } else {
                        shareholderTitle = stakeholderShareholder.getName();
                    }
                    StakeholderRecyclerListItem shareholderListItem
                            = new StakeholderRecyclerListItem(shareholderTitle);
                    shareholderRecyclerViewList.set(position, shareholderListItem);
                    shareholderAdapter.notifyItemChanged(position);
                });
            }

            @Override
            public void onClickDelete(int position) {
                shareholderRecyclerViewList.remove(position);
                shareholderList.remove(position);
                shareholderAdapter.notifyItemRemoved(position);

            }
        });
    }
}
