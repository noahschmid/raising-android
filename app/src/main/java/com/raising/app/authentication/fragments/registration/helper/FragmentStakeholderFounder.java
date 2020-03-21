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

import com.raising.app.R;
import com.raising.app.RaisingFragment;

public class FragmentStakeholderFounder extends RaisingFragment implements View.OnClickListener {
    private EditText founderFirstNameInput, founderLastNameInput,
            founderEducationInput;
    private AutoCompleteTextView founderCompanyPositionInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stakeholder_founder,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String [] VALUES_POSITIONS = new String[] {"CEO", "CFO", "VRP" };

        ArrayAdapter adapterPosition = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_POSITIONS);

        founderFirstNameInput = view.findViewById(R.id.input_founder_first_name);
        founderLastNameInput = view.findViewById(R.id.input_founder_last_name);
        founderEducationInput = view.findViewById(R.id.input_founder_education);

        founderCompanyPositionInput = view.findViewById(R.id.input_founder_poistion);
        founderCompanyPositionInput.setAdapter(adapterPosition);

        Button btnCancelFounder = view.findViewById(R.id.button_cancel_founder);
        btnCancelFounder.setOnClickListener(this);
        Button btnAddFounder = view.findViewById(R.id.button_add_founder);
        btnAddFounder.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View v) {
        switch(getId()) {
            case R.id.button_cancel_founder:
                //TODO: pop uppermost fragment from backstack
                break;
            case R.id.button_add_founder:
                //TODO: link with RegisterStakeholderFragment view ViewModel and pass data
                break;
            default:
                break;
        }

    }
}

