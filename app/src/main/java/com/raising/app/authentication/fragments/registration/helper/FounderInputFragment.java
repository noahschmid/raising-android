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

import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.registration.helper.viewModels.FounderViewModel;
import com.raising.app.models.stakeholder.Founder;

public class FounderInputFragment extends RaisingFragment {
    private FounderViewModel founderViewModel;

    private EditText founderFirstNameInput, founderLastNameInput,
            founderEducationInput;
    private AutoCompleteTextView founderCompanyPositionInput;

    private Founder founder;

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

        if(founder == null) {
            founder = new Founder();
        }

        founderViewModel = new ViewModelProvider(requireActivity()).get(FounderViewModel.class);

        String [] VALUES_POSITIONS = new String[] {"CEO", "CFO", "VRP" };

        ArrayAdapter adapterPosition = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_POSITIONS);

        founderFirstNameInput = view.findViewById(R.id.input_founder_first_name);
        founderLastNameInput = view.findViewById(R.id.input_founder_last_name);
        founderEducationInput = view.findViewById(R.id.input_founder_education);

        founderCompanyPositionInput = view.findViewById(R.id.input_founder_poistion);
        founderCompanyPositionInput.setAdapter(adapterPosition);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            founderFirstNameInput.setText(bundle.getString("firstName"));
            founderLastNameInput.setText(bundle.getString("lastName"));
            founderCompanyPositionInput.setText(bundle.getString("position"));
            founderEducationInput.setText(bundle.getString("education"));
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
                        || companyPosition.length() == 0 || education.length() == 0) {
                    showSimpleDialog(getString(R.string.register_dialog_title),
                            getString(R.string.register_dialog_text_empty_credentials));
                    return;
                }

                Founder founder = new Founder(
                        firstName, lastName, companyPosition, education);

                founderViewModel.select(founder);

                leaveFounderFragment();

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideBottomNavigation(false);
    }

    private void leaveFounderFragment() {
        popCurrentFragment(this);
    }
}

