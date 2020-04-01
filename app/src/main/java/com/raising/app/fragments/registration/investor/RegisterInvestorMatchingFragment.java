package com.raising.app.fragments.registration.investor;

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
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Investor;
import com.raising.app.models.Model;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.ResourcesManager;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterInvestorMatchingFragment extends RaisingFragment
        implements View.OnClickListener {
    private Slider ticketSize;
    private Button geographicsButton;
    private CustomPicker customPicker;
    private TextView ticketSizeText;
    private LinearLayout industryLayout;
    private LinearLayout investmentPhaseLayout;
    private LinearLayout supportLayout;
    private RadioGroup investorTypeGroup;

    private View fragmentView;
    private long investorType = -1;
    public ArrayList<PickerItem> pickerItems;

    private int minimumTicketSize, maximumTicketSize;
    private int [] ticketSizeSteps;
    private String [] ticketSizeStrings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_matching,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        investorTypeGroup = view.findViewById(R.id.register_investor_matching_radio_investor);

        geographicsButton = view.findViewById(R.id.register_investor_matching_geographics_button);

        ticketSize = view.findViewById(R.id.register_investor_matching_ticket_size);

        Investor investor = RegistrationHandler.getInvestor();

        pickerItems = new ArrayList<>();
        pickerItems.addAll(ResourcesManager.getContinents());
        pickerItems.addAll(ResourcesManager.getCountries());

        ticketSizeStrings = ResourcesManager.getTicketSizeStrings(getString(R.string.currency),
                getResources().getStringArray(R.array.revenue_units));

        ticketSizeSteps = ResourcesManager.getTicketSizeValues();

        prepareTicketSizeSlider(view);

        if(investor.getTicketMinId() != 0 && investor.getTicketMaxId() != 0)
            ticketSize.setValues((float)investor.getTicketMinId(), (float)investor.getTicketMaxId());

        industryLayout = view.findViewById(R.id.register_investor_matching_industry_layout);
        investmentPhaseLayout = view.findViewById(R.id.register_investor_matching_phase_layout);
        supportLayout = view.findViewById(R.id.register_investor_matching_support_layout);

        investorTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    investorType = Integer.parseInt((String) checkedRadioButton.getContentDescription());
                }
            }
        });

        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .multiSelect(true)
                        .setItems(pickerItems);

        customPicker = builder.build();

        geographicsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customPicker.instanceRunning())
                    customPicker.dismiss();

                customPicker.showDialog(getActivity());
            }
        });

        setupLists();
        restoreLists();

        Button btnInvestorMatching = view.findViewById(R.id.button_investor_matching);
        btnInvestorMatching.setOnClickListener(this);
    }

    /**
     * Load all necessary items into list
     */
    private void setupLists() {
        setupCheckboxes(ResourcesManager.getInvestmentPhases(), investmentPhaseLayout);
        setupCheckboxes(ResourcesManager.getIndustries(), industryLayout);
        setupRadioGroup(ResourcesManager.getInvestorTypes(), investorTypeGroup);
        setupCheckboxes(ResourcesManager.getSupports(), supportLayout);
    }

    /**
     * Restore values of lists from previous entered data (saved in RegistrationHandler)
     */
    private void restoreLists() {
        Investor investor = RegistrationHandler.getInvestor();
        investorType = investor.getInvestorTypeId();
        investor.getInvestmentPhases().forEach(phase ->
                tickCheckbox(investmentPhaseLayout, phase.getId()));
        investor.getIndustries().forEach(industry ->
                tickCheckbox(industryLayout, industry.getId()));

        tickRadioButton(investorTypeGroup, investor.getInvestorTypeId());

        investor.getSupport().forEach(support ->
                tickCheckbox(supportLayout, support.getId()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_investor_matching:
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
        if(investorType == -1) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        int ticketSizeMinId =  (int)ResourcesManager.getTicketSizes().get(
                (int)ticketSize.getMinimumValue() - 1).getId();

        int ticketSizeMaxId =  (int)ResourcesManager.getTicketSizes().get(
                (int)ticketSize.getMaximumValue() - 1).getId();

        ArrayList<Long> industries = getSelectedCheckboxIds(industryLayout);
        ArrayList<Long> investmentPhases = getSelectedCheckboxIds(investmentPhaseLayout);
        ArrayList<Long> support = getSelectedCheckboxIds(supportLayout);

        ArrayList<Country> countries = new ArrayList<>();
        ArrayList<Continent> continents = new ArrayList<>();

        // only add child objects (countries) if parent object (continent) isn't selected
        // otherwise only add parent object
        pickerItems.forEach(item -> {
            if(item instanceof Continent && item.isChecked()) {
                continents.add((Continent)item);
                pickerItems.forEach(i -> {
                    if(i instanceof Country) {
                        i.setChecked(false);
                    }
                });
            }

            if(item instanceof Country && item.isChecked()) {
                countries.add((Country)item);
            }
        });

        if(industries.size() == 0 || investmentPhases.size() == 0 || support.size() == 0 ||
                (continents.size() == 0 && countries.size() == 0)) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        try {
            RegistrationHandler.saveInvestorMatchingFragment(ticketSizeMinId, ticketSizeMaxId,
                    investorType, investmentPhases, industries, support, countries, continents);
            changeFragment(new RegisterInvestorPitchFragment(),
                    "RegisterInvestorPitchFragment");
        } catch (IOException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Prepare the ticket size slider for optimal usage
     * @param view The view in which the slider lies
     */
    private void prepareTicketSizeSlider(View view) {
        ticketSizeText = view.findViewById(R.id.register_investor_matching_ticket_size_text);
        ticketSize = view.findViewById(R.id.register_investor_matching_ticket_size);
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