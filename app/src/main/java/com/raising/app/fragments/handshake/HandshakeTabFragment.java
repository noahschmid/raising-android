package com.raising.app.fragments.handshake;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.profile.InvestorPublicProfileFragment;
import com.raising.app.fragments.profile.StartupPublicProfileFragment;
import com.raising.app.models.HandshakeItem;
import com.raising.app.models.HandshakeState;
import com.raising.app.models.ViewState;
import com.raising.app.util.recyclerViewAdapter.HandshakeAdapter;

import java.util.ArrayList;

public class HandshakeTabFragment extends RaisingFragment {
    private HandshakeState stateEnum;

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
            stateEnum = (HandshakeState) getArguments().getSerializable("handshakeState");
        }

        // prepare open requests layout
        ConstraintLayout openRequests = view.findViewById(R.id.handshake_open_requests);
        if (stateEnum.equals(HandshakeState.YOUR_TURN)) {
            ImageView image = view.findViewById(R.id.handshake_open_requests_image);
            //TODO: insert image of uppermost open request
            openRequests.setOnClickListener(v ->
                    changeFragment(new HandshakeOpenRequestsFragment(), "HandshakeOpenRequestFragment"));
        } else {
            openRequests.setVisibility(View.GONE);
        }

        // initialize the recycler view for all 'today'-handshakes
        //TODO: store today handshakes in below array list
        ArrayList<HandshakeItem> handshakeItemsToday = new ArrayList<>();

        RecyclerView recyclerToday = view.findViewById(R.id.handshake_tab_recycler_today);
        recyclerToday.setLayoutManager(new LinearLayoutManager(this.getContext()));
        HandshakeAdapter adapterToday = new HandshakeAdapter(handshakeItemsToday, stateEnum);
        recyclerToday.setAdapter(adapterToday);

        adapterToday.setOnItemClickListener(position -> {
            Bundle args = new Bundle();
            args.putLong("id", handshakeItemsToday.get(position).getId());
            Fragment contactFragment = new HandshakeContactFragment();
            contactFragment.setArguments(args);
            changeFragment(contactFragment, "HandshakeContactFragment");
        });

        // initialize the recycler view for all 'this week'-handshakes
        //TODO: store handshakes in below array list
        ArrayList<HandshakeItem> handshakeItemsWeek = new ArrayList<>();

        RecyclerView recyclerWeek = view.findViewById(R.id.handshake_tab_recycler_week);
        recyclerWeek.setLayoutManager(new LinearLayoutManager(this.getContext()));
        HandshakeAdapter adapterWeek = new HandshakeAdapter(handshakeItemsWeek, stateEnum);
        recyclerWeek.setAdapter(adapterWeek);

        adapterWeek.setOnItemClickListener(position -> {
            Bundle args = new Bundle();
            args.putLong("id", handshakeItemsToday.get(position).getId());
            Fragment contactFragment = new HandshakeContactFragment();
            contactFragment.setArguments(args);
            changeFragment(contactFragment, "HandshakeContactFragment");
        });
    }
}
