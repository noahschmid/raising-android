package com.raising.app.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Startup;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaAdapter;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaComponent;
import com.raising.app.viewModels.AccountViewModel;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterStartupMatchingFragment extends RaisingFragment {
    private Slider ticketSize;
    private Button btnStartUpMatching;
    private MatchingCriteriaComponent investorTypeCriteria, supportCriteria, industryCriteria,
            investmentPhaseCriteria;
    private TextView ticketSizeText;

    private int minimumTicketSize, maximumTicketSize;
    private String[] ticketSizeStrings;
    private int[] ticketSizeSteps;
    private Startup startup;
    private boolean editMode = false;

    private AccountViewModel accountViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_matching,
                container, false);

        ticketSize = view.findViewById(R.id.register_startup_matching_ticket_size);
        ticketSize.setValues(
                (float) getResources().getInteger(R.integer.ticket_size_slider_min_value),
                (float) getResources().getInteger(R.integer.ticket_size_slider_starting_value));

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_matching_criteria), true);

        accountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);

        btnStartUpMatching = view.findViewById(R.id.button_startup_matching);
        btnStartUpMatching.setOnClickListener(v -> processInputs());

        // check if this fragment is opened for registration or for profile
        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            // this fragment is opened via profile
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnStartUpMatching.setHint(getString(R.string.myProfile_apply_changes));
            editMode = true;
            startup = (Startup) accountViewModel.getAccount().getValue();
            hideBottomNavigation(false);
        } else {
            startup = RegistrationHandler.getStartup();
        }
        return view;
    }

    @Override
    public void onResourcesLoaded() {
        View view = getView();
        // prepare fragment for usage
        ticketSizeSteps = resources.getTicketSizeValues();
        ticketSizeStrings = resources.getTicketSizeStrings(getString(R.string.currency),
                getResources().getStringArray(R.array.revenue_units));

        prepareTicketSizeSlider(view);

        MatchingCriteriaAdapter.OnItemClickListener clickListener = position -> {
            if (editMode) {
                btnStartUpMatching.setEnabled(true);
            }
        };

        investorTypeCriteria = new MatchingCriteriaComponent(view.findViewById(R.id.register_startup_investor_type_layout),
                resources.getInvestorTypes(), false, clickListener);

        supportCriteria = new MatchingCriteriaComponent(view.findViewById(R.id.register_support_matching_support_layout),
                resources.getSupports(), false, clickListener);

        investmentPhaseCriteria = new MatchingCriteriaComponent(view.findViewById(R.id.register_startup_matching_radio_phase),
                resources.getInvestmentPhases(), true, clickListener);

        industryCriteria = new MatchingCriteriaComponent(view.findViewById(R.id.register_startup_matching_industry_layout),
                resources.getIndustries(), false, clickListener);

        if (editMode) {
            btnStartUpMatching.setEnabled(false);
        }
        populateFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    protected void onAccountUpdated() {
        resetTab();
        popFragment(this);
        accountViewModel.updateCompleted();
    }

    /**
     * Restore values of lists from previous entered data (saved in RegistrationHandler)
     */
    private void populateFragment() {
        if (startup.getTicketMinId() != 0 && startup.getTicketMaxId() != 0)
            ticketSize.setValues((float) startup.getTicketMinId(), (float) startup.getTicketMaxId());

        startup.getInvestorTypes().forEach(type -> investorTypeCriteria.setChecked(type));

        startup.getIndustries().forEach(industry -> industryCriteria.setChecked(industry));

        investmentPhaseCriteria.setChecked(startup.getInvestmentPhaseId());

        startup.getSupport().forEach(support -> supportCriteria.setChecked(support));
    }

    /**
     * Check the validity of user inputs, then handle the inputs
     */
    private void processInputs() {
        ArrayList<Long> industries = industryCriteria.getSelected();

        long investmentPhaseId = investmentPhaseCriteria.getSingleSelected();

        ArrayList<Long> support = supportCriteria.getSelected();
        ArrayList<Long> investorTypes = investorTypeCriteria.getSelected();

        if (industries.size() == 0 || investmentPhaseId == -1 || support.size() == 0 ||
                investorTypes.size() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        int ticketSizeMinId = (int) resources.getTicketSizes().get(
                (int) ticketSize.getMinimumValue() - 1).getId();

        int ticketSizeMaxId = (int) resources.getTicketSizes().get(
                (int) ticketSize.getMaximumValue() - 1).getId();

        startup.setTicketMaxId(ticketSizeMaxId);
        startup.setTicketMinId(ticketSizeMinId);
        startup.setInvestorTypes(investorTypes);
        startup.setIndustries(industries);
        startup.setSupport(support);
        startup.setInvestmentPhaseId(investmentPhaseId);

        try {
            if (!editMode) {
                RegistrationHandler.saveStartup(startup);
                changeFragment(new RegisterStartupPitchFragment(),
                        "RegisterStartupPitchFragment");
            } else {
                accountViewModel.update(startup);
            }
        } catch (IOException e) {
            Log.e("RegisterStartupMatching",
                    "Error in processInputs: " + e.getMessage());
        }
    }

    /**
     * Prepare the ticket size slider for optimal usage
     *
     * @param view The view in which the slider lies
     */
    private void prepareTicketSizeSlider(View view) {
        ticketSizeText = view.findViewById(R.id.register_startup_matching_ticket_size_text);
        ticketSize = view.findViewById(R.id.register_startup_matching_ticket_size);
        ticketSize.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ticketSizeText.setText(adaptSliderValues(
                        (int) slider.getMaximumValue(), (int) slider.getMinimumValue()));
                btnStartUpMatching.setEnabled(true);
            }
        });

        ticketSize.setValueTo(ticketSizeSteps.length);
        ticketSize.setValues(
                (float) getResources().getInteger(R.integer.ticket_size_slider_min_value),
                (float) getResources().getInteger(R.integer.ticket_size_slider_starting_value));
        ticketSizeText.setText(adaptSliderValues(
                (int) ticketSize.getMaximumValue(), (int) ticketSize.getMinimumValue()));
    }

    /**
     * Create the string representation of the currently selected slider values
     *
     * @param maxValue The currently selected maximal value
     * @param minValue The currently selected minimal value
     * @return String representing the current slider selection
     */
    private String adaptSliderValues(int maxValue, int minValue) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(ticketSizeStrings[minValue - 1]);
        minimumTicketSize = ticketSizeSteps[minValue - 1];

        stringBuilder.append(" - ");
        stringBuilder.append(ticketSizeStrings[maxValue - 1]);
        maximumTicketSize = ticketSizeSteps[maxValue - 1];

        return stringBuilder.toString();
    }
}
