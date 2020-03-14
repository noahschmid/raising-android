package com.raising.app.authentication.fragments.registration.startup;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.MainActivity;
import com.raising.app.R;
import com.raising.app.RaisingFragment;

import java.util.Calendar;
import java.util.Objects;

public class RegisterFinancialRequirementsFragment extends RaisingFragment implements View.OnClickListener {
    private EditText financialTypeInput, financialValuationInput, financialClosingTimeInput;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_financial_requirements,
                container, false);

        hideBottomNavigation(true);

        //TODO: fetch these values from our backend
        String [] VALUES_TYPE = new String[] {"Equity", "Deposit", "Loan", "Grant"};

        ArrayAdapter adapterType = new ArrayAdapter<>( getContext(),
                R.layout.dropdown_menu_items, VALUES_TYPE);

        AutoCompleteTextView typeExposedDropdown = view.findViewById(R.id.register_input_financial_type);
        typeExposedDropdown.setAdapter(adapterType);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        financialTypeInput = view.findViewById(R.id.register_input_financial_type);
        financialValuationInput = view.findViewById(R.id.register_input_financial_valuation);

        Button btnFinancialRequirements = view.findViewById(R.id.button_financial_requirements);
        btnFinancialRequirements.setOnClickListener(this);

        financialClosingTimeInput = view.findViewById(R.id.register_input_financial_closing_time);
        financialClosingTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareDatePicker();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            /**
             * Get date, that has been selected in datePicker
             * @param datePicker The DatePicker in which the date was selected
             * @param year The year of the selected date
             * @param month The month of the selected date, january is month 0, december is month 11
             * @param dayOfMonth The day of the selected date
             */
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                month = month + 1;
               String selectedDate = dayOfMonth + "." + month + "." + year;
               financialClosingTimeInput.setText(selectedDate);

            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        switch(getId()) {
            case R.id.button_financial_requirements:
                //TODO: insert function to call
                break;
            default:
                break;
        }
    }

    /**
     * Prepares the date picker with the current date and a custom theme
     */
    private void prepareDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Objects.requireNonNull(getView()).getContext(),
                R.style.DialogTheme, dateSetListener, year, month, day);

        datePickerDialog.show();
    }
}
