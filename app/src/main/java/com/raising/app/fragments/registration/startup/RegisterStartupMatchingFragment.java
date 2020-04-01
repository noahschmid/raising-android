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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Model;
import com.raising.app.models.Startup;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.ResourcesManager;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterStartupMatchingFragment extends RaisingFragment
        implements View.OnClickListener {
    private Slider ticketSize;
    private LinearLayout investorTypeLayout, supportLayout, industryLayout;
    private RadioGroup investmentPhaseGroup;
    private TextView ticketSizeText;

    private int minimumTicketSize, maximumTicketSize;
    private String [] ticketSizeStrings;
    private int [] ticketSizeSteps;


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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ticketSizeSteps = ResourcesManager.getTicketSizeValues();
        ticketSizeStrings = ResourcesManager.getTicketSizeStrings(getString(R.string.currency),
                getResources().getStringArray(R.array.revenue_units));

        prepareTicketSizeSlider(view);

        investorTypeLayout = view.findViewById(R.id.register_startup_investor_type_layout);
        supportLayout = view.findViewById(R.id.register_support_matching_support_layout);
        investmentPhaseGroup = view.findViewById(R.id.register_startup_matching_radio_phase);
        industryLayout = view.findViewById(R.id.register_startup_matching_industry_layout);

        Startup startup = RegistrationHandler.getStartup();
        if(startup.getTicketMinId() != 0 && startup.getTicketMaxId() != 0)
            ticketSize.setValues((float)startup.getTicketMinId(), (float)startup.getTicketMaxId());

        setupLists();
        restoreLists();

        Button btnStartUpMatching = view.findViewById(R.id.button_startup_matching);
        btnStartUpMatching.setOnClickListener(this);
    }

    /**
     * Load all necessary items into list
     */
    private void setupLists() {
        setupRadioGroup(ResourcesManager.getInvestmentPhases(), investmentPhaseGroup);
        setupCheckboxes(ResourcesManager.getIndustries(), industryLayout);
        setupCheckboxes(ResourcesManager.getInvestorTypes(), investorTypeLayout);
        setupCheckboxes(ResourcesManager.getSupports(), supportLayout);
    }

    /**
     * Restore values of lists from previous entered data (saved in RegistrationHandler)
     */
    private void restoreLists() {
        Startup startup = RegistrationHandler.getStartup();

        startup.getInvestorTypes().forEach(type ->
                tickCheckbox(investorTypeLayout, type.getId()));

        startup.getIndustries().forEach(industry ->
                tickCheckbox(industryLayout, industry.getId()));

        tickRadioButton(investmentPhaseGroup, startup.getInvestmentPhaseId());

        startup.getSupport().forEach(support ->
                tickCheckbox(supportLayout, support.getId()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
        Log.d("debugMessage", "onDestroy()");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_startup_matching:
                processMatchingInformation();
                break;
            default:
                break;
        }
    }

    /**
     * Check if all information is valid and save it
     */
    private void processMatchingInformation() {
        ArrayList<Long> industries = getSelectedCheckboxIds(industryLayout);

        ArrayList<Long> investmentPhases = new ArrayList<>();
        investmentPhases.add(getSelectedRadioId(investmentPhaseGroup));

        ArrayList<Long> support = getSelectedCheckboxIds(supportLayout);
        ArrayList<Long> investorTypes = getSelectedCheckboxIds(investorTypeLayout);

        if(industries.size() == 0 || investmentPhases.size() == 0 || support.size() == 0 ||
        investorTypes.size() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        int ticketSizeMinId =  (int)ResourcesManager.getTicketSizes().get(
                (int)ticketSize.getMinimumValue() - 1).getId();

        int ticketSizeMaxId =  (int)ResourcesManager.getTicketSizes().get(
                (int)ticketSize.getMaximumValue() - 1).getId();

        try {
            RegistrationHandler.saveStartupMatchingFragment(ticketSizeMinId, ticketSizeMaxId,
                    investorTypes, investmentPhases, industries, support);

            changeFragment(new RegisterStartupPitchFragment(),
                    "RegisterStartupPitchFragment");
        } catch (IOException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Prepare the ticket size slider for optimal usage
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