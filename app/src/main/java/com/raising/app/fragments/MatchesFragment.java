package com.raising.app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.raising.app.R;
import com.raising.app.fragments.profile.InvestorPublicProfileFragment;
import com.raising.app.fragments.profile.StartupPublicProfileFragment;
import com.raising.app.models.MatchListItem;
import com.raising.app.util.AccountService;
import com.raising.app.util.recyclerViewAdapter.MatchListAdapter;
import com.raising.app.util.recyclerViewAdapter.PublicProfileMatchingRecyclerViewAdapter;
import com.raising.app.viewModels.AccountViewModel;
import com.raising.app.viewModels.MatchListViewModel;

import java.util.ArrayList;

public class MatchesFragment extends RaisingFragment {
    private RecyclerView matchList;
    private ConstraintLayout emptyMatchListLayout;
    private ArrayList<MatchListItem> matchListItems;
    private MatchListViewModel matchListViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyMatchListLayout = view.findViewById(R.id.empty_matchList_layout);

        //TODO: store matchList items in following arraylist
        matchListViewModel  = new ViewModelProvider(this)
                .get(MatchListViewModel .class);
        matchListItems = matchListViewModel.getMatchList().getValue();
        matchListViewModel.getMatchList().observe(getViewLifecycleOwner(), matches -> {
            matchListItems = matches;
        });

        if(matchListItems.size() == 0) {
            emptyMatchListLayout.setVisibility(View.GONE);
        }

        //helper array to define colors of the pie chart in the matchList
        int [] colors = {
                getResources().getColor(R.color.raisingPrimary, null),
                getResources().getColor(R.color.raisingWhite, null)};
        matchList = view.findViewById(R.id.matchList);
        matchList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        MatchListAdapter matchListAdapter = new MatchListAdapter(matchListItems, colors);
        matchList.setAdapter(matchListAdapter);
        matchListAdapter.setOnItemClickListener(position -> {
            long id = matchListItems.get(position).getId();
            Bundle args = new Bundle();
            args.putLong("id", id);
            if(matchListItems.get(position).isStartup()) {
                InvestorPublicProfileFragment publicProfile = new InvestorPublicProfileFragment();
                publicProfile.setArguments(args);
                changeFragment(publicProfile);
            } else {
                StartupPublicProfileFragment publicProfile = new StartupPublicProfileFragment();
                publicProfile.setArguments(args);
                changeFragment(publicProfile);
            }
           // matchListItems.remove(position);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
