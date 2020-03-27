package com.raising.app.authentication.fragments.registration.investor;

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
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.slider.Slider;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Investor;
import com.raising.app.models.Model;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.ResourcesManager;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RegisterInvestorMatchingFragment extends RaisingFragment
        implements View.OnClickListener {
    private Slider ticketSize;
    private Button geographicsButton;
    private CustomPicker customPicker;
    private LinearLayout industryLayout;
    private LinearLayout investmentPhaseLayout;
    private LinearLayout supportLayout;
    private RadioGroup investorTypeGroup;

    private View fragmentView;
    private long investorType = -1;
    public ArrayList<PickerItem> pickerItems;


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
        ticketSize.setValues(
                (float) getResources().getInteger(R.integer.ticket_size_slider_min_value),
                (float) getResources().getInteger(R.integer.ticket_size_slider_starting_value));

        Investor investor = RegistrationHandler.getInvestor();

        pickerItems = new ArrayList<>();
        pickerItems.addAll(ResourcesManager.getContinents());
        pickerItems.addAll(ResourcesManager.getCountries());


        if(investor.getInvestmentMin() != 0 && investor.getInvestmentMax() != 0)
            ticketSize.setValues((float)investor.getInvestmentMin(), (float)investor.getInvestmentMax());

        industryLayout = view.findViewById(R.id.register_investor_matching_industry_layout);
        investmentPhaseLayout = view.findViewById(R.id.register_investor_matching_phase_layout);
        supportLayout = view.findViewById(R.id.register_investor_matching_support_layout);

        investorTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if (isChecked) {
                    investorType = Integer.parseInt((String) checkedRadioButton.getContentDescription());
                } else {
                    investorType = -1;
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
     * Create checkbox group out of array list
     * @param list the items to add
     * @param layout where to add to
     */
    private void setupCheckboxes(ArrayList<? extends Model> list, LinearLayout layout) {
        list.forEach(item -> {
            CheckBox cb = new CheckBox(getContext());
            cb.setText(item.getName());
            cb.setContentDescription(String.valueOf(item.getId()));
            layout.addView(cb);
        });
    }

    /**
     * Add radio boxes to radio group out of array list
     * @param list the items to add
     * @param group where to add to
     */
    private void setupRadioGroup(ArrayList<? extends Model> list, RadioGroup group) {
        list.forEach(item -> {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(item.getName());
            rb.setContentDescription(String.valueOf(item.getId()));
            group.addView(rb);
        });
    }

    /**
     * Tick checkbox with given id
     * @param layout
     * @param id
     */
    private void tickCheckbox(LinearLayout layout, long id) {
        for (int i = 0; i < layout.getChildCount(); ++i) {
            CheckBox cb = (CheckBox) layout.getChildAt(i);
            if(Long.parseLong((String) cb.getContentDescription()) == id)
                cb.setChecked(true);
        }
    }

    /**
     * Tick radio button with given id
     * @param group
     * @param id
     */
    private void tickRadioButton(RadioGroup group, long id) {
        for (int i = 0; i < group.getChildCount(); ++i) {
            RadioButton rb = (RadioButton) group.getChildAt(i);
            if(Long.parseLong((String) rb.getContentDescription()) == id)
                rb.setChecked(true);
        }
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

        float ticketSizeMin =  ticketSize.getMinimumValue();
        float ticketSizeMax =  ticketSize.getMaximumValue();

        ArrayList<Long> industries = new ArrayList<>();
        for (int i = 0; i < industryLayout.getChildCount(); ++i) {
            View v = industryLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                industries.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        ArrayList<Long> investmentPhases = new ArrayList<>();
        for (int i = 0; i < investmentPhaseLayout.getChildCount(); ++i) {
            View v = investmentPhaseLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                investmentPhases.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        ArrayList<Long> support = new ArrayList<>();
        for (int i = 0; i < supportLayout.getChildCount(); ++i) {
            View v = supportLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                support.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        ArrayList<Country> countries = new ArrayList<>();
        ArrayList<Continent> continents = new ArrayList<>();

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
            RegistrationHandler.saveInvestorMatchingFragment(ticketSizeMin, ticketSizeMax,
                    investorType, investmentPhases, industries, support, countries, continents);
            changeFragment(new RegisterInvestorPitchFragment(),
                    "RegisterInvestorPitchFragment");
        } catch (IOException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }
}