package com.raising.app.fragments.settings;

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

import com.raising.app.R;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.NoFilterArrayAdapter;

import java.util.ArrayList;

public class SettingsFragment extends RaisingFragment implements View.OnClickListener {
    private Button btnNotifications, btnAbout, btnReportProblem, btnFeedback, btnLogout;
    private AutoCompleteTextView languageInput, matchNumberInput;

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
        //TODO: preselect users preferred language
        matchNumberInput = view.findViewById(R.id.settings_matches_input);
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
            case R.id.button_settings_about:
            case R.id.button_settings_report_problem:
            case R.id.button_settings_feedback:
                //TODO: implement action
                break;
            case R.id.button_settings_logout:
                logout();
                break;
            default:
                break;
        }
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
