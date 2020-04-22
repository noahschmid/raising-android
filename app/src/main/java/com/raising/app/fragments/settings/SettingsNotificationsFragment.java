package com.raising.app.fragments.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.NotificationSettings;
import com.raising.app.models.PersonalSettings;
import com.raising.app.models.ViewState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;

import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SettingsNotificationsFragment extends RaisingFragment {
    private final String TAG = "SettingsNotificationsFragment";
    private SwitchMaterial generalSwitch, matchlistSwitch, contactSwitch, connectionSwitch;
    private ConstraintLayout specificSettings;
    ArrayList<NotificationSettings> personalSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.settings_notifications), true);
        return inflater.inflate(R.layout.fragment_settings_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personalSettings = getCachedNotificationSettings();
        if (personalSettings != null) {
            personalSettings.forEach(notificationSettings -> {
                Log.d(TAG, "onViewCreated: Setting: " + notificationSettings.name());
                switch (notificationSettings) {
                    case NEVER:
                        generalSwitch.setChecked(false);
                        break;
                    case LEAD:
                    case REQUEST:
                        contactSwitch.setChecked(true);
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
        contactSwitch = view.findViewById(R.id.notifications_switch_contact);
        connectionSwitch = view.findViewById(R.id.notifications_switch_connection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!(generalSwitch.isChecked())) {
            personalSettings.add(NotificationSettings.NEVER);
        } else {
            if (matchlistSwitch.isChecked()) {
                personalSettings.add(NotificationSettings.MATCHLIST);
            }

            if (contactSwitch.isChecked()) {
                personalSettings.add(NotificationSettings.REQUEST);
            }

            if (connectionSwitch.isChecked()) {
                personalSettings.add(NotificationSettings.CONNECTION);
            }
        }
        cacheNotificationSettings(personalSettings);
    }
}
