package com.raising.app.authentication.fragments.registration.helper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

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
import com.raising.app.authentication.fragments.registration.helper.viewModels.ShareholderViewModel;
import com.raising.app.models.stakeholder.Shareholder;

public class ShareholderInputFragment extends RaisingFragment {
    private boolean privateShareholder;
    private ShareholderViewModel shareholderViewModel;

    private RadioButton selectPrivateShareholder, selectCorporateShareholder;
    private EditText privateFirstNameInput, privateLastNameInput, corporateNameInput, corporateWebsiteInput;
    private AutoCompleteTextView privateCountryInput, privateEquityInput, corporateBodyInput, corporateEquityInput;
    private FrameLayout privateFrameLayout, corporateFrameLayout;

    private Shareholder shareholder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stakeholder_shareholder,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    public void passShareholder(Shareholder shareholder) {
        this.shareholder = shareholder;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shareholderViewModel = new ViewModelProvider(requireActivity()).get(ShareholderViewModel.class);

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

        privateCountryInput.setShowSoftInputOnFocus(false);
        privateEquityInput.setShowSoftInputOnFocus(false);
        corporateBodyInput.setShowSoftInputOnFocus(false);
        corporateEquityInput.setShowSoftInputOnFocus(false);


        privateFrameLayout = view.findViewById(R.id.stakeholder_private_shareholder);
        privateFrameLayout.setVisibility(View.GONE);
        corporateFrameLayout = view.findViewById(R.id.stakeholder_corporate_shareholder);
        corporateFrameLayout.setVisibility(View.GONE);

        selectPrivateShareholder = view.findViewById(R.id.stakeholder_select_private_shareholder);
        selectPrivateShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateShareholder = true;
                corporateFrameLayout.setVisibility(View.GONE);
                privateFrameLayout.setVisibility(View.VISIBLE);
            }
        });
        selectCorporateShareholder = view.findViewById(R.id.stakeholder_select_corporate_shareholder);
        selectCorporateShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateShareholder = false;
                privateFrameLayout.setVisibility(View.GONE);
                corporateFrameLayout.setVisibility(View.VISIBLE);
            }
        });

        if(shareholder == null) {
            shareholder = new Shareholder();
        } else {
            if(shareholder.isPrivateShareholder()) {
                privateEquityInput.setText(shareholder.getEquityShare());
                privateShareholder = true;

                selectPrivateShareholder.setChecked(true);
                selectCorporateShareholder.setChecked(false);

                corporateFrameLayout.setVisibility(View.GONE);
                privateFrameLayout.setVisibility(View.VISIBLE);

                privateFirstNameInput.setText(shareholder.getFirstName());
                privateLastNameInput.setText(shareholder.getLastName());

                privateCountryInput.setText(shareholder.getCountry());

            } else {
                corporateEquityInput.setText(shareholder.getEquityShare());
                privateShareholder = false;

                selectPrivateShareholder.setChecked(false);
                selectCorporateShareholder.setChecked(true);

                privateFrameLayout.setVisibility(View.GONE);
                corporateFrameLayout.setVisibility(View.VISIBLE);

                corporateNameInput.setText(shareholder.getCorporateBody());
                corporateWebsiteInput.setText(shareholder.getWebsite());
                corporateBodyInput.setText(shareholder.getCorporateBody());
            }
        }

        Button btnCancelShareholder = view.findViewById(R.id.button_cancel_shareholder);
        btnCancelShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveShareholderFragment();
            }
        });
        Button btnAddShareholder = view.findViewById(R.id.floating_button_add_shareholder);
        btnAddShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(privateShareholder) {
                    String firstName = privateFirstNameInput.getText().toString();
                    String lastName = privateLastNameInput.getText().toString();
                    String country = privateCountryInput.getText().toString();
                    String privateEquityShare = privateEquityInput.getText().toString();

                    if(firstName.length() == 0 || lastName.length() == 0
                            || country.length() == 0 || privateEquityShare.length() == 0) {
                        showSimpleDialog(getString(R.string.register_dialog_title),
                                getString(R.string.register_dialog_text_empty_credentials));
                        return;
                    }
                    Shareholder newShareholder = new Shareholder(
                            true, firstName, lastName, country,
                            null, null, null, privateEquityShare);
                    shareholderViewModel.select(newShareholder);
                    leaveShareholderFragment();
                } else {
                    String name = corporateNameInput.getText().toString();
                    String corporateBody = corporateBodyInput.getText().toString();
                    String website = corporateWebsiteInput.getText().toString();
                    String corporateEquityShare = corporateEquityInput.getText().toString();

                    if(name.length() == 0 || corporateBody.length() == 0
                            || website.length() == 0 || corporateEquityShare.length() == 0) {
                        showSimpleDialog(getString(R.string.register_dialog_title),
                                getString(R.string.register_dialog_text_empty_credentials));
                        return;
                    }
                    Shareholder newShareholder = new Shareholder(
                            false, null, null, null,
                            name, corporateBody, website, corporateEquityShare);
                    shareholderViewModel.select(newShareholder);
                    leaveShareholderFragment();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    /**
     * {@link com.raising.app.RaisingFragment#popCurrentFragment(androidx.fragment.app.Fragment)}
     */
    private void leaveShareholderFragment() {
        popCurrentFragment(this);
    }
}
