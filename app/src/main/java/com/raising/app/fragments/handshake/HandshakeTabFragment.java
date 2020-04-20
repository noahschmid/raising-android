package com.raising.app.fragments.handshake;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.HandshakeItem;
import com.raising.app.models.LeadState;
import com.raising.app.models.Lead;
import com.raising.app.models.ViewState;
import com.raising.app.util.recyclerViewAdapter.HandshakeAdapter;
import com.raising.app.util.recyclerViewAdapter.RecyclerViewMargin;
import com.raising.app.viewModels.HandshakesViewModel;

import java.util.ArrayList;
import java.util.Date;

public class HandshakeTabFragment extends RaisingFragment {

    private final String TAG = "HandshakeTabFragment";

    private LeadState leadState;
    private HandshakesViewModel handshakesViewModel;

    private ArrayList<HandshakeItem> handshakeItemsToday;
    private ArrayList<HandshakeItem> handshakeItemsWeek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_handshake_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check for handshake state
        if (getArguments() != null) {
            leadState = (LeadState) getArguments().getSerializable("handshakeState");
        }

        // prepare handshakeViewModel for usage
        handshakesViewModel = ViewModelProviders.of(getActivity())
                .get(HandshakesViewModel.class);

        handshakesViewModel.getViewState().observe(getViewLifecycleOwner(), state -> processViewState(state));
        processViewState(handshakesViewModel.getViewState().getValue());
        handshakeItemsToday = new ArrayList<>();
        handshakeItemsWeek = new ArrayList<>();

        resourcesViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
            Log.d(TAG, "onViewCreated: ResourceViewModel ViewState: " + state.toString());
        });

        ArrayList<Lead> leads = handshakesViewModel.getLeads().getValue();
        ArrayList<Lead> handshakeStateLeads = new ArrayList<>();

        switch (leadState) {
            case YOUR_TURN:
                leads.forEach(lead -> {
                    if(lead.getState() == LeadState.YOUR_TURN) {
                        handshakeStateLeads.add(lead);
                    }
                });
                break;
            case PENDING:
                leads.forEach(lead -> {
                    if(lead.getState() == LeadState.PENDING) {
                        handshakeStateLeads.add(lead);
                    }
                });
                break;
            case CLOSED:
                leads.forEach(lead -> {
                    if(lead.getState() == LeadState.CLOSED) {
                        handshakeStateLeads.add(lead);
                    }
                });
                break;
        }

        // populate recycler view lists
        if(resourcesViewModel.getViewState().getValue() == ViewState.RESULT ||
                resourcesViewModel.getViewState().getValue() == ViewState.CACHED) {
            handshakeItemsToday.clear();
            handshakeItemsWeek.clear();
            handshakeStateLeads.forEach(lead -> {
                HandshakeItem handshakeItem = new HandshakeItem();
                handshakeItem.setId(lead.getId());
                handshakeItem.setMatchingPercent(lead.getMatchingPercent());
                handshakeItem.setStartup(lead.isStartup());
                handshakeItem.setPictureId(lead.getProfilePictureId());
                handshakeItem.setInteractionState(lead.getInteractionState());

                if (handshakeItem.isStartup()) {
                    handshakeItem.setName(lead.getCompanyName());
                    handshakeItem.setAttribute(resources.getInvestmentPhase(
                            lead.getInvestmentPhaseId()).getName());
                } else {
                    handshakeItem.setName(lead.getFirstName() + " " + lead.getLastName());
                    handshakeItem.setAttribute(resources.getInvestorType(
                            lead.getInvestorTypeId()).getName());
                }

                if (lead.getDate().equals(new Date())) {
                    handshakeItemsToday.add(handshakeItem);
                } else {
                    handshakeItemsWeek.add(handshakeItem);
                }
                Log.d(TAG, "onViewCreated: Add HandshakeItem: " + handshakeItem.getName());
            });
            Log.d(TAG, "onViewCreated: HandshakeLists filled");
        }

        HandshakeAdapter adapterToday = new HandshakeAdapter(handshakeItemsToday, leadState);
        HandshakeAdapter adapterWeek = new HandshakeAdapter(handshakeItemsWeek, leadState);
        RecyclerViewMargin decoration = new RecyclerViewMargin(15);

        // initialize the recycler view for all 'today'-handshakes
        RecyclerView recyclerToday = view.findViewById(R.id.handshake_tab_recycler_today);
        recyclerToday.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerToday.setAdapter(adapterToday);
        recyclerToday.addItemDecoration(decoration);
        adapterToday.setOnItemClickListener(position -> {
            Bundle args = new Bundle();
            args.putLong("id", handshakeItemsToday.get(position).getId());
            Fragment contactFragment = new HandshakeContactFragment();
            contactFragment.setArguments(args);
            changeFragment(contactFragment, "HandshakeContactFragment");
        });

        // initialize the recycler view for all 'this week'-handshakes
        RecyclerView recyclerWeek = view.findViewById(R.id.handshake_tab_recycler_week);
        recyclerWeek.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerWeek.setAdapter(adapterWeek);
        recyclerWeek.addItemDecoration(decoration);
        adapterWeek.setOnItemClickListener(position -> {
            Bundle args = new Bundle();
            args.putLong("id", handshakeItemsToday.get(position).getId());
            Fragment contactFragment = new HandshakeContactFragment();
            contactFragment.setArguments(args);
            changeFragment(contactFragment, "HandshakeContactFragment");
        });

        // prepare open requests layout
        ConstraintLayout openRequests = view.findViewById(R.id.handshake_open_requests);
        if (!(leadState.equals(LeadState.YOUR_TURN))) {
            openRequests.setVisibility(View.GONE);
        } else {
            ImageView image = view.findViewById(R.id.handshake_open_requests_image);
            if(handshakesViewModel.getOpenRequests().getValue() != null) {
                // set image of uppermost index in openRequests
                // image.setImageBitmap(handshakesViewModel.getOpenRequests().getValue().get(0).getProfileImage());
            } else {
                image.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_person_24dp));
            }
            openRequests.setOnClickListener(v ->
                    changeFragment(new HandshakeOpenRequestsFragment(),
                            "HandshakeOpenRequestFragment"));
        }
    }
}
