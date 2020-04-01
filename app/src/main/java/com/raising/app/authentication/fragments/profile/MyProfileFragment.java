package com.raising.app.authentication.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
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

public class MyProfileFragment extends RaisingFragment implements View.OnClickListener {
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

        /*
        if(RegistrationHandler.getAccountType().equals("startup")) {
            startUpLayout.setVisibility(View.VISIBLE);
            investorLayout.setVisibility(View.GONE);
        } else if (RegistrationHandler.getAccountType().equals("investor")) {
            startUpLayout.setVisibility(View.GONE);
            investorLayout.setVisibility(View.VISIBLE);
        }
         */



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

        investorProfileInformation = view.findViewById(R.id.button_myProfile_investor_profile_information);
        investorProfileInformation.setOnClickListener(this);

        investorMatching = view.findViewById(R.id.button_myProfile_investor_matching_criteria);
        investorMatching.setOnClickListener(this);

        investorPitch = view.findViewById(R.id.button_myProfile_investor_images);
        investorPitch.setOnClickListener(this);

        investorImages = view.findViewById(R.id.button_myProfile_investor_pitch);
        investorImages.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new MyProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isProfileFragment", IS_PROFILE_FRAGMENT);
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
                // fragment = new RegisterStartupImagesFragment();
                fragment.setArguments(bundle);
                break;

            case R.id.button_myProfile_startup_financial_requirements:
                fragment = new RegisterFinancialRequirementsFragment();
                fragment.setArguments(bundle);
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
                // fragment = new RegisterInvestorImagesFragment();
                fragment.setArguments(bundle);
                break;
        }
        changeFragment(fragment);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
