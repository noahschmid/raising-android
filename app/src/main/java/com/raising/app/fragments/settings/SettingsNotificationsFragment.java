package com.raising.app.fragments.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.NotificationSettings;
import com.raising.app.models.PersonalSettings;
import com.raising.app.models.ViewState;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.RaisingTextWatcher;

import java.util.ArrayList;

public class SettingsNotificationsFragment extends RaisingFragment implements CompoundButton.OnCheckedChangeListener, RaisingTextWatcher {
    private final String TAG = "SettingsNotificationsFragment";
    private AutoCompleteTextView matchNumberInput;
    private SwitchMaterial generalSwitch, matchlistSwitch, leadsSwitch, requestSwitch, connectionSwitch;
    private ConstraintLayout specificSettings;
    private PersonalSettings personalSettings;
    private TextView generalNotificationText;
    private Button btnNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.settings_general), true);
        return inflater.inflate(R.layout.fragment_settings_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find all views
        btnNotifications = view.findViewById(R.id.button_notifications);
        btnNotifications.setOnClickListener(v -> updateNotificationSettings());

        specificSettings = view.findViewById(R.id.notifications_specific_settings);
        specificSettings.setVisibility(View.GONE);

        generalNotificationText = view.findViewById(R.id.notifications_general);

        ArrayList<Integer> integers = new ArrayList<>();
        for(int i = 0; i < getResources().getInteger(R.integer.maximumWeeklyMatchesNumber); i++) {
            integers.add(i+1);
        }

        NoFilterArrayAdapter<Integer> adapter = new NoFilterArrayAdapter(getContext(),
                R.layout.item_dropdown_menu, integers);
        matchNumberInput = view.findViewById(R.id.settings_matches_input);
        matchNumberInput.setAdapter(adapter);
        matchNumberInput.addTextChangedListener(this);

        // prepare all switches for usage
        generalSwitch = view.findViewById(R.id.notifications_switch_general);
        generalSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnNotifications.setEnabled(true);
            if (isChecked) {
                specificSettings.setVisibility(View.VISIBLE);
                generalNotificationText.setText(getString(R.string.settings_general_disable_all));
            } else {
                specificSettings.setVisibility(View.GONE);
                generalNotificationText.setText(getString(R.string.settings_general_enable_all));
                matchlistSwitch.setChecked(false);
                leadsSwitch.setChecked(false);
                requestSwitch.setChecked(false);
                connectionSwitch.setChecked(false);
            }
        });
        matchlistSwitch = view.findViewById(R.id.notifications_switch_matchlist);
        leadsSwitch = view.findViewById(R.id.notifications_switch_leads);
        requestSwitch = view.findViewById(R.id.notifications_switch_request);
        connectionSwitch = view.findViewById(R.id.notifications_switch_connection);

        // observe the settings view model to detect changes
        settingsViewModel.getViewState().observe(getViewLifecycleOwner(), viewState -> {
            if(viewState == ViewState.CACHED || viewState == ViewState.RESULT) {
                Log.d(TAG, "onViewCreated: Receive personal settings");
                personalSettings = settingsViewModel.getPersonalSettings().getValue();
                populateFragment();
            }
        });

        //processViewState(settingsViewModel.getViewState().getValue());
        settingsViewModel.loadSettings();

        // set on check changed listeners to all buttons to detect user interaction
        matchlistSwitch.setOnCheckedChangeListener(this);
        leadsSwitch.setOnCheckedChangeListener(this);
        requestSwitch.setOnCheckedChangeListener(this);
        connectionSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnNotifications.setEnabled(true);
    }

    /**
     * Populate the fragment with the users existing data
     */
    private void populateFragment() {
        if(personalSettings != null) {
            matchNumberInput.setText(String.valueOf(personalSettings.getNumberOfMatches()));

            Log.d(TAG, "populateSettings: " + personalSettings);
            personalSettings.getNotificationSettings().forEach(notificationSettings -> {
                switch (notificationSettings) {
                    case NEVER:
                        generalSwitch.setChecked(false);
                        if(!generalSwitch.isChecked()) {
                            specificSettings.setVisibility(View.GONE);
                        }
                        break;
                    case LEAD:
                        leadsSwitch.setChecked(true);
                        generalSwitch.setChecked(true);
                        break;
                    case REQUEST:
                        requestSwitch.setChecked(true);
                        generalSwitch.setChecked(true);
                        break;
                    case MATCHLIST:
                        matchlistSwitch.setChecked(true);
                        generalSwitch.setChecked(true);
                        break;
                    case CONNECTION:
                        connectionSwitch.setChecked(true);
                        generalSwitch.setChecked(true);
                        break;
                }
            });
        } else {
            settingsViewModel.addInitialSettings();

            generalSwitch.setChecked(false);
        }
        // hide button that confirms the changes
        btnNotifications.setEnabled(false);
    }

    /**
     * Update the users notification settings
     */
    private void updateNotificationSettings() {
        Log.d(TAG, "updateNotificationSettings: Update Settings");

        ArrayList<NotificationSettings> newNotificationSettings = new ArrayList<>();

        if(!(generalSwitch.isChecked())) {
            newNotificationSettings.add(NotificationSettings.NEVER);
        } else {
            if (matchlistSwitch.isChecked()) {
                newNotificationSettings.add(NotificationSettings.MATCHLIST);
            }
            if (leadsSwitch.isChecked()) {
                newNotificationSettings.add(NotificationSettings.LEAD);
            }
            if (requestSwitch.isChecked()) {
                newNotificationSettings.add(NotificationSettings.REQUEST);
            }

            if (connectionSwitch.isChecked()) {
                newNotificationSettings.add(NotificationSettings.CONNECTION);
            }
        }
        if(matchNumberInput.getText().toString().length() != 0) {
            personalSettings.setNumberOfMatches(Integer.parseInt(matchNumberInput.getText().toString()));
        }
        personalSettings.setNotificationSettings(newNotificationSettings);
        settingsViewModel.updatePersonalSettings(personalSettings);

        resetTab();
        popFragment(this);
    }

    // show button to apply changes only, when changes happened
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.notifications_switch_matchlist:
            case R.id.notifications_switch_leads:
            case R.id.notifications_switch_request:
            case R.id.notifications_switch_connection:
                btnNotifications.setEnabled(true);
                break;
        }
    }
}
