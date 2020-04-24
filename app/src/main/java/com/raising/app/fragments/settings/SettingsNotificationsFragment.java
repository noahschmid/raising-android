package com.raising.app.fragments.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.NotificationSettings;
import com.raising.app.models.PersonalSettings;
import com.raising.app.models.ViewState;
import com.raising.app.viewModels.SettingsViewModel;

import java.util.ArrayList;

public class SettingsNotificationsFragment extends RaisingFragment {
    private final String TAG = "SettingsNotificationsFragment";
    private SwitchMaterial generalSwitch, matchlistSwitch, requestSwitch, connectionSwitch;
    private ConstraintLayout specificSettings;

    private SettingsViewModel settingsViewModel;
    private PersonalSettings personalSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.settings_notifications), true);
        return inflater.inflate(R.layout.fragment_settings_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingsViewModel = ViewModelProviders.of(getActivity())
                .get(SettingsViewModel.class);

        specificSettings = view.findViewById(R.id.notifications_specific_settings);

        generalSwitch = view.findViewById(R.id.notifications_switch_general);
        generalSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                specificSettings.setVisibility(View.VISIBLE);
            } else {
                specificSettings.setVisibility(View.GONE);
            }
        });
        matchlistSwitch = view.findViewById(R.id.notifications_switch_matchlist);
        requestSwitch = view.findViewById(R.id.notifications_switch_request);
        connectionSwitch = view.findViewById(R.id.notifications_switch_connection);

        settingsViewModel.getViewState().observe(getViewLifecycleOwner(), viewState -> {
            processViewState(viewState);
            if(viewState == ViewState.CACHED || viewState == ViewState.RESULT) {
                Log.d(TAG, "onViewCreated: Receive personal settings");
                personalSettings = settingsViewModel.getPersonalSettings().getValue();

                if (personalSettings != null) {
                    personalSettings.getNotificationSettings().forEach(notificationSettings -> {
                        Log.d(TAG, "onViewCreated: Setting: " + notificationSettings.name());
                        switch (notificationSettings) {
                            case NEVER:
                                generalSwitch.setChecked(false);
                                if(!generalSwitch.isChecked()) {
                                    specificSettings.setVisibility(View.GONE);
                                }
                                break;
                            case LEAD:
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
                    generalSwitch.setChecked(false);
                }
            }
        });
        processViewState(settingsViewModel.getViewState().getValue());
        settingsViewModel.loadSettings();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: Updating settings");

        ArrayList<NotificationSettings> newNotificationSettings = new ArrayList<>();

        if(!(generalSwitch.isChecked())) {
            newNotificationSettings.add(NotificationSettings.NEVER);
        } else {
            if (matchlistSwitch.isChecked()) {
                newNotificationSettings.add(NotificationSettings.MATCHLIST);
            }
            if (requestSwitch.isChecked()) {
                newNotificationSettings.add(NotificationSettings.REQUEST);
            }

            if (connectionSwitch.isChecked()) {
                newNotificationSettings.add(NotificationSettings.CONNECTION);
            }
        }

        personalSettings.setNotificationSettings(newNotificationSettings);
        settingsViewModel.updatePersonalSettings(personalSettings);
    }
}
