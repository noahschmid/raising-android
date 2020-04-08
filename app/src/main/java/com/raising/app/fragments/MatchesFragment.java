package com.raising.app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;
import com.raising.app.fragments.profile.InvestorPublicProfileFragment;
import com.raising.app.fragments.profile.StartupPublicProfileFragment;
import com.raising.app.models.MatchlistItem;
import com.raising.app.util.AccountService;
import com.raising.app.util.recyclerViewAdapter.MatchlistAdapter;
import com.raising.app.util.recyclerViewAdapter.PublicProfileMatchingRecyclerViewAdapter;

import java.util.ArrayList;

public class MatchesFragment extends RaisingFragment {
    private RecyclerView matchlist;
    private boolean isStartup = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isStartup = AccountService.isStartup();

        //TODO: store matchlist items in following arraylist
        ArrayList<MatchlistItem> matchlistItems = new ArrayList<>();
        //helper array to define colors of the pie chart in the matchlist
        int [] colors = {
                getResources().getColor(R.color.raisingPrimary, null),
                getResources().getColor(R.color.raisingWhite, null)};
        matchlist = view.findViewById(R.id.matchlist);
        matchlist.setLayoutManager(new LinearLayoutManager(this.getContext()));
        MatchlistAdapter matchlistAdapter = new MatchlistAdapter(matchlistItems, colors);
        matchlist.setAdapter(matchlistAdapter);
        matchlistAdapter.setOnItemClickListener(position -> {
            long id = matchlistItems.get(position).getId();
            Bundle args = new Bundle();
            args.putLong("id", id);
            if(isStartup) {
                InvestorPublicProfileFragment publicProfile = new InvestorPublicProfileFragment();
                publicProfile.setArguments(args);
                changeFragment(publicProfile);
            } else {
                StartupPublicProfileFragment publicProfile = new StartupPublicProfileFragment();
                publicProfile.setArguments(args);
                changeFragment(publicProfile);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
