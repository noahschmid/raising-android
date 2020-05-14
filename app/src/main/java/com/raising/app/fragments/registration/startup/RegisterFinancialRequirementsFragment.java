package com.raising.app.fragments.registration.startup;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.FinanceType;
import com.raising.app.models.Startup;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.RaisingTextWatcher;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.viewModels.AccountViewModel;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class RegisterFinancialRequirementsFragment extends RaisingFragment implements View.OnClickListener, RaisingTextWatcher {
    private EditText financialValuationInput, financialClosingTimeInput, scopeInput, committedInput;
    private AutoCompleteTextView financialTypeInput;
    private Button btnFinancialRequirements;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Calendar selectedDate;
    private int financialTypeId = -1;
    private boolean editMode = false;
    private Startup startup;

    final private String TAG = "RegisterFinancialRequirementsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_financial_requirements,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_financial_requirements), true);

        btnFinancialRequirements = view.findViewById(R.id.button_financial_requirements);
        btnFinancialRequirements.setOnClickListener(this);

        //define input views and button
        committedInput = view.findViewById(R.id.register_input_financial_committed);
        scopeInput = view.findViewById(R.id.register_input_startup_financial_scope);
        financialValuationInput = view.findViewById(R.id.register_input_financial_valuation);
        financialClosingTimeInput = view.findViewById(R.id.register_input_financial_closing_time);
        financialClosingTimeInput.setOnClickListener(v -> prepareDatePicker());

        accountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);

        //adjust fragment if this fragment is used for profile
        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnFinancialRequirements.setHint(getString(R.string.myProfile_apply_changes));
            btnFinancialRequirements.setVisibility(View.INVISIBLE);
            editMode = true;
            startup = (Startup) accountViewModel.getAccount().getValue();
            hideBottomNavigation(false);
        } else {
            startup = RegistrationHandler.getStartup();
        }

        return view;
    }

    @Override
    public void onResourcesLoaded() {
        ArrayList<FinanceType> financeTypes = resources.getFinanceTypes();
        ArrayList<String> values = new ArrayList<>();
        financeTypes.forEach(type -> values.add(type.getName()));

        NoFilterArrayAdapter<String> adapterType = new NoFilterArrayAdapter(getContext(),
                R.layout.item_dropdown_menu, values);

        financialTypeInput = getView().findViewById(R.id.register_input_financial_type);
        financialTypeInput.setShowSoftInputOnFocus(false);
        financialTypeInput.setAdapter(adapterType);


        if (startup.getFinanceTypeId() != -1) {
            financialTypeInput.setText(resources.getFinanceType(
                    startup.getFinanceTypeId()
            ).getName());
            financialTypeId = (int) startup.getFinanceTypeId();
        }

        financialTypeInput.setOnItemClickListener((AdapterView.OnItemClickListener) (adapterView, view, i, l) -> {
            String itemName = (String) adapterType.getItem(i);

            for (FinanceType type : financeTypes) {
                if (type.getName().equals(itemName)) {
                    financialTypeId = (int) type.getId();
                    break;
                }
            }
        });

        TextInputLayout financialCommittedLayout = getView().findViewById(R.id.register_financial_committed);
        financialCommittedLayout.setEndIconOnClickListener(v -> {
            showSimpleDialog(getString(R.string.registration_information_dialog_title),
                    getString(R.string.registration_information_dialog_committed));
        });

        TextInputLayout financialScopeLayout = getView().findViewById(R.id.register_financial_scope);
        financialScopeLayout.setEndIconOnClickListener(v -> {
            showSimpleDialog(getString(R.string.registration_information_dialog_title),
                    getString(R.string.registration_information_dialog_scope));
        });

        // fill text inputs with existing user data
        if (startup.getClosingTime() != null) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(startup.getClosingTime());
                selectedDate = Calendar.getInstance();
                selectedDate.setTime(date);

                formatter = new SimpleDateFormat("dd.MM.yyyy");

                financialClosingTimeInput.setText(formatter.format(date));
            } catch (ParseException e) {
                Log.d("RegisterFinancialRequirements", "error parsing date!");
            }
        }
        if (startup.getScope() > 0)
            scopeInput.setText(String.valueOf(startup.getScope()));

        if (startup.getPreMoneyValuation() > 0)
            financialValuationInput.setText(String.valueOf(startup.getPreMoneyValuation()));

        if (startup.getRaised() > 0)
            committedInput.setText(String.valueOf(startup.getRaised()));

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

        // if editmode, add text watchers
        if (editMode) {
            financialValuationInput.addTextChangedListener(this);
            financialClosingTimeInput.addTextChangedListener(this);
            scopeInput.addTextChangedListener(this);
            committedInput.addTextChangedListener(this);
            financialTypeInput.addTextChangedListener(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_financial_requirements) {
            processInputs();
        }
    }

    /**
     * This method is called, when the account is update.
     * The method pops the current fragment and signals to the view model, that the update is complete.
     */
    @Override
    protected void onAccountUpdated() {
        resetTab();
        popCurrentFragment(this);
        accountViewModel.updateCompleted();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnFinancialRequirements.setVisibility(View.VISIBLE);
    }

    /**
     * Process inputs and submit registration
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processInputs() {
        Log.d("debugMessage", "processInputs");
        float valuation = 0;
        float scope = 0;
        int committed = 0;

        // check if all mandatory inputs have been filled
        if (scopeInput.getText().length() == 0 || selectedDate == null ||
                financialTypeId == -1) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        // validate scope
        if(Integer.parseInt(scopeInput.getText().toString()) > 2000000000) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_financial_error_high_scope));
            return;
        }
        if (Integer.parseInt(scopeInput.getText().toString()) < 20000 || Integer.parseInt(scopeInput.getText().toString()) == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_financial_error_low_scope));
            return;
        }
        scope = Float.parseFloat(scopeInput.getText().toString());


        // validate committed
        if (committed > (int) scope) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_financial_error_committed));
            return;
        }
        if (committedInput.getText().length() != 0) {
            committed = Integer.parseInt(committedInput.getText().toString());
        }

        // validate closing time
        if (selectedDate.before(Calendar.getInstance())) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_invalid_date));
            return;
        }

        // set pre money valuation
        if (financialValuationInput.getText().length() > 0)
            valuation = Float.parseFloat(financialValuationInput.getText().toString());

        // set other values
        startup.setFinanceTypeId(financialTypeId);
        startup.setPreMoneyValuation((int) valuation);
        startup.setScope((int) scope);
        startup.setRaised(committed);

        Date completedDate = selectedDate.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        startup.setClosingTime(formatter.format(completedDate));

        try {
            if (!editMode) {
                RegistrationHandler.saveStartup(startup);
                changeFragment(new RegisterStakeholderFragment());
            } else {
                accountViewModel.update(startup);
            }
        } catch (IOException e) {
            Log.e("RegisterFinancialRequirements", "Error in processInputs: " + e.getMessage());
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
                R.style.DatePickerStyle, dateSetListener, year, month, day);
        datePickerDialog.show();
    }
}
