package com.raising.app.fragments.registration.startup.stakeholderInputs;

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

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.FounderViewModel;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.ResourcesManager;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;

import java.util.ArrayList;
import java.util.Arrays;

public class FounderInputFragment extends RaisingFragment {
    private FounderViewModel founderViewModel;

    private EditText founderFirstNameInput, founderLastNameInput,
            founderEducationInput, founderCountryInput;
    private AutoCompleteTextView founderCompanyPositionInput;

    private Founder founder;
    private CustomPicker countryPicker;
    private int countryId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stakeholder_founder,
                container, false);

        hideBottomNavigation(true);
        return view;
    }

    public void passFounder(Founder founder) {
        this.founder = founder;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        founderViewModel = new ViewModelProvider(requireActivity()).get(FounderViewModel.class);

        ArrayList<String> positions = new ArrayList<String>(Arrays.asList(
                new String[]{"CEO", "CFO", "VRP"}));


        NoFilterArrayAdapter<String> adapterPosition = new NoFilterArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, positions);

        founderFirstNameInput = view.findViewById(R.id.input_founder_first_name);
        founderLastNameInput = view.findViewById(R.id.input_founder_last_name);
        founderEducationInput = view.findViewById(R.id.input_founder_education);

        founderCompanyPositionInput = view.findViewById(R.id.input_founder_poistion);
        founderCompanyPositionInput.setAdapter(adapterPosition);

        founderCompanyPositionInput.setShowSoftInputOnFocus(false);

        if(founder == null) {
            founder = new Founder();
        } else {
            founderFirstNameInput.setText(founder.getFirstName());
            founderLastNameInput.setText(founder.getLastName());
            founderCompanyPositionInput.setText(founder.getPosition());
            founderEducationInput.setText(founder.getEducation());
        }

        Button btnCancelFounder = view.findViewById(R.id.button_cancel_founder);
        btnCancelFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveFounderFragment();
            }
        });

        Button btnAddFounder = view.findViewById(R.id.button_add_founder);
        btnAddFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = founderFirstNameInput.getText().toString();
                String lastName = founderLastNameInput.getText().toString();
                String companyPosition = founderCompanyPositionInput.getText().toString();
                String education = founderEducationInput.getText().toString();

                if(firstName.length() == 0 || lastName.length() == 0
                        || companyPosition.length() == 0 || countryId == -1) {
                    showSimpleDialog(getString(R.string.register_dialog_title),
                            getString(R.string.register_dialog_text_empty_credentials));
                    return;
                }
                Founder founder = new Founder(firstName, lastName, companyPosition,
                        education, countryId);

                founderViewModel.select(founder);
                leaveFounderFragment();
            }
        });

        //country picker
        founderCountryInput = view.findViewById(R.id.input_founder_country);
        founderCountryInput.setShowSoftInputOnFocus(false);
        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .listener(new OnCustomPickerListener() {
                            @Override
                            public void onSelectItem(PickerItem country) {
                                founderCountryInput.setText(country.getName());
                                countryId = (int)country.getId();
                            }
                        })
                        .setItems(ResourcesManager.getCountries());

        countryPicker = builder.build();

        founderCountryInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    countryPicker.showDialog(getActivity());
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
     * {@link RaisingFragment#popCurrentFragment(androidx.fragment.app.Fragment)}
     */
    private void leaveFounderFragment() {
        popCurrentFragment(this);
    }
}

