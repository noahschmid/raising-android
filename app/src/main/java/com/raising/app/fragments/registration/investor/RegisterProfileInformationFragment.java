package com.raising.app.fragments.registration.investor;

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


import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.ContactData;
import com.raising.app.models.Investor;
import com.raising.app.util.AccountService;
import com.raising.app.util.RaisingTextWatcher;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;
import com.raising.app.viewModels.AccountViewModel;

public class RegisterProfileInformationFragment extends RaisingFragment implements RaisingTextWatcher {
    private EditText profileCompanyInput, profileWebsiteInput, profilePhoneInput, profileCountryInput;
    private CustomPicker customPicker;
    private Button btnProfileInformation;
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

        accountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);

        //define input views and button
        profileCompanyInput = view.findViewById(R.id.register_input_profile_company);
        profileWebsiteInput = view.findViewById(R.id.register_input_profile_website);
        profilePhoneInput = view.findViewById(R.id.register_input_profile_phone);
        profileCountryInput = view.findViewById(R.id.register_input_profile_countries);

        btnProfileInformation = view.findViewById(R.id.button_profile_information);
        btnProfileInformation.setOnClickListener(v -> processProfileInformation());

        //adjust fragment if this fragment is used for profile
        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnProfileInformation.setHint(getString(R.string.myProfile_apply_changes));
            btnProfileInformation.setVisibility(View.INVISIBLE);
            investor = (Investor) accountViewModel.getAccount().getValue();
            contactDetails = AccountService.getContactData();
            editMode = true;
            hideBottomNavigation(false);
        } else {
            investor = RegistrationHandler.getInvestor();
            contactDetails = RegistrationHandler.getContactData();
        }

        return view;
    }

    @Override
    public void onResourcesLoaded() {
        // fill views with users existing data
        profileCompanyInput.setText(investor.getCompanyName());
        profileWebsiteInput.setText(investor.getWebsite());

        if (resources.getCountry(investor.getCountryId()) != null)
            profileCountryInput.setText(
                    resources.getCountry(investor.getCountryId()).getName());

        profilePhoneInput.setText(contactDetails.getPhone());

        profileCountryInput.setShowSoftInputOnFocus(false);
        if (investor.getCountryId() != -1)
            countryId = (int) investor.getCountryId();

        setupCountryPicker();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // if editmode, add text watchers after initial filling with users data
        if (editMode) {
            profileCompanyInput.addTextChangedListener(this);
            profileWebsiteInput.addTextChangedListener(this);
            profilePhoneInput.addTextChangedListener(this);
            profileCountryInput.addTextChangedListener(this);
        }
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
                                countryId = (int) country.getId();
                            }
                        })
                        .setItems(resources.getCountries());

        customPicker = builder.build();

        profileCountryInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!customPicker.instanceRunning())
                        customPicker.showDialog(getActivity());
                }
            }
        });

        profileCountryInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customPicker.instanceRunning())
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
    protected void onAccountUpdated() {
        popCurrentFragment(this);
        accountViewModel.updateCompleted();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnProfileInformation.setVisibility(View.VISIBLE);
    }

    /**
     * Check whether information is valid, then save profile information and
     * switch to next fragment
     */
    private void processProfileInformation() {
        String companyName = profileCompanyInput.getText().toString();
        contactDetails.setPhone(profilePhoneInput.getText().toString());
        if (profileWebsiteInput.getText().toString().length() != 0 && !(profileWebsiteInput.getText().toString().contains("http"))) {
            String website = "http://" + profileWebsiteInput.getText().toString();
            investor.setWebsite(website);
        } else {
            investor.setWebsite(profileWebsiteInput.getText().toString());
        }

        if (countryId == -1 || contactDetails.getPhone().length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        if (companyName.length() > getResources().getInteger(R.integer.raisingMaximumNameLength)) {
            showSimpleDialog(getString(R.string.register_dialog_title), getString(R.string.register_dialog_long_name));
            return;
        }

        investor.setCountryId(countryId);
        investor.setCompanyName(companyName);

        try {
            if (!editMode) {
                RegistrationHandler.saveContactData(contactDetails);
                RegistrationHandler.saveInvestor(investor);
                changeFragment(new RegisterInvestorMatchingFragment(),
                        "RegisterInvestorMatchingFragment");
            } else {
                if (AccountService.saveContactData(contactDetails)) {
                    accountViewModel.update(investor);
                } else {
                    showGenericError();
                }

            }
        } catch (Exception e) {
            Log.d("RegisterProfileInformation",
                    "Error while updating profile information: " + e.getMessage());
        }
    }
}
