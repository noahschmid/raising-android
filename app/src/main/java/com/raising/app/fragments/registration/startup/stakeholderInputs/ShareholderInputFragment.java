package com.raising.app.fragments.registration.startup.stakeholderInputs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.ShareholderViewModel;
import com.raising.app.models.CorporateBody;
import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.ResourcesManager;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;

import java.util.ArrayList;

public class ShareholderInputFragment extends RaisingFragment {
    private boolean privateShareholder;
    private ShareholderViewModel shareholderViewModel;

    private RadioButton selectPrivateShareholder, selectCorporateShareholder;
    private EditText privateFirstNameInput, privateLastNameInput,
            corporateNameInput, corporateWebsiteInput, privateCountryInput,
            corporateCountryInput, privateEquityInput, corporateEquityInput;
    private AutoCompleteTextView corporateBodyInput;
    private FrameLayout privateFrameLayout, corporateFrameLayout;
    private RadioGroup privateTypeGroup;
    private Shareholder shareholder;
    private CustomPicker countryPicker;
    private int countryId = -1;

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
        corporateCountryInput = view.findViewById(R.id.input_shareholder_corporate_country);

        privateTypeGroup = view.findViewById(R.id.private_shareholder_type);

        setupRadioGroup(ResourcesManager.getInvestorTypes(), privateTypeGroup);

        corporateCountryInput.setOnClickListener(v -> {
            countryPicker.dismiss();
            countryPicker.showDialog(getActivity());
        });

        ArrayList<String> bodies = new ArrayList<>();
        ResourcesManager.getCorporateBodies().forEach(body -> bodies.add(body.getName()));

        NoFilterArrayAdapter<String> adapterCorporateBody = new NoFilterArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, bodies);

        privateCountryInput = view.findViewById(R.id.input_shareholder_country);
        privateCountryInput.setShowSoftInputOnFocus(false);
        corporateCountryInput.setShowSoftInputOnFocus(false);

        privateEquityInput = view.findViewById(R.id.input_shareholder_equity_share);

        corporateBodyInput = view.findViewById(R.id.input_shareholder_corporate_body);
        corporateBodyInput.setAdapter(adapterCorporateBody);

        corporateEquityInput = view.findViewById(R.id.input_shareholder_corporate_equity_share);

        privateCountryInput.setShowSoftInputOnFocus(false);
        corporateBodyInput.setShowSoftInputOnFocus(false);

        privateCountryInput.setOnClickListener(v -> {
            countryPicker.dismiss();
            countryPicker.showDialog(getActivity());
        });

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

                tickRadioButton(privateTypeGroup, shareholder.getInvestorTypeId());

                privateCountryInput.setText(ResourcesManager.getCountry((int)shareholder
                        .getCountryId()).getName());

            } else {
                corporateEquityInput.setText(shareholder.getEquityShare());
                privateShareholder = false;

                selectPrivateShareholder.setChecked(false);
                selectCorporateShareholder.setChecked(true);

                privateFrameLayout.setVisibility(View.GONE);
                corporateFrameLayout.setVisibility(View.VISIBLE);

                if(shareholder.getCorporateBodyId() != -1)
                    corporateNameInput.setText(ResourcesManager.getCorporateBody(
                            shareholder.getCorporateBodyId()).getName());
                corporateWebsiteInput.setText(shareholder.getWebsite());
                corporateBodyInput.setText(ResourcesManager
                        .getCorporateBody(shareholder.getCorporateBodyId()).getName());

                corporateCountryInput.setText(ResourcesManager.getCountry((int)shareholder
                        .getCountryId()).getName());
            }
        }

        Button btnCancelShareholder = view.findViewById(R.id.button_cancel_shareholder);
        btnCancelShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveShareholderFragment();
            }
        });
        Button btnAddShareholder = view.findViewById(R.id.button_add_shareholder);
        btnAddShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(privateShareholder) {
                    String firstName = privateFirstNameInput.getText().toString();
                    String lastName = privateLastNameInput.getText().toString();
                    String privateEquityShare = privateEquityInput.getText().toString();
                    long typeId = getSelectedRadioId(privateTypeGroup);

                    if(firstName.length() == 0 || lastName.length() == 0 ||
                            countryId == -1 || privateEquityShare.length() == 0 ||
                            typeId == -1) {
                        showSimpleDialog(getString(R.string.register_dialog_title),
                                getString(R.string.register_dialog_text_empty_credentials));
                        return;
                    }
                    Shareholder newShareholder = new Shareholder(
                            true, firstName, lastName, countryId,
                            null, -1, null, privateEquityShare,
                            getSelectedRadioId(privateTypeGroup));
                    shareholderViewModel.select(newShareholder);
                    leaveShareholderFragment();
                } else {
                    String name = corporateNameInput.getText().toString();
                    String corporateBody = corporateBodyInput.getText().toString();
                    String website = corporateWebsiteInput.getText().toString();
                    String corporateEquityShare = corporateEquityInput.getText().toString();

                    int corporateBodyId = -1;
                    for(CorporateBody body : ResourcesManager.getCorporateBodies()) {
                        if(body.getName().equals(corporateBody)) {
                            corporateBodyId = (int)body.getId();
                        }
                    }

                    if(name.length() == 0 || corporateBody.length() == 0 || countryId == -1 ||
                            corporateBodyId == -1 || website.length() == 0 ||
                            corporateEquityShare.length() == 0) {
                        showSimpleDialog(getString(R.string.register_dialog_title),
                                getString(R.string.register_dialog_text_empty_credentials));
                        return;
                    }

                    Shareholder newShareholder = new Shareholder(
                            false, null, null, countryId,
                            name, corporateBodyId, website, corporateEquityShare, -1);
                    shareholderViewModel.select(newShareholder);
                    leaveShareholderFragment();
                }
            }
        });

        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .listener(new OnCustomPickerListener() {
                            @Override
                            public void onSelectItem(PickerItem country) {
                                privateCountryInput.setText(country.getName());
                                corporateCountryInput.setText(country.getName());
                                countryId = (int)country.getId();
                            }
                        })
                        .setItems(ResourcesManager.getCountries());

        countryPicker = builder.build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    /**
     * {@link RaisingFragment#popCurrentFragment(androidx.fragment.app.Fragment)}
     */
    private void leaveShareholderFragment() {
        popCurrentFragment(this);
    }
}
