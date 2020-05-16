package com.raising.app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.raising.app.R;
import com.raising.app.fragments.settings.SettingsFragment;
import com.raising.app.fragments.settings.SubscriptionFragment;
import com.raising.app.util.TabOrigin;

public class UnlockPremiumFragment extends RaisingFragment {
    private final String TAG = "UnlockPremiumFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.unlock_now), true);
        return inflater.inflate(R.layout.fragment_unlock_premium, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnUnlockPremium = view.findViewById(R.id.button_unlock_premium);

        resetTab();

        btnUnlockPremium.setOnClickListener(v -> {
            Fragment fragment = new SubscriptionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("origin", TabOrigin.SETTINGS.toString());
            fragment.setArguments(bundle);
            tabViewModel.setCurrentSettingsFragment(fragment);
            clearBackstack();
            selectBottomNavigation(R.id.nav_settings);
        });
    }

    @Override
    public void onDestroyView() {
        hideBottomNavigation(false);
        super.onDestroyView();
    }
}
