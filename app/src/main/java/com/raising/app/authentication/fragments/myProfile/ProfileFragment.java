package com.raising.app.authentication.fragments.myProfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.registration.investor.RegisterInvestorMatchingFragment;
import com.raising.app.authentication.fragments.registration.investor.RegisterInvestorPitchFragment;
import com.raising.app.authentication.fragments.registration.investor.RegisterProfileInformationFragment;
import com.raising.app.authentication.fragments.registration.startup.RegisterCompanyInformationFragment;
import com.raising.app.authentication.fragments.registration.startup.RegisterFinancialRequirementsFragment;
import com.raising.app.authentication.fragments.registration.startup.RegisterStakeholderFragment;
import com.raising.app.authentication.fragments.registration.startup.RegisterStartupMatchingFragment;
import com.raising.app.authentication.fragments.registration.startup.RegisterStartupPitchFragment;

public class ProfileFragment extends RaisingFragment {
    private FrameLayout startUpLayout, investorLayout;
    private Button startUpCompanyInformation, startUpMatching, startUpPitch,
            startUpImages, startUpFinancial, startUpStakeholder,
            investorProfileInformation, investorMatching, investorPitch, investorImages;

    private final boolean IS_PROFILE_FRAGMENT = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startUpLayout = view.findViewById(R.id.myProfile_startUp_layout);
        investorLayout = view.findViewById(R.id.myProfile_investor_layout);

        /* TODO: check if the logged in user is a startup or investor
        if(userIsInvestor) {
            startUpLayout.setVisibility(View.GONE);
            investorLayout.setVisibility(View.VISIBLE);
        } else {
            startUpLayout.setVisibility(View.VISIBLE);
            investorLayout.setVisibility(View.GONE);
        }
         */

        Bundle bundle = new Bundle();
        bundle.putBoolean("isProfileFragment", IS_PROFILE_FRAGMENT);

        startUpCompanyInformation = view.findViewById(R.id.button_myProfile_startup_company_information);
        startUpCompanyInformation.setOnClickListener(v -> {
            Fragment fragment = new RegisterCompanyInformationFragment();
            fragment.setArguments(bundle);
            changeFragment(fragment);
        });
        startUpMatching = view.findViewById(R.id.button_myProfile_startup_matching_criteria);
        startUpMatching.setOnClickListener(v -> {
            Fragment fragment = new RegisterStartupMatchingFragment();
            fragment.setArguments(bundle);
            changeFragment(fragment);
        });
        startUpPitch = view.findViewById(R.id.button_myProfile_startup_pitch);
        startUpPitch.setOnClickListener(v -> {
            Fragment fragment = new RegisterStartupPitchFragment();
            fragment.setArguments(bundle);
            changeFragment(fragment);
        });
        startUpImages = view.findViewById(R.id.button_myProfile_startup_images);
        // startUpImages.setOnClickListener(v -> {
        //            Fragment fragment = new RegisterStartupImagesFragment();
        //            fragment.setArguments(bundle);
        //            changeFragment(fragment);
        //        });
        startUpFinancial = view.findViewById(R.id.button_myProfile_startup_financial_requirements);
        startUpFinancial.setOnClickListener(v -> {
            Fragment fragment = new RegisterFinancialRequirementsFragment();
            fragment.setArguments(bundle);
            changeFragment(fragment);
        });
        startUpStakeholder = view.findViewById(R.id.button_myProfile_startup_stakeholders);
        startUpStakeholder.setOnClickListener(v -> {
            Fragment fragment = new RegisterStakeholderFragment();
            fragment.setArguments(bundle);
            changeFragment(fragment);
        });

        investorProfileInformation = view.findViewById(R.id.button_myProfile_investor_profile_information);
        investorProfileInformation.setOnClickListener(v -> {
            Fragment fragment = new RegisterProfileInformationFragment();
            fragment.setArguments(bundle);
            changeFragment(fragment);
        });
        investorMatching = view.findViewById(R.id.button_myProfile_investor_matching_criteria);
        investorMatching.setOnClickListener(v -> {
            Fragment fragment = new RegisterInvestorMatchingFragment();
            fragment.setArguments(bundle);
            changeFragment(fragment);
        });
        investorPitch = view.findViewById(R.id.button_myProfile_investor_images);
        investorPitch.setOnClickListener(v -> {
            Fragment fragment = new RegisterInvestorPitchFragment();
            fragment.setArguments(bundle);
            changeFragment(fragment);
        });
        investorImages = view.findViewById(R.id.button_myProfile_investor_pitch);
        // investorImages.setOnClickListener(v -> {
        //            Fragment fragment = new RegisterInvestorImagesFragment();
        //            fragment.setArguments(bundle);
        //            changeFragment(fragment);
        //        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
