package com.raising.app.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.raising.app.R;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.onboarding.OnboardingPost1Fragment;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.TabOrigin;
import com.raising.app.viewModels.TabViewModel;

public class SettingsFragment extends RaisingFragment implements View.OnClickListener {
    private final String TAG = "SettingsFragment";
    private ConstraintLayout subscriptionLayout, generalLayout, aboutLayout,
            onboardingLayout, feedbackLayout, problemLayout, logoutLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        hideToolbar(false);
        hideBottomNavigation(false);

        customizeAppBar(getString(R.string.toolbar_title_settings), false);
        setBase(TabOrigin.SETTINGS);

        tabViewModel = ViewModelProviders.of(getActivity())
                .get(TabViewModel.class);

        if(tabViewModel.getCurrentSettingsFragment() != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount() - 1; ++i) {
                fm.popBackStack();
            }
            changeFragment(tabViewModel.getCurrentSettingsFragment());
        }

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find all views
        subscriptionLayout = view.findViewById(R.id.settings_subscription_layout);
        subscriptionLayout.setOnClickListener(this);
        generalLayout = view.findViewById(R.id.settings_notifications_layout);
        generalLayout.setOnClickListener(this);
        aboutLayout = view.findViewById(R.id.settings_about_layout);
        aboutLayout.setOnClickListener(this);
        onboardingLayout = view.findViewById(R.id.settings_onboarding_layout);
        onboardingLayout.setOnClickListener(this);
        feedbackLayout = view.findViewById(R.id.settings_feedback_layout);
        feedbackLayout.setOnClickListener(this);
        problemLayout = view.findViewById(R.id.settings_report_layout);
        problemLayout.setOnClickListener(this);
        logoutLayout = view.findViewById(R.id.settings_logout_layout);
        logoutLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_subscription_layout:
                changeFragment(new SubscriptionFragment(), "SubscriptionFragment");
                break;
            case R.id.settings_notifications_layout:
                changeFragment(new SettingsNotificationsFragment(), "SettingsNotificationFragment");
                break;
            case R.id.settings_about_layout:
                changeFragment(new SettingsAboutFragment(), "SettingsAboutFragment");
                break;
            case R.id.settings_onboarding_layout:
                Fragment fragment = new OnboardingPost1Fragment();
                Bundle args = new Bundle();
                args.putBoolean("settings", true);
                fragment.setArguments(args);
                changeFragment(fragment);
                break;
            case R.id.settings_feedback_layout:
                contactRaising(false);
                break;
            case R.id.settings_report_layout:
                contactRaising(true);
                break;
            case R.id.settings_logout_layout:
                logout();
                break;
            default:
                break;
        }
    }

    /**
     * Open a new activity with the users preferred mail service to write a feedback/complaint mail
     * @param isProblemReport true, if user wants to report a problem
     *                        false, if user wants to give feedback
     */
    private void contactRaising(boolean isProblemReport) {
        Intent interactionIntent = new Intent(Intent.ACTION_SENDTO);
        interactionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String subject = "";
        String body = "";
        String targetAddress = getString(R.string.settings_target_email_address);
        if(isProblemReport) {
            subject = getString(R.string.settings_problem_report_subject);
        } else {
            subject = getString(R.string.settings_feedback_subject);
        }

        String uriText = "mailto:" + targetAddress + "?subject=" + subject + "&body=" + body;
        interactionIntent.setData(Uri.parse(uriText));
        startActivity(interactionIntent);
    }

    /**
     * Logout the current user
     */
    private void logout() {
        settingsViewModel.onLogoutResetToken();
        tabViewModel.resetAll();
        AuthenticationHandler.logout();
        selectBottomNavigation(R.id.nav_matches);
        clearBackstackAndReplace(new LoginFragment());
    }
}
