package com.raising.app.fragments.handshake;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.profile.InvestorPublicProfileFragment;
import com.raising.app.fragments.profile.StartupPublicProfileFragment;
import com.raising.app.models.HandshakeOpenRequestItem;
import com.raising.app.util.recyclerViewAdapter.HandshakeAdapter;
import com.raising.app.util.recyclerViewAdapter.HandshakeOpenRequestAdapter;

import java.util.ArrayList;

public class HandshakeOpenRequestsFragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_open_requests), true);

        return inflater.inflate(R.layout.fragment_handshake_open_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: store open requests in the following array list
        ArrayList<HandshakeOpenRequestItem> openRequestItems = new ArrayList<>();

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
                changeFragment(fragment);
            } else {
                Fragment fragment = new InvestorPublicProfileFragment();
                fragment.setArguments(args);
                changeFragment(fragment);
            }
        });
    }
}
