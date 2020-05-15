package com.raising.app.fragments.onboarding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raising.app.R;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.RegisterLoginInformationFragment;

public class OnboardingPre1Fragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hideBottomNavigation(true);
        hideToolbar(true);
        return inflater.inflate(R.layout.fragment_onboarding_pre1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.onboarding_click_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new OnboardingPre2Fragment());
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
