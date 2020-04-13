package com.raising.app.fragments.registration.startup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.ContactDetails;
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Revenue;
import com.raising.app.models.Startup;
import com.raising.app.util.AccountService;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.ResourcesManager;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterCompanyFiguresFragment extends RaisingFragment {
    private AutoCompleteTextView companyRevenueInput;
    private Button companyMarketsButton;
    private EditText companyFteInput, companyBreakevenInput, companyFoundingInput;

    private CustomPicker marketsPicker;
    public ArrayList<PickerItem> marketItems;

    private int revenueMinId = -1;
    private int revenueMaxId = -1;

    private Startup startup;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_company_figures,
                container, false);

        ArrayList<Revenue> revenues = ResourcesManager.getRevenues();
        ArrayList<String> values = new ArrayList<>();
        revenues.forEach(rev -> values.add(rev.toString(getString(R.string.currency),
                getResources().getStringArray(R.array.revenue_units))));

        NoFilterArrayAdapter<String> adapterRevenue = new NoFilterArrayAdapter<String>(getContext(),
                R.layout.item_dropdown_menu, values);

        companyRevenueInput = view.findViewById(R.id.register_input_company_revenue);
        companyRevenueInput.setAdapter(adapterRevenue);

        companyRevenueInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemName = (String) adapterRevenue.getItem(i);

                for (Revenue rev : revenues) {
                    if (rev.toString(getString(R.string.currency),
                            getResources().getStringArray(R.array.revenue_units)).equals(itemName)) {
                        revenueMinId = rev.getRevenueMinId();
                        revenueMaxId = rev.getRevenueMaxId();
                        break;
                    }
                }
            }
        });

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout companyFteLayout = view.findViewById(R.id.register_company_fte);
        companyFteLayout.setEndIconOnClickListener(v -> {
            final Snackbar snackbar = Snackbar.make(companyFteLayout,
                    R.string.register_fte_helper_text, Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.got_it_text), v12 -> snackbar.dismiss());
            snackbar.setDuration(5000)
                    .show();
        });

        companyFteInput = view.findViewById(R.id.register_input_company_fte);
        companyBreakevenInput = view.findViewById(R.id.register_input_company_breakeven);
        companyMarketsButton = view.findViewById(R.id.register_button_company_markets);
        companyFoundingInput = view.findViewById(R.id.register_input_company_founding_year);

        Button btnCompanyFigures = view.findViewById(R.id.button_company_figures);
        btnCompanyFigures.setOnClickListener(v -> processInformation());

        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnCompanyFigures.setHint(getString(R.string.myProfile_apply_changes));
            editMode = true;
            startup = (Startup) AccountService.getAccount();
        } else {
            startup = RegistrationHandler.getStartup();
        }

        if (startup.getRevenueMinId() > -1) {
            revenueMinId = startup.getRevenueMinId();
            revenueMaxId = startup.getRevenueMaxId();

            companyRevenueInput.setText(ResourcesManager
                    .getRevenueString(startup.getRevenueMinId()), false);
        }

        if (startup.getNumberOfFte() > 0)
            companyFteInput.setText(Integer.toString(startup.getNumberOfFte()));
        if (startup.getFoundingYear() > 0)
            companyFoundingInput.setText(Integer.toString(startup.getFoundingYear()));
        if (startup.getBreakEvenYear() > 0)
            companyBreakevenInput.setText(Integer.toString(startup.getBreakEvenYear()));

        companyBreakevenInput.setOnClickListener(v -> showYearPicker("Select breakeven year", companyBreakevenInput));
        companyFoundingInput.setOnClickListener(v -> showYearPicker("Select founding year", companyFoundingInput));

        // Markets picker
        marketItems = new ArrayList<>();
        marketItems.addAll(ResourcesManager.getContinents());
        marketItems.addAll(ResourcesManager.getCountries());

        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .multiSelect(true)
                        .setItems(marketItems);

        marketsPicker = builder.build();

        companyMarketsButton.setOnClickListener(v -> {
            if (marketsPicker.instanceRunning())
                marketsPicker.dismiss();

            marketsPicker.showDialog(getActivity());
        });


        // restore selected markets
        ArrayList<Long> selected = new ArrayList<>();

        if (startup.getContinents().size() > 0) {
            selected.addAll(startup.getContinents());
        }

        if (startup.getCountries().size() > 0) {
            selected.addAll(startup.getCountries());
        }

        if (selected.size() > 0) {
            marketsPicker.setSelectedById(selected);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    private void processInformation() {
        if (companyBreakevenInput.getText().length() == 0 ||
                companyFteInput.getText().length() == 0 ||
                companyRevenueInput.getText().length() == 0 ||
                companyFoundingInput.getText().length() == 0 ||
                revenueMinId == -1 || revenueMaxId == -1) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        ArrayList<Long> countries = new ArrayList<>();
        ArrayList<Long> continents = new ArrayList<>();

        marketItems.forEach(item -> {
            if (item instanceof Continent && item.isChecked()) {
                continents.add(((Continent) item).getId());
                marketItems.forEach(i -> {
                    if (i instanceof Country) {
                        i.setChecked(false);
                    }
                });
            }

            if (item instanceof Country && item.isChecked()) {
                countries.add(((Country) item).getId());
            }
        });

        if (countries.isEmpty() && continents.isEmpty()) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        startup.setCountries(countries);
        startup.setContinents(continents);
        startup.setBreakEvenYear(Integer.parseInt(
                companyBreakevenInput.getText().toString()));
        startup.setNumberOfFte(Integer.parseInt(companyFteInput.getText().toString()));

        startup.setRevenueMinId(revenueMinId);
        startup.setRevenueMaxId(revenueMaxId);

        startup.setFoundingYear(Integer.parseInt(companyFoundingInput.getText().toString()));

        try {
            if (!editMode) {
                RegistrationHandler.saveStartup(startup);
                changeFragment(new RegisterStartupMatchingFragment(),
                        "RegisterStartupMatchingFragment");
            } else {
                AccountService.updateAccount(startup, v -> {
                    popCurrentFragment(this);
                    return null;
                });
            }

        } catch (IOException e) {
            Log.d("RegisterCompanyFiguresFragment", "Error while processing inputs: "
                    + e.getMessage());
        }
    }
}
