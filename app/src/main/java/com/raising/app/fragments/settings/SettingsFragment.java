package com.raising.app.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.raising.app.R;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.onboarding.OnboardingPost1Fragment;
import com.raising.app.util.AuthenticationHandler;

public class SettingsFragment extends RaisingFragment implements View.OnClickListener {
    private final String TAG = "SettingsFragment";
    private ConstraintLayout subscriptionLayout, generalLayout, aboutLayout,
            onboardingLayout, feedbackLayout, problemLayout, logoutLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_settings), false);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
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
                changeFragment(fragment, "Onboarding");
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

    private void contactRaising(boolean isProblemReport) {
        Intent interactionIntent = new Intent(Intent.ACTION_SENDTO);
        interactionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String subject = "";
        String body = "";
        String targetAddress = getString(R.string.settings_target_email_address);
        if(isProblemReport) {
            subject = getString(R.string.settings_problem_report_subject);
            body = getString(R.string.settings_problem_report_body);
        } else {
            subject = getString(R.string.settings_feedback_subject);
            body = getString(R.string.settings_feedback_body);
        }

        String uriText = "mailto:" + targetAddress + "?subject=" + subject + "&body=" + body;
        interactionIntent.setData(Uri.parse(uriText));
        startActivity(interactionIntent);
    }

    private void logout() {
        Log.d("debugMessage", "logout()");
        AuthenticationHandler.logout();
        clearBackstackAndReplace(new LoginFragment());
    }
}
