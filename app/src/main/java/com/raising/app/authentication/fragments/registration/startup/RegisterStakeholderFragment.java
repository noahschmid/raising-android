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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.registration.helper.BoardMemberInputFragment;
import com.raising.app.authentication.fragments.registration.helper.FounderInputFragment;
import com.raising.app.authentication.fragments.registration.helper.ShareholderInputFragment;
import com.raising.app.authentication.fragments.registration.helper.viewModels.BoardMemberViewModel;
import com.raising.app.authentication.fragments.registration.helper.viewModels.FounderViewModel;
import com.raising.app.authentication.fragments.registration.helper.viewModels.ShareholderViewModel;
import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.models.stakeholder.StakeholderRecyclerListItem;
import com.raising.app.util.StakeholderRecyclerViewAdapter;

import java.lang.reflect.Type;
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
    private ArrayList<StakeholderRecyclerListItem> founderList,
            boardMemberList, shareholderList;

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
/*
        //creates a example list for boardMemberRecyclerViewList
        boardMemberList = new ArrayList<>();
        boardMemberList.add(new BoardMember("Max", "Mustermann", "Carpenter", "VRP", "2010", "Bachelor"));
        boardMemberList.add(new BoardMember("Maxine", "Mustermann", "Carpenter", "VRP", "2010", "Bachelor"));
        boardMemberList.add(new BoardMember("Maxim", "Mustermann", "Carpenter", "VRP", "2010", "Bachelor"));

        createBoardMemberRecyclerView(view);

        //creates a example list for shareholderRecyclerViewList
        shareholderList = new ArrayList<>();
        createShareholderRecyclerView(view);*/

        if (savedInstanceState == null) {
            founderList = new ArrayList<>();
            boardMemberList = new ArrayList<>();
            shareholderList = new ArrayList<>();
        }

        // initialize the different recycler views
        setupFounderRecyclerView(view);
        setupBoardMemberRecyclerView(view);
        setupShareholderRecyclerView(view);

        FloatingActionButton floatingBtnAddFounder = view.findViewById(R.id.floating_button_add_founder);
        floatingBtnAddFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                founderViewModel.getSelectedFounder().observe(getViewLifecycleOwner(), founder -> {
                            // add item to recycler view
                            founderList.add(founder);
                            founderAdapter.notifyDataSetChanged();
                        });
                changeFragment(new FounderInputFragment(), "FragmentStakeholderFounder");
            }
        });

        FloatingActionButton floatingBtnAddBoardMember
                = view.findViewById(R.id.floating_button_add_board_member);
        floatingBtnAddBoardMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardMemberViewModel.getSelectedBoardMember().observe(getViewLifecycleOwner(), boardMember -> {
                    boardMemberList.add(boardMember);
                    boardMemberAdapter.notifyDataSetChanged();
                });
                changeFragment(new BoardMemberInputFragment(), "FragmentStakeholderBoardMember");
            }
        });

        FloatingActionButton floatingBtnAddShareholder
                = view.findViewById(R.id.floating_button_add_shareholder);
        floatingBtnAddShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareholderViewModel.getSelectedShareholder().observe(getViewLifecycleOwner(), shareholder -> {
                        shareholderList.add(shareholder);
                        shareholderAdapter.notifyDataSetChanged();
                    });
                changeFragment(new ShareholderInputFragment(), "FragmentStakeholderShareholder");
            }
        });

        if(savedInstanceState != null) {
            Gson gson = new Gson();
            Type founderListType = new TypeToken<ArrayList<Founder>>(){}.getType();
            founderList = gson.fromJson(savedInstanceState.getString("founderList"), founderListType);

            Type boardMemberListType = new TypeToken<ArrayList<BoardMember>>(){}.getType();
            boardMemberList = gson.fromJson(savedInstanceState.getString("boardmemberList"), boardMemberListType);

            Type shareholderListType = new TypeToken<ArrayList<Shareholder>>(){}.getType();
            shareholderList = gson.fromJson(savedInstanceState.getString("shareholderList"), shareholderListType);
        } else {
            shareholderList.add(
                    new Shareholder(
                            true, "Max", "Mustermann",
                            "Switzerland", null, null, null,
                            "15"));
            shareholderList.add(
                    new Shareholder(
                            false, null, null,
                            null, "Maxine", "Family Business",
                            "www.www.de", "50"));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();
        String json = gson.toJson(founderList.toArray());
        outState.putString("founderList", json);

        json = gson.toJson(boardMemberList.toArray());
        outState.putString("boardMemberList", json);

        json = gson.toJson(shareholderList.toArray());
        outState.putString("shareholderList", json);
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
            founderList.add(founderListItem);
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
        founderAdapter = new StakeholderRecyclerViewAdapter(founderList);
        founderRecyclerView.setAdapter(founderAdapter);
        founderAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                FounderInputFragment founderFragment = new FounderInputFragment();
                Bundle args = new Bundle();
                Founder selectedFounder = ((Founder)founderList.get(position));
                founderFragment.passFounder(selectedFounder);
                changeFragment(founderFragment, null);

                founderViewModel.getSelectedFounder().observe(getViewLifecycleOwner(), founder -> {
                    // set updated item into founderList
                    founder.updateTitle();
                    founderList.set(position, founder);
                    founderAdapter.notifyItemChanged(position);
                });
            }

            @Override
            public void onClickDelete(int position) {
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
            boardMemberList.add(boardMemberListItem);
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
        boardMemberAdapter = new StakeholderRecyclerViewAdapter(boardMemberList);
        boardMemberRecyclerView.setAdapter(boardMemberAdapter);
        boardMemberAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                BoardMemberInputFragment boardMemberFragment = new BoardMemberInputFragment();
                BoardMember selectedBoardMember = ((BoardMember)boardMemberList.get(position));
                boardMemberFragment.passBoardMember(selectedBoardMember);
                changeFragment(boardMemberFragment);

                boardMemberViewModel.getSelectedBoardMember().observe(getViewLifecycleOwner(), boardMember -> {
                    // set updated item into boardMemberList
                    boardMember.updateTitle();
                    boardMemberList.set(position, boardMember);
                    boardMemberAdapter.notifyItemChanged(position);
                });
            }

            @Override
            public void onClickDelete(int position) {
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
            shareholderList.add(shareholderListItem);
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
        shareholderAdapter = new StakeholderRecyclerViewAdapter(shareholderList);
        shareholderRecyclerView.setAdapter(shareholderAdapter);
        shareholderAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                ShareholderInputFragment shareholderFragment = new ShareholderInputFragment();
                Shareholder selectedShareholder = ((Shareholder)shareholderList.get(position));
                shareholderFragment.passShareholder(selectedShareholder);
                changeFragment(shareholderFragment, "StakeholderShareholder");
                shareholderViewModel.getSelectedShareholder().observe(getViewLifecycleOwner(), shareholder -> {
                    // set updated item to shareholderList
                    shareholder.updateTitle();
                    shareholderList.set(position, shareholder);
                    shareholderAdapter.notifyItemChanged(position);
                });
            }

            @Override
            public void onClickDelete(int position) {
                shareholderList.remove(position);
                shareholderAdapter.notifyItemRemoved(position);

            }
        });
    }
}
