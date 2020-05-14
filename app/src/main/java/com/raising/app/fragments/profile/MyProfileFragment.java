package com.raising.app.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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

import com.raising.app.fragments.settings.SubscriptionFragment;
import com.raising.app.models.Investor;
import com.raising.app.models.Startup;
import com.raising.app.util.AccountService;
import com.raising.app.util.TabOrigin;
import com.raising.app.viewModels.TabViewModel;

public class MyProfileFragment extends RaisingFragment implements View.OnClickListener {
    private ConstraintLayout startUpLayout, investorLayout;
    private ConstraintLayout
            startupAccountInformation, startUpCompanyInformation, startupCompanyFigures,
            startUpMatching, startUpPitch, startupLabels, startUpImages,
            startUpFinancial, startUpStakeholder, startupPublicProfile,
            investorAccountInformation, investorProfileInformation, investorMatching, investorPitch,
            investorImages, investorPublicProfile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_my_profile), false);
        setBase(TabOrigin.PROFILE);

        tabViewModel = ViewModelProviders.of(getActivity())
                .get(TabViewModel.class);

        if(tabViewModel.getCurrentProfileFragment() != null) {
            changeFragment(tabViewModel.getCurrentProfileFragment());
        }
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

        startupAccountInformation = view.findViewById(R.id.profile_startup_login_information_layout);
        startupAccountInformation.setOnClickListener(this);

        startUpCompanyInformation = view.findViewById(R.id.profile_startup_company_layout);
        startUpCompanyInformation.setOnClickListener(this);

        startupCompanyFigures = view.findViewById(R.id.profile_startup_facts_layout);
        startupCompanyFigures.setOnClickListener(this);

        startUpMatching = view.findViewById(R.id.profile_startup_matching_layout);
        startUpMatching.setOnClickListener(this);

        startUpPitch = view.findViewById(R.id.profile_startup_pitch_layout);
        startUpPitch.setOnClickListener(this);

        startupLabels = view.findViewById(R.id.profile_startup_labels_layout);
        startupLabels.setOnClickListener(this);

        startUpImages = view.findViewById(R.id.profile_startup_images_layout);
        startUpImages.setOnClickListener(this);

        startUpFinancial = view.findViewById(R.id.profile_startup_financial_layout);
        startUpFinancial.setOnClickListener(this);

        startUpStakeholder = view.findViewById(R.id.profile_startup_stakeholder_layout);
        startUpStakeholder.setOnClickListener(this);

        startupPublicProfile = view.findViewById(R.id.profile_startup_view_profile_layout);
        startupPublicProfile.setOnClickListener(this);

        investorAccountInformation = view.findViewById(R.id.profile_investor_login_information_layout);
        investorAccountInformation.setOnClickListener(this);

        investorProfileInformation = view.findViewById(R.id.profile_investor_profile_information_layout);
        investorProfileInformation.setOnClickListener(this);

        investorMatching = view.findViewById(R.id.profile_investor_matching_layout);
        investorMatching.setOnClickListener(this);

        investorPitch = view.findViewById(R.id.profile_investor_pitch_layout);
        investorPitch.setOnClickListener(this);

        investorImages = view.findViewById(R.id.profile_investor_images_layout);
        investorImages.setOnClickListener(this);

        investorPublicProfile = view.findViewById(R.id.profile_investor_view_profile_layout);
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
            case R.id.profile_startup_login_information_layout:
            case R.id.profile_investor_login_information_layout:
                fragment = new RegisterLoginInformationFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_startup_company_layout:
                fragment = new RegisterCompanyInformationFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_startup_facts_layout:
                fragment = new RegisterCompanyFiguresFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_startup_matching_layout:
                fragment = new RegisterStartupMatchingFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_startup_pitch_layout:
                fragment = new RegisterStartupPitchFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_startup_labels_layout:
                fragment = new RegisterStartupLabelsFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_startup_images_layout:
                fragment = new RegisterStartupImagesFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_startup_financial_layout:
                fragment = new RegisterFinancialRequirementsFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_startup_view_profile_layout:
                bundle.putSerializable("startup", (Startup) currentAccount);
                fragment = new StartupPublicProfileFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_startup_stakeholder_layout:
                fragment = new RegisterStakeholderFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_investor_profile_information_layout:
                fragment = new RegisterProfileInformationFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_investor_matching_layout:
                fragment = new RegisterInvestorMatchingFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_investor_pitch_layout:
                fragment = new RegisterInvestorPitchFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_investor_images_layout:
                fragment = new RegisterInvestorImagesFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.profile_investor_view_profile_layout:
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
