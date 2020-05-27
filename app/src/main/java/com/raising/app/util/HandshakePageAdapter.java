package com.raising.app.util;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.raising.app.fragments.leads.LeadsFragment;
import com.raising.app.models.leads.LeadState;

/**
 * Adapter that handles the tabs on the LeadsFragment
 */

public class HandshakePageAdapter extends FragmentPagerAdapter {
    private int numberOfItems;

    public HandshakePageAdapter(@NonNull FragmentManager fm, int behavior, int numberOfItems) {
        super(fm, behavior);
        this.numberOfItems = numberOfItems;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new LeadsFragment();
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                args.putSerializable("leadsState", LeadState.YOUR_TURN);
                fragment.setArguments(args);
                break;
            case 1:
                args.putSerializable("leadsState", LeadState.PENDING);
                fragment.setArguments(args);
                break;
            case 2:
                args.putSerializable("leadsState", LeadState.CLOSED);
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
