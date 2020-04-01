package com.raising.app.fragments.registration.startup;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.FinanceType;
import com.raising.app.models.Startup;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.ResourcesManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class RegisterFinancialRequirementsFragment extends RaisingFragment implements View.OnClickListener {
    private EditText financialValuationInput, financialClosingTimeInput, scopeInput, completedInput;
    private AutoCompleteTextView financialTypeInput;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Calendar selectedDate;
    private int financialTypeId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_financial_requirements,
                container, false);

        hideBottomNavigation(true);

        ArrayList<FinanceType> financeTypes = ResourcesManager.getFinanceTypes();
        ArrayList<String> values = new ArrayList<>();
        financeTypes.forEach(type -> values.add(type.getName()));

        NoFilterArrayAdapter<String> adapterType = new NoFilterArrayAdapter( getContext(),
                R.layout.item_dropdown_menu, values);

        financialTypeInput = view.findViewById(R.id.register_input_financial_type);
        financialTypeInput.setAdapter(adapterType);

        financialTypeInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemName = (String)adapterType.getItem(i);

                for (FinanceType type : financeTypes) {
                    if (type.getName().equals(itemName)) {
                        financialTypeId = (int)type.getId();
                        break;
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        financialTypeInput.setShowSoftInputOnFocus(false);

        scopeInput = view.findViewById(R.id.register_input_startup_financial_scope);
        financialValuationInput = view.findViewById(R.id.register_input_financial_valuation);

        Button btnFinancialRequirements = view.findViewById(R.id.button_financial_requirements);
        btnFinancialRequirements.setOnClickListener(this);

        financialClosingTimeInput = view.findViewById(R.id.register_input_financial_closing_time);
        financialClosingTimeInput.setOnClickListener(v -> prepareDatePicker());

        completedInput = view.findViewById(R.id.register_input_financial_completed);

        Startup startup = RegistrationHandler.getStartup();

        if(startup.getClosingTime() != null) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Date date = formatter.parse(startup.getClosingTime());
                selectedDate = Calendar.getInstance();
                selectedDate.setTime(date);
                financialClosingTimeInput.setText(selectedDate.get(Calendar.DAY_OF_MONTH)
                        + "." + (selectedDate.get(Calendar.MONTH) + 1 + "."
                                + selectedDate.get(Calendar.YEAR)));
            } catch (ParseException e) {
                Log.d("debugMessage", "error parsing date!");
            }
        }
        if(startup.getScope() > 0)
            scopeInput.setText(String.valueOf(startup.getScope()));

        if(startup.getPreMoneyValuation() > 0)
            financialValuationInput.setText(String.valueOf(startup.getPreMoneyValuation()));

        if(startup.getRaised() > 0)
            completedInput.setText(String.valueOf(startup.getRaised()));

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_financial_requirements){
            processInputs();
        }
    }

    /**
     * Process inputs and submit registration
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processInputs() {
        Log.d("debugMessage", "processInputs");
        if(scopeInput.getText().length() == 0 || selectedDate == null ||
                financialTypeId == -1 ||
                Integer.parseInt(scopeInput.getText().toString()) == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        float valuation = 0;

        if(financialValuationInput.getText().length() > 0)
            valuation = Float.parseFloat(financialValuationInput.getText().toString());

        if(selectedDate.before(Calendar.getInstance())) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_invalid_date));
            return;
        }

        float scope = Float.parseFloat(scopeInput.getText().toString());
        int completed = 0;
        if(completedInput.getText().length() != 0) {
            completed = Integer.parseInt(completedInput.getText().toString());
        }

        try {
            RegistrationHandler.saveFinancialRequirements(financialTypeId, valuation, selectedDate, scope,
                    completed);
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