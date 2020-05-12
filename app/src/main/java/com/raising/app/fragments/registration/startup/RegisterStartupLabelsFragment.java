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
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.google.android.flexbox.FlexboxLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Startup;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaAdapter;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaComponent;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterStartupLabelsFragment extends RaisingFragment {
    private MatchingCriteriaComponent labelsLayout;

    private Startup startup;
    private boolean editMode = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_labels,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_labels), true);

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
            btnStartupLabels.setVisibility(View.INVISIBLE);
            startup = (Startup)currentAccount;
            hideBottomNavigation(false);
            editMode = true;
        } else {
            startup = RegistrationHandler.getStartup();
        }

        MatchingCriteriaAdapter.OnItemClickListener clickListener = position -> {
            if(editMode) {
                btnStartupLabels.setVisibility(View.VISIBLE);
            }
        };

        labelsLayout = new MatchingCriteriaComponent(view.findViewById(R.id.register_startup_pitch_labels), resources.getLabels(),
                false, clickListener, true);

        startup.getLabels().forEach(label -> labelsLayout.setChecked(label));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    private void processInformation() {
        ArrayList<Long> labels = labelsLayout.getSelected();

        if(labels.size() > 3) {
            showSimpleDialog(getString(R.string.register_label_error_title), getString(R.string.register_label_error_text));
            return;
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
