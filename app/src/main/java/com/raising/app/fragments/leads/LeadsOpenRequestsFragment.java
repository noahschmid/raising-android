package com.raising.app.fragments.leads;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.profile.InvestorPublicProfileFragment;
import com.raising.app.fragments.profile.StartupPublicProfileFragment;
import com.raising.app.models.leads.Lead;
import com.raising.app.models.ViewState;
import com.raising.app.util.recyclerViewAdapter.LeadsOpenRequestAdapter;
import com.raising.app.viewModels.LeadsViewModel;

import java.util.ArrayList;

public class LeadsOpenRequestsFragment extends RaisingFragment {
    private final String TAG = "HandshakeOpenRequestFragment";

    ConstraintLayout emptyListLayout;

    private LeadsViewModel leadsViewModel;

    private ArrayList<Lead> openRequestItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_open_requests), true);

        return inflater.inflate(R.layout.fragment_leads_open_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyListLayout = view.findViewById(R.id.empty_requests_layout);
        emptyListLayout.setVisibility(View.GONE);

        // prepare leadsViewModel for usage
        leadsViewModel = ViewModelProviders.of(getActivity())
                .get(LeadsViewModel.class);

        leadsViewModel.getViewState().observe(getViewLifecycleOwner(), state -> processViewState(state));
        processViewState(leadsViewModel.getViewState().getValue());
        openRequestItems = new ArrayList<>();

        resourcesViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
            Log.d(TAG, "onViewCreated: ResourceViewModel ViewState: " + state.toString());
        });

        // populate open requests
        if(resourcesViewModel.getViewState().getValue() == ViewState.RESULT ||
                resourcesViewModel.getViewState().getValue() == ViewState.CACHED) {
            ArrayList<Lead> openRequests = leadsViewModel.getOpenRequests();
            if(openRequests == null || openRequests.size() == 0) {
                emptyListLayout.setVisibility(View.VISIBLE);
            }

            for(int i = 0; i < openRequests.size(); ++i) {
                Lead request = openRequests.get(i);
                if (request.isStartup()) {
                    request.setTitle(request.getCompanyName());
                    request.setAttribute(resources.getInvestmentPhase(
                            request.getInvestmentPhaseId()).getName());
                } else {
                    request.setTitle(request.getFirstName() + " " + request.getLastName());
                    request.setAttribute(resources.getInvestorType(
                            request.getInvestorTypeId()).getName());
                }
                Log.d(TAG, "onViewCreated: Add OpenRequest: " + request.getTitle());
                openRequestItems.add(request);
            }
        }

        RecyclerView recyclerView = view.findViewById(R.id.leads_open_requests_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        LeadsOpenRequestAdapter adapter = new LeadsOpenRequestAdapter(openRequestItems);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new LeadsOpenRequestAdapter.OnClickListener() {
            @Override
            public void onClickAccept(int position) {
                //TODO: accept open request
            }

            @Override
            public void onClickDecline(int position) {
                //TODO: decline open request
            }
        });

        adapter.setOnItemClickListener(position -> {
            Bundle args = new Bundle();
            args.putLong("id", openRequestItems.get(position).getAccountId());
            args.putInt("score", openRequestItems.get(position).getMatchingPercent());
            args.putLong("relationshipId", openRequestItems.get(position).getId());
            args.putString("title", openRequestItems.get(position).getTitle());
            if(openRequestItems.get(position).isStartup()) {
                Fragment fragment = new StartupPublicProfileFragment();
                fragment.setArguments(args);
                changeFragment(fragment, "StartupPublicProfileFragment");
            } else {
                Fragment fragment = new InvestorPublicProfileFragment();
                fragment.setArguments(args);
                changeFragment(fragment, "InvestorPublicProfileFragment");
            }
        });
    }
}
