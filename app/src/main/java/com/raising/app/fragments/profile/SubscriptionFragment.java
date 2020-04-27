package com.raising.app.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;

public class SubscriptionFragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customizeAppBar(getString(R.string.toolbar_profile_subscription), true);
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }
}
