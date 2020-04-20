package com.raising.app.fragments.handshake;

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
import com.raising.app.models.HandshakeItem;
import com.raising.app.models.Lead;
import com.raising.app.models.ViewState;
import com.raising.app.util.recyclerViewAdapter.HandshakeOpenRequestAdapter;
import com.raising.app.viewModels.HandshakesViewModel;

import java.util.ArrayList;

public class HandshakeOpenRequestsFragment extends RaisingFragment {
    private final String TAG = "HandshakeOpenRequestFragment";

    ConstraintLayout emptyListLayout;

    private HandshakesViewModel handshakesViewModel;

    private ArrayList<HandshakeItem> openRequestItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_open_requests), true);

        return inflater.inflate(R.layout.fragment_handshake_open_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyListLayout = view.findViewById(R.id.empty_requests_layout);
        emptyListLayout.setVisibility(View.GONE);

        // prepare handshakeViewModel for usage
        handshakesViewModel = ViewModelProviders.of(getActivity())
                .get(HandshakesViewModel.class);

        handshakesViewModel.getViewState().observe(getViewLifecycleOwner(), state -> processViewState(state));
        processViewState(handshakesViewModel.getViewState().getValue());
        openRequestItems = new ArrayList<>();

        resourcesViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
            Log.d(TAG, "onViewCreated: ResourceViewModel ViewState: " + state.toString());
        });

        // populate open requests
        if(resourcesViewModel.getViewState().getValue() == ViewState.RESULT ||
                resourcesViewModel.getViewState().getValue() == ViewState.CACHED) {
            ArrayList<Lead> openRequests = handshakesViewModel.getOpenRequests().getValue();
            if(openRequests == null || openRequests.size() == 0) {
                emptyListLayout.setVisibility(View.VISIBLE);
            }
            openRequests.forEach(openRequest -> {
                HandshakeItem openRequestItem = new HandshakeItem();
                openRequestItem.setId(openRequest.getId());
                openRequestItem.setStartup(openRequest.isStartup());
                openRequestItem.setPictureId(openRequest.getProfilePictureId());
                if (openRequestItem.isStartup()) {
                    openRequestItem.setName(openRequest.getCompanyName());
                    openRequestItem.setAttribute(resources.getInvestmentPhase(
                            openRequest.getInvestmentPhaseId()).getName());
                } else {
                    openRequestItem.setName(openRequest.getFirstName() + " " + openRequest.getLastName());
                    openRequestItem.setAttribute(resources.getInvestorType(
                            openRequest.getInvestorTypeId()).getName());
                }
                Log.d(TAG, "onViewCreated: Add OpenRequest: " + openRequestItem.getName());
                openRequestItems.add(openRequestItem);
            });
            Log.d(TAG, "onViewCreated: OpenRequests filled");
        }

        RecyclerView recyclerView = view.findViewById(R.id.handshake_open_requests_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        HandshakeOpenRequestAdapter adapter = new HandshakeOpenRequestAdapter(openRequestItems);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new HandshakeOpenRequestAdapter.OnClickListener() {
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
            args.putLong("id", openRequestItems.get(position).getId());
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
