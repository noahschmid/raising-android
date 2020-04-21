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
import android.widget.Switch;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.PersonalSettings;

import java.lang.invoke.ConstantCallSite;

public class SettingsNotificationsFragment extends RaisingFragment implements View.OnClickListener {
    private Switch generalSwitch, matchlistSwitch, contactSwitch, connectionSwitch;
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
        generalSwitch.setOnClickListener(this);
        generalSwitch.setChecked(true);
        matchlistSwitch = view.findViewById(R.id.notifications_switch_matchlist);
        matchlistSwitch.setOnClickListener(this);
        contactSwitch = view.findViewById(R.id.notifications_switch_contact);
        contactSwitch.setOnClickListener(this);
        connectionSwitch = view.findViewById(R.id.notifications_switch_connection);
        connectionSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notifications_switch_general:
                generalSwitch.toggle();
                personalSettings.setGeneralNotifications(!personalSettings.isGeneralNotifications());
                if(personalSettings.isGeneralNotifications()) {
                    specificSettings.setVisibility(View.VISIBLE);
                } else {
                    specificSettings.setVisibility(View.GONE);
                }
                break;
            case R.id.notifications_switch_matchlist:
                matchlistSwitch.toggle();
                personalSettings.setMatchlistNotifications(!personalSettings.isMatchlistNotifications());
                break;
            case R.id.notifications_switch_contact:
                contactSwitch.toggle();
                personalSettings.setContactNotifications(!personalSettings.isContactNotifications());
                break;
            case R.id.notifications_switch_connection:
                connectionSwitch.toggle();
                personalSettings.setConnectionNotifications(!personalSettings.isConnectionNotifications());
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //TODO: store personal settings in internal storage
    }
}
