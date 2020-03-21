package com.raising.app.authentication.fragments.registration.helper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.raising.app.R;
import com.raising.app.RaisingFragment;

public class FragmentStakeholderShareholder extends RaisingFragment implements View.OnClickListener {
    RadioButton selectPrivateShareholder, selectCorporateShareholder;
    EditText privateFirstNameInput, privateLastNameInput, corporateNameInput, corporateWebsiteInput;
    AutoCompleteTextView privateCountryInput, privateEquityInput, corporateBodyInput, corporateEquityInput;
    FrameLayout privateFrameLayout, corporateFrameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stakeholder_shareholder,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        privateFirstNameInput = view.findViewById(R.id.input_shareholder_first_name);
        privateLastNameInput = view.findViewById(R.id.input_shareholder_last_name);
        corporateNameInput = view.findViewById(R.id.input_shareholder_name);
        corporateWebsiteInput = view.findViewById(R.id.input_shareholder_website);

        String [] VALUES_COUNTRIES = new String[] { "Switzerland", "Sweden", "USA"};
        String [] VALUES_EQUITY = new String[] { "10", "20", "30"};
        String [] VALUES_CORPORATE_BODY = new String[] {"Family Business"};

        ArrayAdapter adapterCountries = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_COUNTRIES);

        ArrayAdapter adapterEquity = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_EQUITY);

        ArrayAdapter adapterCorporateBody = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_CORPORATE_BODY);

        privateCountryInput = view.findViewById(R.id.input_shareholder_country);
        privateCountryInput.setAdapter(adapterCountries);

        privateEquityInput = view.findViewById(R.id.input_shareholder_equity_share);
        privateEquityInput.setAdapter(adapterEquity);

        corporateBodyInput = view.findViewById(R.id.input_shareholder_corporate_body);
        corporateBodyInput.setAdapter(adapterCorporateBody);

        corporateEquityInput = view.findViewById(R.id.input_shareholder_corporate_equity_share);
        corporateEquityInput.setAdapter(adapterEquity);


        privateFrameLayout = view.findViewById(R.id.stakeholder_private_shareholder);
        privateFrameLayout.setVisibility(View.GONE);
        corporateFrameLayout = view.findViewById(R.id.stakeholder_corporate_shareholder);
        corporateFrameLayout.setVisibility(View.GONE);

        selectPrivateShareholder = view.findViewById(R.id.stakeholder_select_private_shareholder);
        selectPrivateShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                corporateFrameLayout.setVisibility(View.GONE);
                privateFrameLayout.setVisibility(View.VISIBLE);
            }
        });
        selectCorporateShareholder = view.findViewById(R.id.stakeholder_select_corporate_shareholder);
        selectCorporateShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateFrameLayout.setVisibility(View.GONE);
                corporateFrameLayout.setVisibility(View.VISIBLE);
            }
        });

        Button btnCancelShareholder = view.findViewById(R.id.button_cancel_shareholder);
        btnCancelShareholder.setOnClickListener(this);
        Button btnAddShareholder = view.findViewById(R.id.button_add_shareholder);
        btnAddShareholder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (getId()) {
            case R.id.button_cancel_shareholder:
                //TODO: insert method
                break;
            case R.id.button_add_shareholder:
                //TODO: insert method
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }
}
