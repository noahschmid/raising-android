package com.raising.app.fragments.registration.investor;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintHelper;

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

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.slider.Slider;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Investor;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaAdapter;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaComponent;

import java.util.ArrayList;
import java.util.List;

public class RegisterInvestorMatchingFragment extends RaisingFragment {
    private final String TAG = "RegisterInvestorMatchingFragment";
    private Slider ticketSize;
    private Button geographicsButton, btnInvestorMatching;
    private CustomPicker customPicker;
    private TextView ticketSizeText;
    private MatchingCriteriaComponent industryCriteria, investmentPhaseCriteria, supportCriteria,
        investorTypeCriteria;

    private View fragmentView;
    private long investorType = -1;
    private ArrayList<PickerItem> pickerItems;
    private ArrayList<Long> selected = new ArrayList<>();
    private Investor investor;

    private int minimumTicketSize, maximumTicketSize;
    private int [] ticketSizeSteps;
    private String [] ticketSizeStrings;

    private boolean editMode = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_matching,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_matching_criteria), true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        geographicsButton = view.findViewById(R.id.register_investor_matching_geographics_button);

        ticketSize = view.findViewById(R.id.register_investor_matching_ticket_size);

        btnInvestorMatching = view.findViewById(R.id.button_investor_matching);
        btnInvestorMatching.setOnClickListener(v -> processMatchingInformation());

        investor = null;

        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnInvestorMatching.setHint(getString(R.string.myProfile_apply_changes));
            investor = (Investor) accountViewModel.getAccount().getValue();
            editMode = true;
            hideBottomNavigation(false);
        } else {
            investor = RegistrationHandler.getInvestor();
        }

        pickerItems = new ArrayList<>();
        pickerItems.addAll(resources.getContinents());
        pickerItems.addAll(resources.getCountries());

        ticketSizeStrings = resources.getTicketSizeStrings(getString(R.string.currency),
                getResources().getStringArray(R.array.revenue_units));

        ticketSizeSteps = resources.getTicketSizeValues();

        prepareTicketSizeSlider(view);

        if(investor.getTicketMinId() != 0 && investor.getTicketMaxId() != 0)
            ticketSize.setValues((float)investor.getTicketMinId(), (float)investor.getTicketMaxId());
        if(editMode) {
            btnInvestorMatching.setVisibility(View.INVISIBLE);
        }

        MatchingCriteriaAdapter.OnItemClickListener clickListener = new MatchingCriteriaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(editMode) {
                    btnInvestorMatching.setVisibility(View.VISIBLE);
                }
            }
        };

        industryCriteria = new MatchingCriteriaComponent(view.findViewById(R.id.register_investor_matching_industry_layout),
                resources.getIndustries(), false, clickListener);

        investmentPhaseCriteria = new MatchingCriteriaComponent(view.findViewById(R.id.register_investor_matching_phase_layout),
                resources.getInvestmentPhases(), false, clickListener);

        supportCriteria = new MatchingCriteriaComponent(view.findViewById(R.id.register_investor_matching_support_layout),
                resources.getSupports(), false, clickListener);

        investorTypeCriteria = new MatchingCriteriaComponent(view.findViewById(R.id.register_investor_matching_radio_investor),
                resources.getInvestorTypes(),true, clickListener);

        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .multiSelect(true)
                        .setItems(pickerItems);

        customPicker = builder.build();

        geographicsButton.setOnClickListener(v -> {
            if(customPicker.instanceRunning())
                customPicker.dismiss();

            customPicker.showDialog(getActivity(), dialog -> {
                Log.d(TAG, "onDismiss: ");
                checkIfMarketsChanged(customPicker.getResult());
            });
        });

        if(investor.getContinents().size() > 0) {
            selected.addAll(investor.getContinents());
        }

        if(investor.getCountries().size() > 0) {
            selected.addAll(investor.getCountries());
        }

        if(selected.size() > 0) {
            customPicker.setSelectedById(selected);
        }

        restoreLists();
    }

    /**
     * Restore values of lists from previous entered data
     */
    private void restoreLists() {
        investorType = investor.getInvestorTypeId();
        investor.getInvestmentPhases().forEach(phase -> investmentPhaseCriteria.setChecked(phase));
        investor.getIndustries().forEach(industry -> industryCriteria.setChecked(industry));
        investorTypeCriteria.setChecked(investor.getInvestorTypeId());
        investor.getSupport().forEach(support -> supportCriteria.setChecked(support));
    }

    /**
     * Checks if the user has changed his selection of markets
     * @param list The users new selection of markets after dismissing the custom picker
     */
    private void checkIfMarketsChanged(List<PickerItem> list) {
        ArrayList<Long> listId = new ArrayList<>();
        list.forEach(pickerItem -> {
            listId.add(pickerItem.getId());
        });

        Log.d(TAG, "checkIfMarketsChanged: listId " + listId.toString());
        Log.d(TAG, "checkIfMarketsChanged: selected " + selected.toString());
        if(!listId.equals(selected)) {
            btnInvestorMatching.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    protected void onAccountUpdated() {
        popCurrentFragment(this);
        accountViewModel.updateCompleted();
    }

    /**
     * Check if all information is valid and save it
     */
    private void processMatchingInformation() {
        investorType = investorTypeCriteria.getSingleSelected();
        if(investorType == -1) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        investor.setTicketMinId((int)resources.getTicketSizes().get(
                (int)ticketSize.getMinimumValue() - 1).getId());

        investor.setTicketMaxId((int)resources.getTicketSizes().get(
                (int)ticketSize.getMaximumValue() - 1).getId());

        ArrayList<Long> industries = industryCriteria.getSelected();
        ArrayList<Long> investmentPhases = investmentPhaseCriteria.getSelected();
        ArrayList<Long> support = supportCriteria.getSelected();

        ArrayList<Long> countries = new ArrayList<>();
        ArrayList<Long> continents = new ArrayList<>();

        // only add child objects (countries) if parent object (continent) isn't selected
        // otherwise only add parent object
        customPicker.getResult().forEach(item -> {
            if(item instanceof Continent) {
                continents.add(item.getId());
                pickerItems.forEach(i -> {
                    if(i instanceof Country) {
                        i.setChecked(false);
                    }
                });
            }

            if(item instanceof Country) {
                countries.add(item.getId());
            }
        });

        if(industries.size() == 0 || investmentPhases.size() == 0 || support.size() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        // check for countries and continents
        if (countries.isEmpty() && continents.isEmpty() && investor.getCountries().isEmpty() &&
                investor.getContinents().isEmpty()) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        investor.setInvestmentPhases(investmentPhases);
        investor.setIndustries(industries);
        investor.setSupport(support);
        investor.setInvestorTypeId(investorType);

        if(!continents.isEmpty() || !countries.isEmpty()) {
            investor.setContinents(continents);
            investor.setCountries(countries);
        }

        try {
            if(!editMode) {
                RegistrationHandler.saveInvestor(investor);
                changeFragment(new RegisterInvestorPitchFragment(),
                        "RegisterInvestorPitchFragment");
            } else {
               accountViewModel.update(investor);
            }
        } catch (Exception e) {
            Log.d("RegisterInvestorMatchingFragment",
                    "Error while saving data: " + e.getMessage());
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
                btnInvestorMatching.setVisibility(View.VISIBLE);
            }
        });
        ticketSize.setValueFrom((float) 1);
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