package com.raising.app.fragments.registration.startup;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Revenue;
import com.raising.app.models.Startup;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.RaisingTextWatcher;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.viewModels.AccountViewModel;

import java.util.ArrayList;
import java.util.List;

public class RegisterCompanyFiguresFragment extends RaisingFragment implements RaisingTextWatcher {
    private final String TAG = "RegisterCompanyInformationFragment";
    private AutoCompleteTextView companyRevenueInput;
    private Button companyMarketsButton, btnCompanyFigures;
    private EditText companyFteInput, companyBreakevenInput, companyFoundingInput;

    private CustomPicker marketsPicker;
    private ArrayList<PickerItem> marketItems;
    private ArrayList<Long> selected = new ArrayList<>();

    private int revenueMinId = -1;
    private int revenueMaxId = -1;

    private Startup startup;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_company_figures,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_company_figures), true);

        accountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);

        //define input views and button
        btnCompanyFigures = view.findViewById(R.id.button_company_figures);
        btnCompanyFigures.setOnClickListener(v -> processInputs());

        companyFteInput = view.findViewById(R.id.register_input_company_fte);
        companyBreakevenInput = view.findViewById(R.id.register_input_company_breakeven);
        companyMarketsButton = view.findViewById(R.id.register_button_company_markets);
        companyFoundingInput = view.findViewById(R.id.register_input_company_founding_year);

        companyRevenueInput = view.findViewById(R.id.register_input_company_revenue);



        // check if this fragment is opened for registration or for profile
        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            // this fragment is opened via profile
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnCompanyFigures.setHint(getString(R.string.myProfile_apply_changes));
            btnCompanyFigures.setEnabled(false);
            editMode = true;
            startup = (Startup) accountViewModel.getAccount().getValue();
            hideBottomNavigation(false);
        } else {
            startup = RegistrationHandler.getStartup();
        }

        return view;
    }

    /**
     * If resources are ready, fill view with information
     */
    @Override
    public void onResourcesLoaded() {
        ArrayList<Revenue> revenues = resources.getRevenues();
        ArrayList<String> values = new ArrayList<>();
        revenues.forEach(rev -> values.add(rev.toString(getString(R.string.currency),
                getResources().getStringArray(R.array.revenue_units))));

        NoFilterArrayAdapter<String> adapterRevenue = new NoFilterArrayAdapter<String>(getContext(),
                R.layout.item_dropdown_menu, values);
        companyRevenueInput.setAdapter(adapterRevenue);

        companyRevenueInput.setOnItemClickListener((AdapterView.OnItemClickListener) (adapterView, view, i, l) -> {
            String itemName = (String) adapterRevenue.getItem(i);

            for (Revenue rev : revenues) {
                if (rev.toString(getString(R.string.currency),
                        getResources().getStringArray(R.array.revenue_units)).equals(itemName)) {
                    revenueMinId = rev.getRevenueMinId();
                    revenueMaxId = rev.getRevenueMaxId();
                    break;
                }
            }
        });
        prepareMarketsPicker();

        populateFragment();

        // if editmode, add text watchers after initial filling with users data
        if (editMode) {
            companyFteInput.addTextChangedListener(this);
            companyBreakevenInput.addTextChangedListener(this);
            companyFoundingInput.addTextChangedListener(this);
            companyRevenueInput.addTextChangedListener(this);
        }
    }

    @Override
    protected void onAccountUpdated() {
        resetTab();
        popFragment(this);
        accountViewModel.updateCompleted();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0)
            return;

        try {
            if (Integer.parseInt(s.toString()) == startup.getFoundingYear()
                    || Integer.parseInt(s.toString()) == startup.getBreakEvenYear())
                return;
        } catch (NumberFormatException e) {
            Log.e(TAG, "onTextChanged: " + e.getMessage());
            return;
        }
        btnCompanyFigures.setEnabled(true);
    }

    /**
     * Prepare the markets picker with resources and existing user data
     */
    private void prepareMarketsPicker() {
        marketItems = new ArrayList<>();
        marketItems.addAll(resources.getContinents());
        marketItems.addAll(resources.getCountries());

        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .multiSelect(true)
                        .setItems(marketItems);

        marketsPicker = builder.build();

        companyMarketsButton.setOnClickListener(v -> {
            if (marketsPicker.instanceRunning())
                marketsPicker.dismiss();

            marketsPicker.showDialog(getActivity(), dialog -> {
                checkIfMarketsChanged(marketsPicker.getResult());
            });
        });

        // restore selected markets
        if (startup.getContinents().size() > 0) {
            selected.addAll(startup.getContinents());
        }

        if (startup.getCountries().size() > 0) {
            selected.addAll(startup.getCountries());
        }

        if (selected.size() > 0) {
            marketsPicker.setSelectedById(selected);
        }
    }

    /**
     * Populate the fragment with existing user data
     */
    private void populateFragment() {
        if (startup.getRevenueMinId() > -1) {
            revenueMinId = startup.getRevenueMinId();
            revenueMaxId = startup.getRevenueMaxId();

            companyRevenueInput.setText(resources
                    .getRevenueString(startup.getRevenueMinId()), false);
        }

        if (startup.getNumberOfFte() > 0)
            companyFteInput.setText(Integer.toString(startup.getNumberOfFte()));
        if (startup.getFoundingYear() > 0)
            companyFoundingInput.setText(Integer.toString(startup.getFoundingYear()));
        if (startup.getBreakEvenYear() > 0)
            companyBreakevenInput.setText(Integer.toString(startup.getBreakEvenYear()));

        companyBreakevenInput.setOnClickListener(v -> {
            if (startup.getBreakEvenYear() > 0) {
                showYearPicker(getString(R.string.break_even_picker_title), companyBreakevenInput,
                        Integer.parseInt(companyBreakevenInput.getText().toString()));
            } else {
                showYearPicker(getString(R.string.break_even_picker_title), companyBreakevenInput);
            }
        });

        companyFoundingInput.setOnClickListener(v -> {
            if (startup.getFoundingYear() > 0) {
                showYearPicker(getString(R.string.founding_picker_title), companyFoundingInput,
                        Integer.parseInt(companyFoundingInput.getText().toString()));
            } else {
                showYearPicker(getString(R.string.founding_picker_title), companyFoundingInput);
            }
        });
    }

    /**
     * Checks if the user has changed his selection of markets
     *
     * @param list The users new selection of markets after dismissing the custom picker
     */
    private void checkIfMarketsChanged(List<PickerItem> list) {
        ArrayList<Long> listId = new ArrayList<>();
        list.forEach(pickerItem -> {
            listId.add(pickerItem.getId());
        });

        if (!listId.equals(selected)) {
            btnCompanyFigures.setEnabled(true);
        }
    }

    /**
     * Check the validity of user inputs, then handle the inputs
     */
    private void processInputs() {
        if (companyBreakevenInput.getText().length() == 0 ||
                companyFteInput.getText().length() == 0 ||
                companyRevenueInput.getText().length() == 0 ||
                companyFoundingInput.getText().length() == 0 ||
                revenueMinId == -1 || revenueMaxId == -1) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        ArrayList<Long> countries = new ArrayList<>();
        ArrayList<Long> continents = new ArrayList<>();

        marketsPicker.getResult().forEach(item -> {
            if (item instanceof Continent) {
                continents.add(((Continent) item).getId());
                marketItems.forEach(i -> {
                    if (i instanceof Country) {
                        i.setChecked(false);
                    }
                });
            }

            if (item instanceof Country) {
                countries.add(((Country) item).getId());
            }
        });

        // check if FTE-input is valid
        try {
            if (Integer.parseInt(companyFteInput.getText().toString()) < 1) {
                showSimpleDialog(getString(R.string.register_dialog_title),
                        getString(R.string.register_company_error_fte));
                return;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "processInformation: " + e.getMessage());
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_company_error_large_fte));
            return;
        }

        // check if break-even year input is valid
        if (Integer.parseInt(companyBreakevenInput.getText().toString())
                < Integer.parseInt(companyFoundingInput.getText().toString())) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_company_error_break_even));
            return;
        }

        // check for countries and continents
        if (countries.isEmpty() && continents.isEmpty() && startup.getCountries().isEmpty() &&
                startup.getContinents().isEmpty()) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        if (!countries.isEmpty() || !continents.isEmpty()) {
            startup.setCountries(countries);
            startup.setContinents(continents);
        }

        try {
            startup.setBreakEvenYear(Integer.parseInt(companyBreakevenInput.getText().toString()));
            startup.setFoundingYear(Integer.parseInt(companyFoundingInput.getText().toString()));
            startup.setNumberOfFte(Integer.parseInt(companyFteInput.getText().toString()));
        } catch (NumberFormatException e) {
            Log.e(TAG, "processInformation: " + e.getMessage());
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_company_error_number_format));
            return;
        }

        startup.setRevenueMinId(revenueMinId);
        startup.setRevenueMaxId(revenueMaxId);

        try {
            if (!editMode) {
                RegistrationHandler.saveStartup(startup);
                changeFragment(new RegisterStartupMatchingFragment(),
                        "RegisterStartupMatchingFragment");
            } else {
                accountViewModel.update(startup);
            }

        } catch (Exception e) {
            Log.d("RegisterCompanyFiguresFragment", "Error while processing inputs: "
                    + e.getMessage());
        }
    }
}
