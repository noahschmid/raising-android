package com.raising.app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.raising.app.R;
import com.raising.app.fragments.settings.SubscriptionFragment;

public class UnlockPremiumFragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hideBottomNavigation(true);
        hideToolbar(true);
        return inflater.inflate(R.layout.fragment_unlock_premium, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnUnlockPremium = view.findViewById(R.id.button_unlock_premium);
        btnUnlockPremium.setOnClickListener(v -> changeFragment(new SubscriptionFragment()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        hideBottomNavigation(false);
        hideToolbar(false);
    }
}
