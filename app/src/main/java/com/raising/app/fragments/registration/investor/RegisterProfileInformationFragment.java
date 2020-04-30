package com.raising.app.fragments.registration.investor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.ContactData;
import com.raising.app.models.Investor;
import com.raising.app.util.AccountService;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;

public class RegisterProfileInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText profileCompanyInput, profileWebsiteInput, profilePhoneInput, profileCountryInput;
    private CustomPicker customPicker;
    private int countryId = -1;
    private boolean editMode = false;
    private ContactData contactDetails;
    private Investor investor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_profile_information,
                container, false);

        profileCountryInput = view.findViewById(R.id.register_input_profile_countries);

        customizeAppBar(getString(R.string.toolbar_title_profile_information), true);
        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileCompanyInput = view.findViewById(R.id.register_input_profile_company);
        profileWebsiteInput = view.findViewById(R.id.register_input_profile_website);
        profilePhoneInput = view.findViewById(R.id.register_input_profile_phone);
        profileCountryInput = view.findViewById(R.id.register_input_profile_countries);

        Button btnProfileInformation = view.findViewById(R.id.button_profile_information);
        btnProfileInformation.setOnClickListener(this);

        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnProfileInformation.setHint(getString(R.string.myProfile_apply_changes));
            investor = (Investor) accountViewModel.getAccount().getValue();
            contactDetails = AccountService.getContactData();
            editMode = true;
            hideBottomNavigation(false);
        } else {
            investor = RegistrationHandler.getInvestor();
            contactDetails = RegistrationHandler.getContactData();
        }

        setupCountryPicker();

        profileCompanyInput.setText(investor.getCompanyName());
        profileWebsiteInput.setText(investor.getWebsite());

        if(resources.getCountry(investor.getCountryId()) != null)
            profileCountryInput.setText(
                    resources.getCountry(investor.getCountryId()).getName());

        profilePhoneInput.setText(contactDetails.getPhone());

        profileCountryInput.setShowSoftInputOnFocus(false);
        if(investor.getCountryId() != -1)
            countryId = (int)investor.getCountryId();
    }

    private void setupCountryPicker() {
        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .listener(new OnCustomPickerListener() {
                            @Override
                            public void onSelectItem(PickerItem country) {
                                profileCountryInput.setText(country.getName());
                                countryId = (int)country.getId();
                            }
                        })
                        .setItems(resources.getCountries());

        customPicker = builder.build();

        profileCountryInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(!customPicker.instanceRunning())
                        customPicker.showDialog(getActivity());
                }
            }
        });

        profileCountryInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customPicker.instanceRunning())
                    customPicker.dismiss();

                customPicker.showDialog(getActivity());
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
            case R.id.button_profile_information:
                processProfileInformation();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onAccountUpdated() {
        popCurrentFragment(this);
        accountViewModel.updateCompleted();
    }

    /**
     * Check whether information is valid, then save profile information and
     * switch to next fragment
     */
    private void processProfileInformation() {
        String companyName = profileCompanyInput.getText().toString();
        contactDetails.setPhone(profilePhoneInput.getText().toString());
        if(profileWebsiteInput.getText().toString().length() != 0) {
            String website = "http://" + profileWebsiteInput.getText().toString();
            investor.setWebsite(website);
        }

        if(countryId == -1 || contactDetails.getPhone().length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        if(companyName.length() > getResources().getInteger(R.integer.raisingMaximumNameLength)) {
            showSimpleDialog(getString(R.string.register_dialog_title), getString(R.string.register_dialog_long_name));
            return;
        }

        investor.setCountryId(countryId);
        investor.setCompanyName(companyName);

        try {
            if(!editMode) {
                RegistrationHandler.saveContactData(contactDetails);
                RegistrationHandler.saveInvestor(investor);
                changeFragment(new RegisterInvestorMatchingFragment(),
                        "RegisterInvestorMatchingFragment");
            } else {
                if(AccountService.saveContactData(contactDetails)) {
                    accountViewModel.update(investor);
                } else {
                    showSimpleDialog(getString(R.string.generic_error_title),
                            getString(R.string.generic_error_text));
                }

            }
        } catch (Exception e) {
            Log.d("RegisterProfileInformation",
                    "Error while updating profile information: " + e.getMessage());
        }
    }
}
