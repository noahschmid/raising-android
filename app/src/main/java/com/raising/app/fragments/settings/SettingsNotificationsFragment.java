package com.raising.app.fragments.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.PersonalSettings;

import java.lang.invoke.ConstantCallSite;

public class SettingsNotificationsFragment extends RaisingFragment {
    private SwitchMaterial generalSwitch, matchlistSwitch, contactSwitch, connectionSwitch;
    private ConstraintLayout specificSettings;
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

        //TODO: retrieve personal settings from internal storage

        specificSettings = view.findViewById(R.id.notifications_specific_settings);

        generalSwitch = view.findViewById(R.id.notifications_switch_general);
        generalSwitch.setChecked(true);
        generalSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
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

        // personalSettings.setGeneralNotifications(generalSwitch.isChecked());
        // personalSettings.setMatchlistNotifications(matchlistSwitch.isChecked());
        // personalSettings.setContactNotifications(contactSwitch.isChecked());
        // personalSettings.setConnectionNotifications(connectionSwitch.isChecked());

        //TODO: store personal settings in internal storage
    }
}
