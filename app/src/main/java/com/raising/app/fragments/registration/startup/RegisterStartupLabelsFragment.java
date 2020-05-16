package com.raising.app.fragments.registration.startup;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Startup;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaAdapter;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaComponent;
import com.raising.app.viewModels.AccountViewModel;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterStartupLabelsFragment extends RaisingFragment {
    private final String TAG = "RegisterStartupLabelsFragment";
    private MatchingCriteriaComponent labelsLayout;

    private Startup startup;
    private boolean editMode = false;

    Button btnStartupLabels;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_labels,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_labels), true);

        btnStartupLabels = view.findViewById(R.id.button_startup_labels);
        btnStartupLabels.setOnClickListener(v -> processInputs());

        accountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);

        // check if this fragment is opened for registration or for profile
        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            // this fragment is opened via profile
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnStartupLabels.setHint(getString(R.string.myProfile_apply_changes));
            btnStartupLabels.setEnabled(false);
            startup = (Startup) accountViewModel.getAccount().getValue();
            hideBottomNavigation(false);
            editMode = true;
        } else {
            startup = RegistrationHandler.getStartup();
        }

        return view;
    }

    @Override
    public void onResourcesLoaded() {
        MatchingCriteriaAdapter.OnItemClickListener clickListener = position -> {
            if(editMode) {
                btnStartupLabels.setEnabled(true);
            }
        };

        labelsLayout = new MatchingCriteriaComponent(getView().findViewById(R.id.register_startup_pitch_labels),
                resources.getLabels(), false, clickListener, true);

        if(startup != null && startup.getLabels() != null) {
            Log.d(TAG, "onResourcesLoaded: " + startup.getLabels());
            startup.getLabels().forEach(label -> labelsLayout.setChecked(label));
        }
    }

    @Override
    public void onDestroyView() {
        hideBottomNavigation(false);
        super.onDestroyView();
    }

    @Override
    protected void onAccountUpdated() {
        resetTab();
        popFragment(this);
        accountViewModel.updateCompleted();
    }

    /**
     * Check the validity of user inputs, then handle the inputs
     */
    private void processInputs() {
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
}

