package com.raising.app.fragments.registration.startup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.ContactDetails;
import com.raising.app.models.Country;
import com.raising.app.models.Startup;
import com.raising.app.util.AccountService;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterCompanyInformationFragment extends RaisingFragment {
    private EditText companyNameInput, companyUidInput, companyWebsiteInput, companyPhoneInput, companyCountryInput;
    private CustomPicker countryPicker;
    public ArrayList<PickerItem> countryItems;
    private Country countrySelected = null;
    private Startup startup;
    private ContactDetails contactDetails;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_company_information, container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout companyUidLayout = view.findViewById(R.id.register_company_uid);
        companyUidLayout.setEndIconOnClickListener(v -> Snackbar.make(companyUidLayout,
                R.string.register_uid_helper_text, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.register_uid_snackbar_find), v1 -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.register_uid_link)));
                    startActivity(browserIntent);
                }).setDuration(getResources().getInteger(R.integer.raisingLongSnackbar))
                .show());



        companyCountryInput = view.findViewById(R.id.register_input_company_countries);
        companyPhoneInput = view.findViewById(R.id.register_input_company_phone);
        companyWebsiteInput = view.findViewById(R.id.register_input_company_website);
        companyNameInput = view.findViewById(R.id.register_input_company_name);
        companyUidInput = view.findViewById(R.id.register_input_company_uid);

        Button btnCompanyInformation = view.findViewById(R.id.button_company_information);
        btnCompanyInformation.setOnClickListener(v -> processInformation());

        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnCompanyInformation.setHint(getString(R.string.myProfile_apply_changes));
            editMode = true;
            startup = (Startup)accountViewModel.getAccount().getValue();
            contactDetails = AccountService.getContactDetails();
        } else {
            startup = RegistrationHandler.getStartup();
            contactDetails = RegistrationHandler.getContactDetails();
        }

        companyUidInput.setText(startup.getUId());
        companyNameInput.setText(startup.getCompanyName());

        if(resources.getCountry(startup.getCountryId()) != null)
            companyCountryInput.setText(resources.getCountry(startup.getCountryId()).getName());
        companyUidInput.setText(startup.getUId());
        companyWebsiteInput.setText(startup.getWebsite());
        companyPhoneInput.setText(contactDetails.getPhone());
        countrySelected = resources.getCountry(startup.getCountryId());

        // Country picker
        countryItems = new ArrayList<>();
        resources.getCountries().forEach(country -> countryItems.add(new Country(country)));
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
                        .setItems(resources.getCountries());

        countryPicker = pickerBuilder.build();

        companyCountryInput.setOnClickListener(v -> {
            if(countryPicker.instanceRunning())
                countryPicker.dismiss();

            countryPicker.showDialog(getActivity());
        });
    }

    @Override
    protected void onAccountUpdated() {
        if(!editMode)
            return;

        AccountService.saveContactDetails(contactDetails);
        popCurrentFragment(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    /**
     * Process entered information
     */
    private void processInformation() {
        if(companyNameInput.getText().length() == 0 ||
                companyUidInput.getText().length() == 0 ||
                countrySelected == null ) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        startup.setCompanyName(companyNameInput.getText().toString());
        startup.setUId(companyUidInput.getText().toString());

        contactDetails.setPhone(companyPhoneInput.getText().toString());
        startup.setWebsite(companyWebsiteInput.getText().toString());
        startup.setCountryId(countrySelected.getId());

        try {
            if(!editMode) {
                RegistrationHandler.saveStartup(startup);
                RegistrationHandler.saveContactDetails(contactDetails);
                changeFragment(new RegisterCompanyFiguresFragment(),
                        "RegisterCompanyFiguresFragment");
            } else {
                accountViewModel.update(startup);
            }

        } catch (IOException e) {
            Log.d("RegisterCompanyInformationFragment", "Error while processing inputs: "
                    + e.getMessage());
        }
    }
}
