package com.raising.app.fragments.onboarding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.RaisingFragment;

public class OnboardingPost3Fragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hideBottomNavigation(true);
        customizeAppBar(" ", false);
        return inflater.inflate(R.layout.fragment_onboarding_post3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.text_onboarding_skip).setOnClickListener(v -> {
            disablePostOnboarding();
            clearBackstackAndReplace(new MatchesFragment());
        });
        view.findViewById(R.id.text_onboarding_next).setOnClickListener(v -> changeFragment(new OnboardingPost4Fragment()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        hideBottomNavigation(false);
    }
}
