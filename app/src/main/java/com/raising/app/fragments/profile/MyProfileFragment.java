package com.raising.app.fragments.profile;

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
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.investor.RegisterInvestorImagesFragment;
import com.raising.app.fragments.registration.investor.RegisterInvestorMatchingFragment;
import com.raising.app.fragments.registration.investor.RegisterInvestorPitchFragment;
import com.raising.app.fragments.registration.investor.RegisterProfileInformationFragment;
import com.raising.app.fragments.registration.startup.RegisterCompanyInformationFragment;
import com.raising.app.fragments.registration.startup.RegisterFinancialRequirementsFragment;
import com.raising.app.fragments.registration.startup.RegisterStakeholderFragment;
import com.raising.app.fragments.registration.startup.RegisterStartupImagesFragment;
import com.raising.app.fragments.registration.startup.RegisterStartupMatchingFragment;
import com.raising.app.fragments.registration.startup.RegisterStartupPitchFragment;

import com.raising.app.util.AccountService;
import com.raising.app.util.AuthenticationHandler;

public class MyProfileFragment extends RaisingFragment implements View.OnClickListener {
    private FrameLayout startUpLayout, investorLayout;
    private Button startUpCompanyInformation, startUpMatching, startUpPitch,
            startUpImages, startUpFinancial, startUpStakeholder, startupPublicProfile,
            investorProfileInformation, investorMatching, investorPitch,
            investorImages, investorPublicProfile;

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

        if(AccountService.isStartup()) {
            startUpLayout.setVisibility(View.VISIBLE);
            investorLayout.setVisibility(View.GONE);
        } else {
            startUpLayout.setVisibility(View.GONE);
            investorLayout.setVisibility(View.VISIBLE);
        }

        startUpCompanyInformation = view.findViewById(R.id.button_myProfile_startup_company_information);
        startUpCompanyInformation.setOnClickListener(this);

        startUpMatching = view.findViewById(R.id.button_myProfile_startup_matching_criteria);
        startUpMatching.setOnClickListener(this);

        startUpPitch = view.findViewById(R.id.button_myProfile_startup_pitch);
        startUpPitch.setOnClickListener(this);

        startUpImages = view.findViewById(R.id.button_myProfile_startup_images);
        startUpImages.setOnClickListener(this);

        startUpFinancial = view.findViewById(R.id.button_myProfile_startup_financial_requirements);
        startUpFinancial.setOnClickListener(this);

        startUpStakeholder = view.findViewById(R.id.button_myProfile_startup_stakeholders);
        startUpStakeholder.setOnClickListener(this);

        startupPublicProfile = view.findViewById(R.id.button_myProfile_startup_public_profile);
        startupPublicProfile.setOnClickListener(this);

        investorProfileInformation = view.findViewById(R.id.button_myProfile_investor_profile_information);
        investorProfileInformation.setOnClickListener(this);

        investorMatching = view.findViewById(R.id.button_myProfile_investor_matching_criteria);
        investorMatching.setOnClickListener(this);

        investorPitch = view.findViewById(R.id.button_myProfile_investor_images);
        investorPitch.setOnClickListener(this);

        investorImages = view.findViewById(R.id.button_myProfile_investor_pitch);
        investorImages.setOnClickListener(this);

        investorPublicProfile = view.findViewById(R.id.button_myProfile_investor_public_profile);
        investorPublicProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putBoolean("editMode", true);
        switch (v.getId()) {
            case R.id.button_myProfile_startup_company_information:
                fragment = new RegisterCompanyInformationFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_matching_criteria:
                fragment = new RegisterStartupMatchingFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_pitch:
                fragment = new RegisterStartupPitchFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_images:
                fragment = new RegisterStartupImagesFragment();
                fragment.setArguments(bundle);
                break;

            case R.id.button_myProfile_startup_financial_requirements:
                fragment = new RegisterFinancialRequirementsFragment();
                fragment.setArguments(bundle);
                break;

            case R.id.button_myProfile_startup_public_profile:
                fragment = new StartupPublicProfileFragment();
                break;

            case R.id.button_myProfile_startup_stakeholders:
                fragment = new RegisterStakeholderFragment();
                fragment.setArguments(bundle);
                break;

            case R.id.button_myProfile_investor_profile_information:
                fragment = new RegisterProfileInformationFragment();
                fragment.setArguments(bundle);
                break;

            case R.id.button_myProfile_investor_matching_criteria:
                fragment = new RegisterInvestorMatchingFragment();
                fragment.setArguments(bundle);
                break;

            case R.id.button_myProfile_investor_pitch:
                fragment = new RegisterInvestorPitchFragment();
                fragment.setArguments(bundle);
                break;

            case R.id.button_myProfile_investor_images:
                fragment = new RegisterInvestorImagesFragment();
                fragment.setArguments(bundle);
                break;

            case R.id.button_myProfile_investor_public_profile:
                fragment = new InvestorPublicProfileFragment();
                break;
        }

        if(fragment != null) {
            changeFragment(fragment);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
