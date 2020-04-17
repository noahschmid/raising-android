package com.raising.app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.raising.app.R;
import com.raising.app.fragments.profile.InvestorPublicProfileFragment;
import com.raising.app.fragments.profile.StartupPublicProfileFragment;
import com.raising.app.models.Match;
import com.raising.app.models.MatchListItem;
import com.raising.app.models.ViewState;
import com.raising.app.util.recyclerViewAdapter.MatchListAdapter;
import com.raising.app.util.recyclerViewAdapter.RecyclerViewMargin;
import com.raising.app.viewModels.MatchesViewModel;

import java.util.ArrayList;
import java.util.List;

public class MatchesFragment extends RaisingFragment {
    private RecyclerView matchList;
    private ConstraintLayout emptyMatchListLayout;
    private ArrayList<MatchListItem> matchListItems;
    private MatchesViewModel matchesViewModel;
    private MatchListAdapter matchListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final String TAG = "MatchesFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_match_list), false);

        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyMatchListLayout = view.findViewById(R.id.empty_matchList_layout);
        emptyMatchListLayout.setVisibility(View.GONE);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        matchesViewModel = new ViewModelProvider(this)
                .get(MatchesViewModel.class);

        matchesViewModel.getViewState().observe(getViewLifecycleOwner(), state -> processViewState(state));
        processViewState(matchesViewModel.getViewState().getValue());
        matchListItems = new ArrayList<>();

        if (resourcesViewModel.getViewState().getValue() == ViewState.RESULT ||
                resourcesViewModel.getViewState().getValue() == ViewState.CACHED) {
            ArrayList<Match> matchList = matchesViewModel.getMatches().getValue();
            matchListItems.clear();
            matchList.forEach(match -> {
                MatchListItem matchItem = new MatchListItem();
                matchItem.setDescription(match.getDescription());
                matchItem.setAccountId(match.getAccountId());
                matchItem.setScore(match.getMatchingPercent());
                matchItem.setStartup(match.isStartup());
                matchItem.setPictureId(match.getProfilePictureId());
                if (matchItem.isStartup()) {
                    matchItem.setAttribute(resources.getInvestmentPhase(
                            match.getInvestmentPhaseId()).getName());
                    matchItem.setName(match.getCompanyName());
                } else {
                    matchItem.setAttribute(resources.getInvestorType(
                            match.getInvestorTypeId()).getName());
                    matchItem.setName(match.getFirstName() + " " + match.getLastName());
                }
                matchListItems.add(matchItem);
            });
        }
        matchListAdapter = new MatchListAdapter(matchListItems);

        matchListItems.forEach(item -> {
            Log.d(TAG, "matchListItems: " + +item.getAccountId() + " " + item.getAttribute() + item.getName());
        });

        matchesViewModel.getMatches().observe(getViewLifecycleOwner(), matches -> {
            processItems(matches);
        });

        matchesViewModel.getViewState().observe(getViewLifecycleOwner(), viewState -> {
            switch (viewState) {
                case RESULT:
                case CACHED:
                    dismissLoadingPanel();
                    break;
                case LOADING:
                    showLoadingPanel();
                    break;
            }});

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    matchesViewModel.loadMatches();
                }
            });

            matchList = view.findViewById(R.id.matchList);
            matchList.setLayoutManager(new LinearLayoutManager(this.getContext()));
            matchList.setAdapter(matchListAdapter);
            RecyclerViewMargin decoration = new RecyclerViewMargin(15);
            matchList.addItemDecoration(decoration);
            matchListAdapter.setOnItemClickListener(position -> {
                Bundle args = new Bundle();
                MatchListItem item = matchListItems.get(position);
                args.putLong("id", item.getAccountId());
                args.putInt("score", item.getScore());
                args.putString("title", item.getName());
                customizeAppBar(item.getName(), true);
                if (matchListItems.get(position).isStartup()) {
                    StartupPublicProfileFragment publicProfile = new StartupPublicProfileFragment();
                    publicProfile.setArguments(args);
                    changeFragment(publicProfile);
                } else {
                    InvestorPublicProfileFragment publicProfile = new InvestorPublicProfileFragment();
                    publicProfile.setArguments(args);
                    changeFragment(publicProfile);
                }
            });
        }

        private void processItems (List < Match > matches) {
            if (resourcesViewModel.getViewState().getValue() == ViewState.RESULT ||
                    resourcesViewModel.getViewState().getValue() == ViewState.CACHED) {

                if (matches.size() == 0) {
                    emptyMatchListLayout.setVisibility(View.VISIBLE);
                } else {
                    emptyMatchListLayout.setVisibility(View.GONE);
                }
                matchListItems.clear();
                matches.forEach(match -> {
                    MatchListItem matchItem = new MatchListItem();
                    matchItem.setDescription(match.getDescription());
                    matchItem.setAccountId(match.getAccountId());
                    matchItem.setScore(match.getMatchingPercent());
                    matchItem.setStartup(match.isStartup());
                    matchItem.setPictureId(match.getProfilePictureId());
                    if (matchItem.isStartup()) {
                        matchItem.setAttribute(resources.getInvestmentPhase(
                                match.getInvestmentPhaseId()).getName());
                        matchItem.setName(match.getCompanyName());
                    } else {
                        matchItem.setAttribute(resources.getInvestorType(
                                match.getInvestorTypeId()).getName());
                        matchItem.setName(match.getFirstName() + " " + match.getLastName());
                    }
                    matchListItems.add(matchItem);
                });
                matchListAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onActivityCreated (@Nullable Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
        }
    }
