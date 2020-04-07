package com.raising.app.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.raising.app.models.ContactDetails;
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Revenue;
import com.raising.app.models.Startup;
import com.raising.app.util.AccountService;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.ResourcesManager;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterCompanyInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText companyNameInput, companyUidInput, companyFteInput, companyBreakevenInput,
            companyFoundingInput, companyWebsiteInput, companyPhoneInput, companyCountryInput;
    private AutoCompleteTextView companyRevenueInput;
    private Button companyMarketsButton;
    private CustomPicker marketsPicker, countryPicker;
    public ArrayList<PickerItem> marketItems, countryItems;
    private int revenueMinId = -1;
    private int revenueMaxId = -1;
    private Country countrySelected = null;
    private Startup startup;
    private ContactDetails contactDetails;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_company_information, container, false);

        ArrayList<Revenue> revenues = ResourcesManager.getRevenues();
        ArrayList<String> values = new ArrayList<>();
        revenues.forEach(rev -> values.add(rev.toString(getString(R.string.currency),
                getResources().getStringArray(R.array.revenue_units))));

        NoFilterArrayAdapter<String> adapterRevenue = new NoFilterArrayAdapter<String>( getContext(),
                R.layout.item_dropdown_menu, values);

        companyRevenueInput = view.findViewById(R.id.register_input_company_revenue);
        companyRevenueInput.setAdapter(adapterRevenue);

        companyRevenueInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemName = (String)adapterRevenue.getItem(i);

                for (Revenue rev : revenues) {
                    if (rev.toString(getString(R.string.currency),
                            getResources().getStringArray(R.array.revenue_units)).equals(itemName)) {
                        revenueMinId = rev.getRevenueMinId();
                        revenueMaxId = rev.getRevenueMaxId();
                        break;
                    }
                }
            }
        });


        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        companyBreakevenInput = view.findViewById(R.id.register_input_company_breakeven);
        companyMarketsButton = view.findViewById(R.id.register_button_company_markets);
        companyFoundingInput = view.findViewById(R.id.register_input_company_founding_year);
        companyCountryInput = view.findViewById(R.id.register_input_company_countries);
        companyPhoneInput = view.findViewById(R.id.register_input_company_phone);
        companyWebsiteInput = view.findViewById(R.id.register_input_company_website);
        companyNameInput = view.findViewById(R.id.register_input_company_name);
        companyUidInput = view.findViewById(R.id.register_input_company_uid);
        companyFteInput = view.findViewById(R.id.register_input_company_fte);

        Button btnCompanyInformation = view.findViewById(R.id.button_company_information);
        btnCompanyInformation.setOnClickListener(this);

        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.GONE);
            btnCompanyInformation.setHint(getString(R.string.myProfile_apply_changes));
            editMode = true;
            startup = (Startup)AccountService.getAccount();
            contactDetails = AccountService.getContactDetails();
        } else {
            startup = RegistrationHandler.getStartup();
            contactDetails = RegistrationHandler.getContactDetails();
        }

        if(startup.getRevenueMinId() > -1) {
            revenueMinId = startup.getRevenueMinId();
            revenueMaxId = startup.getRevenueMaxId();

            companyRevenueInput.setText(ResourcesManager
                    .getRevenueString(startup.getRevenueMinId()), false);
        }

        companyBreakevenInput.setShowSoftInputOnFocus(false);
        companyFoundingInput.setShowSoftInputOnFocus(false);
        companyRevenueInput.setShowSoftInputOnFocus(false);
        companyCountryInput.setShowSoftInputOnFocus(false);
        companyUidInput.setText(startup.getUId());

        if(startup.getNumberOfFte() > 0)
            companyFteInput.setText(Integer.toString(startup.getNumberOfFte()));
        if(startup.getFoundingYear() > 0)
            companyFoundingInput.setText(Integer.toString(startup.getFoundingYear()));
        if(startup.getBreakEvenYear() > 0)
            companyBreakevenInput.setText(Integer.toString(startup.getBreakEvenYear()));

        companyNameInput.setText(startup.getName());

        if(ResourcesManager.getCountry(startup.getCountryId()) != null)
            companyCountryInput.setText(ResourcesManager.getCountry(startup.getCountryId()).getName());
        companyUidInput.setText(startup.getUId());
        companyWebsiteInput.setText(startup.getWebsite());
        companyPhoneInput.setText(contactDetails.getPhone());
        countrySelected = ResourcesManager.getCountry(startup.getCountryId());

        companyBreakevenInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYearPicker("Select breakeven year", companyBreakevenInput);
            }
        });

        companyBreakevenInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showYearPicker("Select breakeven year", companyBreakevenInput);
                }
            }
        });

        companyFoundingInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYearPicker("Select founding year", companyFoundingInput);
            }
        });

        companyFoundingInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showYearPicker("Select founding year", companyFoundingInput);
                }
            }
        });

        // Markets picker
        marketItems = new ArrayList<>();
        marketItems.addAll(ResourcesManager.getContinents());
        marketItems.addAll(ResourcesManager.getCountries());

        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .multiSelect(true)
                        .setItems(marketItems);

        marketsPicker = builder.build();

        companyMarketsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(marketsPicker.instanceRunning())
                    marketsPicker.dismiss();

                marketsPicker.showDialog(getActivity());
            }
        });

        // Country picker
        countryItems = new ArrayList<>();
        ResourcesManager.getCountries().forEach(country -> countryItems.add(new Country(country)));
        CustomPicker.Builder pickerBuilder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .listener(new OnCustomPickerListener() {
                            @Override
                            public void onSelectItem(PickerItem country) {
                                companyCountryInput.setText(country.getName());
                                countrySelected = (Country)country;
                            }
                        })
                        .setItems(ResourcesManager.getCountries());

        countryPicker = pickerBuilder.build();

        companyCountryInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(!countryPicker.instanceRunning())
                        countryPicker.showDialog(getActivity());
                }
            }
        });

        companyCountryInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countryPicker.instanceRunning())
                    countryPicker.dismiss();

                countryPicker.showDialog(getActivity());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_company_information:
                processInformation();
                break;
            default:
                break;
        }
    }

    /**
     * Process entered information
     */
    private void processInformation() {
        if(companyBreakevenInput.getText().length() == 0 ||
                companyFteInput.getText().length() == 0 ||
                companyNameInput.getText().length() == 0 ||
                companyUidInput.getText().length() == 0 ||
                companyRevenueInput.getText().length() == 0 ||
                companyFoundingInput.getText().length() == 0 ||
                countrySelected == null ||
                companyWebsiteInput.getText().length() == 0 ||
                companyPhoneInput.getText().length() == 0 ||
                revenueMinId == -1 || revenueMaxId == -1) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        ArrayList<Country> countries = new ArrayList<>();
        ArrayList<Continent> continents = new ArrayList<>();

        Calendar today = Calendar.getInstance();

        marketItems.forEach(item -> {
            if(item instanceof Continent && item.isChecked()) {
                continents.add((Continent)item);
                marketItems.forEach(i -> {
                    if(i instanceof Country) {
                        i.setChecked(false);
                    }
                });
            }

            if(item instanceof Country && item.isChecked()) {
                countries.add((Country)item);
            }
        });

        if(countries.isEmpty() && continents.isEmpty()) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        startup.setBreakEvenYear(Integer.parseInt(
                companyBreakevenInput.getText().toString()));
        startup.setNumberOfFte(Integer.parseInt(companyFteInput.getText().toString()));
        startup.setCompany(companyNameInput.getText().toString());
        startup.setUId(companyUidInput.getText().toString());
        startup.setRevenueMinId(revenueMinId);
        startup.setRevenueMaxId(revenueMaxId);

        contactDetails.setPhone(companyPhoneInput.getText().toString());
        startup.setWebsite(companyWebsiteInput.getText().toString());
        startup.setFoundingYear(Integer.parseInt(companyFoundingInput.getText().toString()));
        startup.setCountryId(countrySelected.getId());

        try {

            if(!editMode) {
                RegistrationHandler.saveStartup(startup);
                RegistrationHandler.saveContactDetails(contactDetails);
                changeFragment(new RegisterStartupMatchingFragment(),
                        "RegisterStartupMatchingFragment");
            } else {
                AccountService.saveContactDetails(contactDetails);
                AccountService.updateAccount(startup, v -> {
                    popCurrentFragment(this);
                    return null;
                });
            }

        } catch (IOException e) {
            Log.d("RegisterCompanyInformationFragment", "Error while processing inputs: "
                    + e.getMessage());
        }
    }
}
