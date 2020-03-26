package com.raising.app.authentication.fragments.registration.startup;

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

import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.LoginFragment;
import com.raising.app.models.Startup;
import com.raising.app.util.RegistrationHandler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Callable;

public class RegisterFinancialRequirementsFragment extends RaisingFragment implements View.OnClickListener {
    private EditText financialValuationInput, financialClosingTimeInput, scopeInput;
    private AutoCompleteTextView financialTypeInput;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Calendar selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_financial_requirements,
                container, false);

        hideBottomNavigation(true);

        //TODO: fetch these values from our backend
        String [] VALUES_TYPE = new String[] {"Equity", "Deposit", "Loan", "Grant"};

        ArrayAdapter adapterType = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_TYPE);

        financialTypeInput = view.findViewById(R.id.register_input_financial_type);
        financialTypeInput.setAdapter(adapterType);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scopeInput = view.findViewById(R.id.register_input_startup_financial_scope);
        financialValuationInput = view.findViewById(R.id.register_input_financial_valuation);

        Button btnFinancialRequirements = view.findViewById(R.id.button_financial_requirements);
        btnFinancialRequirements.setOnClickListener(this);

        financialClosingTimeInput = view.findViewById(R.id.register_input_financial_closing_time);
        financialClosingTimeInput.setOnClickListener(v -> prepareDatePicker());

        Startup startup = RegistrationHandler.getStartup();
        scopeInput.setText(String.valueOf(startup.getScope()));

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
               String selected = dayOfMonth + "." + (month + 1) + "." + year;
               financialClosingTimeInput.setText(selected);

               selectedDate = Calendar.getInstance();
               selectedDate.set(Calendar.YEAR, year);
               selectedDate.set(Calendar.MONTH, month);
               selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };

        if(this.getArguments() != null && this.getArguments().getBoolean("isProfileFragment")) {
            btnFinancialRequirements.setHint(getString(R.string.myProfile_apply_changes));
            btnFinancialRequirements.setOnClickListener(v -> popCurrentFragment(this));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_financial_requirements){
            processInputs();
        }
    }

    /**
     * Process inputs and submit registration
     */
    private void processInputs() {
        Log.d("debugMessage", "processInputs");
        if(scopeInput.getText().length() == 0 || selectedDate == null) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        long type = 1;

        float valuation = 0;
        if(financialValuationInput.getText().length() > 0)
            valuation = Float.parseFloat(financialValuationInput.getText().toString());

        if(selectedDate.before(Calendar.getInstance())) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_invalid_date));
            return;
        }

        float scope = Float.parseFloat(scopeInput.getText().toString());

        if(financialClosingTimeInput.getText().length() == 0 ||
                financialTypeInput.getText().length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        try {
            RegistrationHandler.proceed();
            RegistrationHandler.saveFinancialRequirements(type, valuation, selectedDate, scope);
            changeFragment(new RegisterStakeholderFragment());
        } catch (IOException e) {
            Log.d("debugMessage", e.getMessage());
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
