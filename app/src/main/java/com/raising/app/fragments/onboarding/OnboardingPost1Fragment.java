package com.raising.app.fragments.onboarding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.settings.SettingsFragment;

public class OnboardingPost1Fragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hideBottomNavigation(true);
        hideToolbar(true);
        return inflater.inflate(R.layout.fragment_onboarding_post1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = new Bundle();
        if (getArguments() != null && getArguments().getBoolean("settings")) {
            args.putBoolean("settings", getArguments().getBoolean("settings"));
            view.findViewById(R.id.text_onboarding_skip).setVisibility(View.INVISIBLE);
        } else {
            // set the click listeners for next and skip buttons
            view.findViewById(R.id.text_onboarding_skip).setOnClickListener(v -> {
                tabViewModel.resetCurrentSettingsFragment();
                if (getArguments() != null && getArguments().getBoolean("settings")) {
                    clearBackstackAndReplace(new SettingsFragment());
                } else {
                    disablePostOnboarding();
                    clearBackstackAndReplace(new MatchesFragment());
                }
            });
        }

        view.findViewById(R.id.text_onboarding_next).setOnClickListener(v -> {
            Fragment fragment = new OnboardingPost2Fragment();
            tabViewModel.resetCurrentSettingsFragment();
            if(getArguments() != null && getArguments().getBoolean("settings")) {
                fragment.setArguments(args);
                changeFragmentWithoutBackstack(fragment);
            } else {
                changeFragmentWithoutBackstack(fragment);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        hideBottomNavigation(false);
        hideToolbar(false);
    }
}
