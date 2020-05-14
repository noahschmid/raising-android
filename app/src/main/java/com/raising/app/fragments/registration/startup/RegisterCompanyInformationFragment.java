package com.raising.app.fragments.registration.startup;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.ContactData;
import com.raising.app.models.Country;
import com.raising.app.models.Startup;
import com.raising.app.util.AccountService;
import com.raising.app.util.RaisingTextWatcher;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.viewModels.AccountViewModel;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterCompanyInformationFragment extends RaisingFragment implements RaisingTextWatcher {
    private final String TAG = "RegisterCompanyInformationFragment";
    private EditText companyNameInput, companyUidInput, companyWebsiteInput, companyPhoneInput, companyCountryInput;
    private Button btnCompanyInformation;
    private CustomPicker countryPicker;
    private ArrayList<PickerItem> countryItems;
    private Country countrySelected = null;
    private Startup startup;
    private ContactData contactDetails;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_company_information, container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_company_information), true);

        btnCompanyInformation = view.findViewById(R.id.button_company_information);
        btnCompanyInformation.setOnClickListener(v -> processInformation());

        //define input views and button
        companyCountryInput = view.findViewById(R.id.register_input_company_countries);
        companyPhoneInput = view.findViewById(R.id.register_input_company_phone);
        companyWebsiteInput = view.findViewById(R.id.register_input_company_website);
        companyNameInput = view.findViewById(R.id.register_input_company_name);
        companyUidInput = view.findViewById(R.id.register_input_company_uid);

        accountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);

        //adjust fragment if this fragment is used for profile
        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnCompanyInformation.setHint(getString(R.string.myProfile_apply_changes));
            btnCompanyInformation.setVisibility(View.INVISIBLE);
            editMode = true;
            startup = (Startup) accountViewModel.getAccount().getValue();
            contactDetails = AccountService.getContactData();
            hideBottomNavigation(false);
        } else {
            startup = RegistrationHandler.getStartup();
            contactDetails = RegistrationHandler.getContactData();
        }

        return view;
    }

    @Override
    public void onResourcesLoaded() {
        if (resources.getCountry(startup.getCountryId()) != null)
            companyCountryInput.setText(resources.getCountry(startup.getCountryId()).getName());

        // Country picker
        countryItems = new ArrayList<>();
        resources.getCountries().forEach(country -> countryItems.add(new Country(country)));
        CustomPicker.Builder pickerBuilder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .listener(country -> {
                            companyCountryInput.setText(country.getName());
                            countrySelected = (Country) country;
                        })
                        .setItems(resources.getCountries());

        countryPicker = pickerBuilder.build();

        companyCountryInput.setOnClickListener(v -> {
            if (countryPicker.instanceRunning())
                countryPicker.dismiss();

            countryPicker.showDialog(getActivity());
        });

        TextInputLayout companyUidLayout = getView().findViewById(R.id.register_company_uid);
        companyUidLayout.setEndIconOnClickListener(v ->
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.registration_information_dialog_title))
                        .setMessage(getString(R.string.registration_information_dialog_uid))
                        .setPositiveButton(getString(R.string.ok_text), (dialog, which) -> {
                        })
                        .setNegativeButton(getString(R.string.register_uid_find), (dialog, which) -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(getString(R.string.register_uid_link)));
                            startActivity(browserIntent);
                        })
                        .show());

        // fill text inputs with existing user data
        companyNameInput.setText(startup.getCompanyName());
        companyPhoneInput.setText(contactDetails.getPhone());
        companyWebsiteInput.setText(startup.getWebsite());
        companyUidInput.setText(startup.getUId());
        countrySelected = resources.getCountry(startup.getCountryId());

        // if editmode, add text watchers
        if(editMode) {
            companyNameInput.addTextChangedListener(this);
            companyUidInput.addTextChangedListener(this);
            companyWebsiteInput.addTextChangedListener(this);
            companyPhoneInput.addTextChangedListener(this);
            companyCountryInput.addTextChangedListener(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnCompanyInformation.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onAccountUpdated() {
        if (!editMode)
            return;

        resetTab();
        AccountService.saveContactData(contactDetails);
        popFragment(this);
    }

    @Override
    public void onDestroyView() {
        hideBottomNavigation(false);
        super.onDestroyView();
    }

    /**
     * Process entered information
     */
    private void processInformation() {
        if (companyNameInput.getText().length() == 0
                || companyUidInput.getText().length() == 0
                || companyPhoneInput.getText().length() == 0
                || countrySelected == null) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        if (companyNameInput.length() > getResources().getInteger(R.integer.raisingMaximumNameLength)) {
            showSimpleDialog(getString(R.string.register_dialog_title), getString(R.string.register_dialog_long_name));
            return;
        }

        if (!isValidUid(companyUidInput.getText().toString())) {
            showSimpleDialog(getString(R.string.simple_dialog_invalid_input_title),
                    getString(R.string.invalid_uid_text));
            return;
        }

        startup.setCompanyName(companyNameInput.getText().toString());
        startup.setUId(companyUidInput.getText().toString());

        contactDetails.setPhone(companyPhoneInput.getText().toString());
        if (companyWebsiteInput.getText().toString().length() != 0 && !(companyWebsiteInput.getText().toString().contains("http"))) {
            String website = "http://" + companyWebsiteInput.getText().toString();
            startup.setWebsite(website);
        } else {
            startup.setWebsite(companyWebsiteInput.getText().toString());
        }
        startup.setCountryId(countrySelected.getId());

        try {
            if (!editMode) {
                RegistrationHandler.saveStartup(startup);
                RegistrationHandler.saveContactData(contactDetails);
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


    /**
     * Check whether given uid is a valid one
     *
     * @param uid
     * @return
     */
    private boolean isValidUid(String uid) {
        if (!uid.matches("[A-Z]{3}-\\d\\d\\d\\.\\d\\d\\d\\.\\d\\d\\d")) {
            return false;
        }

        uid = uid.substring(4);
        uid = uid.replace(".", "");
        int number = 0;
        int[] multipliers = {5, 4, 3, 2, 7, 6, 5, 4};
        int checksum = Integer.parseInt(String.valueOf(uid.charAt(uid.length() - 1)));
        for (int i = 0; i < uid.length() - 1; ++i) {
            number += Integer.parseInt(String.valueOf(uid.charAt(i))) * multipliers[i];
        }

        int remainder = 0;
        if (number % 11 != 0)
            remainder = 11 - (number % 11);

        if (checksum == remainder) {
            return true;
        }

        return false;
    }
}
