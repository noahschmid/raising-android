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
import android.widget.TextView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.HandshakeItem;
import com.raising.app.models.HandshakeState;
import com.raising.app.util.recyclerViewAdapter.HandshakeAdapter;
import com.raising.app.util.recyclerViewAdapter.StartupProfileFounderAdapter;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HandshakeTabFragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_handshake_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: store items in this array list and handshake state in enum below
        ArrayList<HandshakeItem> handshakeItems = new ArrayList<>();
        Enum<HandshakeState> stateEnum;

        RecyclerView recyclerView = view.findViewById(R.id.handshake_tab_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        HandshakeAdapter handshakeAdapter
                = new HandshakeAdapter(handshakeItems, stateEnum);
        recyclerView.setAdapter(handshakeAdapter);
}
}
