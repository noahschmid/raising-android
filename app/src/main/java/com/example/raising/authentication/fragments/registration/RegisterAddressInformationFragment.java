package com.example.raising.authentication.fragments.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.raising.R;
import com.example.raising.RaisingFragment;

public class RegisterAddressInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText addressCountryInput, addressCityInput, addressZipInput,
            addressStreetInput, addressWebsiteInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_address_information, container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addressCountryInput = view.findViewById(R.id.register_input_address_country);
        addressCityInput = view.findViewById(R.id.register_input_address_city);
        addressZipInput = view.findViewById(R.id.register_input_address_zip);
        addressStreetInput = view.findViewById(R.id.register_input_address_street);
        addressWebsiteInput = view.findViewById(R.id.register_input_address_website);

        Button btnAddressInformation = view.findViewById(R.id.button_address_information);
        btnAddressInformation.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        switch(getId()) {
            case R.id.button_address_information:
                //TODO: insert function to be executed
                break;
            default:
                break;
        }
    }
}
