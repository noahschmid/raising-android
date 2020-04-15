package com.raising.app.util;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.raising.app.fragments.handshake.HandshakeTabFragment;
import com.raising.app.models.HandshakeState;

public class HandshakePageAdapter extends FragmentPagerAdapter {
    private int numberOfItems;

    public HandshakePageAdapter(@NonNull FragmentManager fm, int behavior, int numberOfItems) {
        super(fm, behavior);
        this.numberOfItems = numberOfItems;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new HandshakeTabFragment();
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                args.putSerializable("handshakeState", HandshakeState.YOUR_TURN);
                fragment.setArguments(args);
                break;
            case 1:
                args.putSerializable("handshakeState", HandshakeState.PENDING);
                fragment.setArguments(args);
                break;
            case 2:
                args.putSerializable("handshakeState", HandshakeState.CLOSED);
                fragment.setArguments(args);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return numberOfItems;
    }
}
