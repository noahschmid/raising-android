package com.raising.app.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Startup;
import com.raising.app.util.RegistrationHandler;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterStartupLabelsFragment extends RaisingFragment {
    private LinearLayout labelsLayout;

    private Startup startup;
    private boolean editMode = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_labels,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnStartupLabels = view.findViewById(R.id.button_startup_labels);
        btnStartupLabels.setOnClickListener(v -> processInformation());

        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnStartupLabels.setHint(getString(R.string.myProfile_apply_changes));
            startup = (Startup)currentAccount;
            editMode = true;
        } else {
            startup = RegistrationHandler.getStartup();
        }

        labelsLayout = view.findViewById(R.id.register_startup_pitch_labels);

        resources.getLabels().forEach(label -> {
            CheckBox cb = new CheckBox(getContext());
            cb.setText(label.getName());
            cb.setContentDescription(String.valueOf(label.getId()));
            labelsLayout.addView(cb);
        });

        startup.getLabels().forEach(label -> tickCheckbox(labelsLayout, label));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    private void processInformation() {
        ArrayList<Long> labels = new ArrayList<>();
        for (int i = 0; i < labelsLayout.getChildCount(); ++i) {
            View v = labelsLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                labels.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        startup.setLabels(labels);

        try {
            if(!editMode) {
                RegistrationHandler.saveStartup(startup);
                changeFragment(new RegisterStartupImagesFragment());
            } else {
                accountViewModel.update(startup);
            }
        } catch(IOException e) {
            Log.e("RegisterStartupLabels", "Error while saving startup labels");
        }
    }

    @Override
    protected void onAccountUpdated() {
        popCurrentFragment(this);
        accountViewModel.updateCompleted();
    }
}
