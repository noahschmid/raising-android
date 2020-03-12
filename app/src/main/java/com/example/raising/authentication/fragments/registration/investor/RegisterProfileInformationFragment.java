package com.example.raising.authentication.fragments.registration.investor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.raising.R;
import com.example.raising.RaisingFragment;

public class RegisterProfileInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText profileCompanyInput, profileWebsiteInput, profileStreetInput,
            profileZipInput, profileCityInput, profileCountryInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_profile_information, container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileCompanyInput = view.findViewById(R.id.register_input_profile_company);
        profileWebsiteInput = view.findViewById(R.id.register_input_profile_website);
        profileStreetInput = view.findViewById(R.id.register_input_profile_street);
        profileZipInput = view.findViewById(R.id.register_input_profile_zip);
        profileCityInput = view.findViewById(R.id.register_input_profile_city);
        profileCountryInput = view.findViewById(R.id.register_input_profile_country);

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
        switch(getId()) {
            case R.id.button_profile_information:
                //TODO: insert function to be executed
                break;
            default:
                break;
        }
    }
}
