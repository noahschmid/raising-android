package com.raising.app.authentication.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.raising.app.R;
import com.raising.app.RaisingFragment;

public class RegisterCompanyInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText companyNameInput, companyUidInput, companyRevenueInput,
            companyBreakevenInput, companyFteInput, companyMarketInput;
    private AutoCompleteTextView revenueInput, foundingInput;
    private MultiAutoCompleteTextView marketsInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_company_information, container, false);

        // TODO: fetch VALUES_REVENUE AND VALUES_MARKET from backend
        String [] VALUES_REVENUE = new String[] {"CHF 1 - 100'000", "CHF 100'000 - 1'000'000",
                "CHF 1'000'000 - 10'000'000", "CHF 10'000'000+"};
        String [] VALUES_MARKETS = new String[] {"Asia", "Europe", "Africa", "Australia",
                "North America", "South America"};
        String [] VALUES_YEARS = new String[] {"2000", "1990", "1980", "1970"};

        ArrayAdapter adapterRevenue = new ArrayAdapter<>( getContext(),
                R.layout.dropdown_menu_items, VALUES_REVENUE);

        ArrayAdapter adapterMarkets = new ArrayAdapter<>( getContext(),
                R.layout.dropdown_menu_items, VALUES_MARKETS);

        ArrayAdapter adapterYear = new ArrayAdapter<>( getContext(),
                R.layout.dropdown_menu_items, VALUES_YEARS);

        revenueInput = view.findViewById(R.id.register_input_company_revenue);
        revenueInput.setAdapter(adapterRevenue);

        marketsInput = view.findViewById(R.id.register_input_company_markets);
        marketsInput.setAdapter(adapterMarkets);
        marketsInput.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        foundingInput = view.findViewById(R.id.register_input_company_founding_year);
        foundingInput.setAdapter(adapterYear);


        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        companyNameInput = view.findViewById(R.id.register_input_company_name);
        companyUidInput = view.findViewById(R.id.register_input_company_uid);
        companyRevenueInput = view.findViewById(R.id.register_input_company_revenue);
        companyBreakevenInput = view.findViewById(R.id.register_input_company_breakeven);
        companyFteInput = view.findViewById(R.id.register_input_company_fte);
        companyMarketInput = view.findViewById(R.id.register_input_company_markets);

        Button btnCompanyInformation = view.findViewById(R.id.button_company_information);
        btnCompanyInformation.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        switch(getId()) {
            case R.id.button_company_information:
                //TODO: insert function to be executed
                break;
            default:
                break;
        }
    }
}
