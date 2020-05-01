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
import com.raising.app.fragments.registration.RegisterLoginInformationFragment;
import com.raising.app.fragments.registration.investor.RegisterInvestorImagesFragment;
import com.raising.app.fragments.registration.investor.RegisterInvestorMatchingFragment;
import com.raising.app.fragments.registration.investor.RegisterInvestorPitchFragment;
import com.raising.app.fragments.registration.investor.RegisterProfileInformationFragment;
import com.raising.app.fragments.registration.startup.RegisterCompanyFiguresFragment;
import com.raising.app.fragments.registration.startup.RegisterCompanyInformationFragment;
import com.raising.app.fragments.registration.startup.RegisterFinancialRequirementsFragment;
import com.raising.app.fragments.registration.startup.RegisterStakeholderFragment;
import com.raising.app.fragments.registration.startup.RegisterStartupImagesFragment;
import com.raising.app.fragments.registration.startup.RegisterStartupLabelsFragment;
import com.raising.app.fragments.registration.startup.RegisterStartupMatchingFragment;
import com.raising.app.fragments.registration.startup.RegisterStartupPitchFragment;

import com.raising.app.fragments.registration.startup.RegisterStartupVideoFragment;
import com.raising.app.models.Investor;
import com.raising.app.models.Startup;
import com.raising.app.util.AccountService;

public class MyProfileFragment extends RaisingFragment implements View.OnClickListener {
    private FrameLayout startUpLayout, investorLayout;
    private Button
            startupAccountInformation, startUpCompanyInformation, startupCompanyFigures,
            startUpMatching, startUpPitch, startupLabels, startUpImages, startupVideo,
            startUpFinancial, startUpStakeholder, startupSubscription, startupPublicProfile,
            investorAccountInformation, investorProfileInformation, investorMatching, investorPitch,
            investorImages, investorSubscription, investorPublicProfile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        customizeAppBar(getString(R.string.toolbar_title_my_profile), false);
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

        startupAccountInformation = view.findViewById(R.id.button_myProfile_startup_account_information);
        startupAccountInformation.setOnClickListener(this);

        startUpCompanyInformation = view.findViewById(R.id.button_myProfile_startup_company_information);
        startUpCompanyInformation.setOnClickListener(this);

        startupCompanyFigures = view.findViewById(R.id.button_myProfile_startup_company_figures);
        startupCompanyFigures.setOnClickListener(this);

        startUpMatching = view.findViewById(R.id.button_myProfile_startup_matching_criteria);
        startUpMatching.setOnClickListener(this);

        startUpPitch = view.findViewById(R.id.button_myProfile_startup_pitch);
        startUpPitch.setOnClickListener(this);

        startupLabels = view.findViewById(R.id.button_myProfile_startup_labels);
        startupLabels.setOnClickListener(this);

        startUpImages = view.findViewById(R.id.button_myProfile_startup_images);
        startUpImages.setOnClickListener(this);

        startupVideo = view.findViewById(R.id.button_myProfile_startup_video);
        startupVideo.setOnClickListener(this);

        startUpFinancial = view.findViewById(R.id.button_myProfile_startup_financial_requirements);
        startUpFinancial.setOnClickListener(this);

        startUpStakeholder = view.findViewById(R.id.button_myProfile_startup_stakeholders);
        startUpStakeholder.setOnClickListener(this);

        startupSubscription = view.findViewById(R.id.button_myProfile_startup_subscription);
        startupSubscription.setOnClickListener(this);

        startupPublicProfile = view.findViewById(R.id.button_myProfile_startup_public_profile);
        startupPublicProfile.setOnClickListener(this);

        investorAccountInformation = view.findViewById(R.id.button_myProfile_investor_account_information);
        investorAccountInformation.setOnClickListener(this);

        investorProfileInformation = view.findViewById(R.id.button_myProfile_investor_profile_information);
        investorProfileInformation.setOnClickListener(this);

        investorMatching = view.findViewById(R.id.button_myProfile_investor_matching_criteria);
        investorMatching.setOnClickListener(this);

        investorPitch = view.findViewById(R.id.button_myProfile_investor_images);
        investorPitch.setOnClickListener(this);

        investorImages = view.findViewById(R.id.button_myProfile_investor_pitch);
        investorImages.setOnClickListener(this);

        investorSubscription = view.findViewById(R.id.button_myProfile_investor_subscription);
        investorSubscription.setOnClickListener(this);

        investorPublicProfile = view.findViewById(R.id.button_myProfile_investor_public_profile);
        investorPublicProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // if data not yet loaded don't proceed
        if(!(currentAccount instanceof Startup) &&
        !(currentAccount instanceof Investor)) {
            return;
        }
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putBoolean("editMode", true);
        switch (v.getId()) {
            case R.id.button_myProfile_startup_account_information:
            case R.id.button_myProfile_investor_account_information:
                fragment = new RegisterLoginInformationFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_company_information:
                fragment = new RegisterCompanyInformationFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_company_figures:
                fragment = new RegisterCompanyFiguresFragment();
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
            case R.id.button_myProfile_startup_labels:
                fragment = new RegisterStartupLabelsFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_images:
                fragment = new RegisterStartupImagesFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_video:
                fragment = new RegisterStartupVideoFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_financial_requirements:
                fragment = new RegisterFinancialRequirementsFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_public_profile:
                bundle.putSerializable("startup", (Startup) currentAccount);
                fragment = new StartupPublicProfileFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.button_myProfile_startup_subscription:
            case R.id.button_myProfile_investor_subscription:
                // fragment = new SubscriptionFragment();
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
                bundle.putSerializable("investor", (Investor)currentAccount);
                fragment = new InvestorPublicProfileFragment();
                fragment.setArguments(bundle);
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
