package com.raising.app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;
import com.raising.app.fragments.profile.InvestorPublicProfileFragment;
import com.raising.app.fragments.profile.StartupPublicProfileFragment;
import com.raising.app.models.Match;
import com.raising.app.models.MatchListItem;
import com.raising.app.util.SubscriptionHandler;
import com.raising.app.util.TabOrigin;
import com.raising.app.util.recyclerViewAdapter.MatchListAdapter;
import com.raising.app.models.ViewState;
import com.raising.app.util.recyclerViewAdapter.RecyclerViewMargin;
import com.raising.app.viewModels.MatchesViewModel;
import com.raising.app.viewModels.SettingsViewModel;
import com.raising.app.viewModels.TabViewModel;

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
        Log.d(TAG, "onCreateView: ");

        setBase(TabOrigin.MATCHES);

        customizeAppBar(getString(R.string.toolbar_title_match_list), false);
        matchesViewModel = ViewModelProviders.of(getActivity())
                .get(MatchesViewModel.class);
        tabViewModel = ViewModelProviders.of(getActivity())
                .get(TabViewModel.class);

        if(tabViewModel.getCurrentMatchesFragment() != null) {
            changeFragment(tabViewModel.getCurrentMatchesFragment());
        }

        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onResourcesLoaded() {
        Log.d(TAG, "onResourcesLoaded: ");
        matchesViewModel.getViewState().observe(getViewLifecycleOwner(), viewState -> {
            Log.d(TAG, "onViewCreated: Matches Observer ViewState: " + viewState);
            if (viewState == ViewState.RESULT || viewState == ViewState.CACHED) {
                processItems(matchesViewModel.getMatches().getValue());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");

        emptyMatchListLayout = view.findViewById(R.id.empty_matchList_layout);
        emptyMatchListLayout.setVisibility(View.GONE);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> matchesViewModel.loadMatches());

        matchListItems = new ArrayList<>();

        matchListAdapter = new MatchListAdapter(matchListItems);
        matchesViewModel.loadMatches();

        // prepare match list recycler view
        matchList = view.findViewById(R.id.matchList);
        matchList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        matchList.setAdapter(matchListAdapter);
        matchListAdapter.setOnItemClickListener(position -> {
            // check if user has valid subscription
            if(!SubscriptionHandler.hasValidSubscription()) {
                changeFragment(new UnlockPremiumFragment());
            } else {
                Bundle args = new Bundle();
                MatchListItem item = matchListItems.get(position);
                args.putLong("id", item.getAccountId());
                args.putLong("relationshipId", item.getRelationshipId());
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
            }
        });

        if (matchListItems.size() == 0 && resourcesViewModel.getViewState().getValue() == ViewState.RESULT) {
            emptyMatchListLayout.setVisibility(View.VISIBLE);
            Log.d(TAG, "processItems: Empty Layout visible");
        }
    }

    /**
     * Create MatchListItems for recyclerview out of list of matches
     * @param matches
     */
    private void processItems(List<Match> matches) {
        Log.d(TAG, "processItems: ");
        emptyMatchListLayout.setVisibility(View.GONE);
        matchListItems.clear();
        matches.forEach(match -> {
            MatchListItem matchItem = new MatchListItem();
            matchItem.setDescription(match.getDescription());
            matchItem.setRelationshipId(match.getId());
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

        if (matchListItems.size() == 0 && resourcesViewModel.getViewState().getValue() == ViewState.RESULT) {
            emptyMatchListLayout.setVisibility(View.VISIBLE);
            Log.d(TAG, "processItems: Empty Layout visible");
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
