package com.raising.app.authentication.fragments.registration.investor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.LoginFragment;
import com.raising.app.models.PrivateProfile;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.ResourcesManager;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;

public class RegisterProfileInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText profileCompanyInput, profileWebsiteInput, profilePhoneInput, profileCountryInput;
    private CustomPicker customPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_profile_information,
                container, false);

        profileCountryInput = view.findViewById(R.id.register_input_profile_countries);

        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                .listener(new OnCustomPickerListener() {
                    @Override
                    public void onSelectItem(PickerItem country) {
                        profileCountryInput.setText(country.getName());
                    }
                })
                .setItems(ResourcesManager.getCountries());

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

        PrivateProfile profile = RegistrationHandler.getPrivateProfile();
        profileCompanyInput.setText(profile.getCompany());
        profileWebsiteInput.setText(profile.getWebsite());
        profileCountryInput.setText(profile.getCountry());
        profilePhoneInput.setText(profile.getPhone());


        Button btnProfileInformation = view.findViewById(R.id.button_profile_information);
        btnProfileInformation.setOnClickListener(this);
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
                // changeFragment(new RegisterInvestorMatchingFragment(), "RegisterInvestorMatchingFragment");
                break;
            default:
                break;
        }
    }

    /**
     * Check whether information is valid, then save profile information and
     * switch to next fragment
     */
    private void processProfileInformation() {
        String company = profileCompanyInput.getText().toString();
        String phone = profilePhoneInput.getText().toString();
        String country = profileCountryInput.getText().toString();
        String website = profileWebsiteInput.getText().toString();

        //TODO: also test country for length == 0
        if(country.length() == 0 || phone.length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        try {
            RegistrationHandler.saveProfileInformation(company, phone, website, country);
            changeFragment(new RegisterInvestorMatchingFragment(),
                    "RegisterInvestorMatchingFragment");
        } catch (Exception e) {
            Log.d("debugMessage", e.getMessage());
        }
    }
}
