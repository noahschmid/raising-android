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
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.RegisterLoginInformationFragment;

public class OnboardingPre2Fragment extends RaisingFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hideBottomNavigation(true);
        hideToolbar(true);
        return inflater.inflate(R.layout.fragment_onboarding_pre2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.text_onboarding_skip).setOnClickListener(v -> {
            resetTab();
            disablePreOnboarding();
            clearBackstackAndReplace(new RegisterLoginInformationFragment());
        });
        view.findViewById(R.id.text_onboarding_next).setOnClickListener(v -> changeFragment(new OnboardingPre3Fragment()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        hideBottomNavigation(false);
        hideToolbar(false);
    }
}
