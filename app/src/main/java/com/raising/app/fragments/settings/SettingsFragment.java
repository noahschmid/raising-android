package com.raising.app.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.NoFilterArrayAdapter;

import java.util.ArrayList;

public class SettingsFragment extends RaisingFragment implements View.OnClickListener {
    private Button btnNotifications, btnAbout, btnReportProblem, btnFeedback, btnLogout;
    private AutoCompleteTextView languageInput;
    private EditText matchNumberInput;

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

        ArrayList<String> languages = new ArrayList<>();
        languages.add("English");

        NoFilterArrayAdapter<String> adapterType = new NoFilterArrayAdapter(getContext(),
                R.layout.item_dropdown_menu, languages);

        languageInput = view.findViewById(R.id.settings_language_input);
        languageInput.setAdapter(adapterType);
        languageInput.setText(languages.get(0));
        matchNumberInput = view.findViewById(R.id.settings_matches_input);
        matchNumberInput.setText("10");

        TextInputLayout matchNumberLayout = view.findViewById(R.id.settings_matches_layout);
        matchNumberLayout.setHelperText(getString(R.string.settings_max_number_weekly_matches)
                + getResources().getInteger(R.integer.maximumWeeklyMatchesNumber));
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
            case R.id.button_settings_logout:
                logout();
                break;
            default:
                break;
        }
    }

    private void contactRaising(boolean isProblemReport) {
        Intent interactionIntent = new Intent(Intent.ACTION_SENDTO);
        String [] addresses = {"lorenz.caliezi@gmail.com"};
        interactionIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
        interactionIntent.setData(Uri.parse("mailto:"));
        if(isProblemReport) {
            interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_problem_report_subject));
            interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_problem_report_body));
        } else {
            interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_feedback_subject));
            interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_feedback_body));
        }
        startActivity(interactionIntent);
    }

    private void logout() {
        Log.d("debugMessage", "logout()");
        AuthenticationHandler.logout();
        getActivitiesFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        String language = languageInput.getText().toString();
        String numberOfMatches = matchNumberInput.getText().toString();
        //TODO: communicate new values to backend and store in internal storage
    }
}
