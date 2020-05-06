package com.raising.app.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.R;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.onboarding.OnboardingPost1Fragment;
import com.raising.app.models.PersonalSettings;
import com.raising.app.models.ViewState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.viewModels.MatchesViewModel;
import com.raising.app.viewModels.SettingsViewModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

public class SettingsFragment extends RaisingFragment implements View.OnClickListener {
    private final String TAG = "SettingsFragment";
    private Button btnNotifications, btnAbout, btnReportProblem, btnFeedback, btnOnboarding, btnLogout;
    private AutoCompleteTextView matchNumberInput;
    private PersonalSettings personalSettings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_settings), false);

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnNotifications = view.findViewById(R.id.button_settings_notifications);
        btnNotifications.setOnClickListener(this);
        btnAbout = view.findViewById(R.id.button_settings_about);
        btnAbout.setOnClickListener(this);
        btnReportProblem = view.findViewById(R.id.button_settings_report_problem);
        btnReportProblem.setOnClickListener(this);
        btnFeedback = view.findViewById(R.id.button_settings_feedback);
        btnFeedback.setOnClickListener(this);
        btnLogout = view.findViewById(R.id.button_settings_logout);
        btnLogout.setOnClickListener(this);
        btnOnboarding = view.findViewById(R.id.button_settings_onboarding);
        btnOnboarding.setOnClickListener(this);

        ArrayList<Integer> integers = new ArrayList<>();
        for(int i = 0; i < getResources().getInteger(R.integer.maximumWeeklyMatchesNumber); i++) {
            integers.add(i+1);
        }

        NoFilterArrayAdapter<Integer> adapter = new NoFilterArrayAdapter(getContext(),
                R.layout.item_dropdown_menu, integers);
        matchNumberInput = view.findViewById(R.id.settings_matches_input);
        matchNumberInput.setAdapter(adapter);

        settingsViewModel.getViewState().observe(getViewLifecycleOwner(), viewState -> {
            Log.d(TAG, "onViewCreated: SettingsViewState" + viewState.toString());
            processViewState(viewState);
            if(viewState == ViewState.CACHED || viewState == ViewState.RESULT) {
                personalSettings = settingsViewModel.getPersonalSettings().getValue();
                populateSettings();
            }
        });
        processViewState(settingsViewModel.getViewState().getValue());
        settingsViewModel.loadSettings();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_settings_notifications:
                changeFragment(new SettingsNotificationsFragment(), "SettingsNotificationFragment");
                break;
            case R.id.button_settings_about:
                changeFragment(new SettingsAboutFragment(), "SettingsAboutFragment");
                break;
            case R.id.button_settings_report_problem:
                contactRaising(true);
                break;
            case R.id.button_settings_feedback:
                contactRaising(false);
                break;
            case R.id.button_settings_onboarding:
                Fragment fragment = new OnboardingPost1Fragment();
                Bundle args = new Bundle();
                args.putBoolean("settings", true);
                fragment.setArguments(args);
                changeFragment(fragment, "Onboarding");
                break;
            case R.id.button_settings_logout:
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

    private void populateSettings() {
        if(personalSettings != null) {
            matchNumberInput.setText(String.valueOf(personalSettings.getNumberOfMatches()));
        } else {
            settingsViewModel.addInitialSettings();
        }
    }

    private void logout() {
        Log.d("debugMessage", "logout()");
        AuthenticationHandler.logout();
        clearBackstackAndReplace(new LoginFragment());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Updating Settings");

        personalSettings.setNumberOfMatches(Integer.parseInt(matchNumberInput.getText().toString()));

        settingsViewModel.updatePersonalSettings(personalSettings);
    }
}
