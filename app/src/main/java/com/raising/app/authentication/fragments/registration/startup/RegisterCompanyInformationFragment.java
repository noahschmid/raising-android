package com.raising.app.authentication.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.registration.investor.RegisterInvestorPitchFragment;
import com.raising.app.models.Startup;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterCompanyInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText companyNameInput, companyUidInput, companyFteInput;
    private AutoCompleteTextView companyRevenueInput, companyBreakevenInput, companyFoundingInput;
    private MultiAutoCompleteTextView companyMarketsInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_company_information, container, false);

        // TODO: fetch VALUES_REVENUE from backend
        String [] VALUES_REVENUE = new String[] {"CHF 1 - 100'000", "CHF 100'000 - 1'000'000",
                "CHF 1'000'000 - 10'000'000", "CHF 10'000'000+"};
        String [] VALUES_YEARS = new String[] {"2000", "1990", "1980", "1970"};

        ArrayAdapter adapterRevenue = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_REVENUE);

        ArrayAdapter adapterYear = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_YEARS);

        companyRevenueInput = view.findViewById(R.id.register_input_company_revenue);
        companyRevenueInput.setAdapter(adapterRevenue);

        companyBreakevenInput = view.findViewById(R.id.register_input_company_breakeven);
        companyBreakevenInput.setAdapter(adapterYear);

        companyMarketsInput = view.findViewById(R.id.register_input_company_markets);
        companyMarketsInput.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        companyFoundingInput = view.findViewById(R.id.register_input_company_founding_year);
        companyFoundingInput.setAdapter(adapterYear);


        hideBottomNavigation(true);

        if(RegistrationHandler.hasBeenVisited()) {
            RegistrationHandler.skip();
            changeFragment(new RegisterAddressInformationFragment(),
                    "RegisterAddressInformationFragment");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        companyNameInput = view.findViewById(R.id.register_input_company_name);
        companyUidInput = view.findViewById(R.id.register_input_company_uid);
        companyFteInput = view.findViewById(R.id.register_input_company_fte);

        Button btnCompanyInformation = view.findViewById(R.id.button_company_information);
        btnCompanyInformation.setOnClickListener(this);

        Startup startup = RegistrationHandler.getStartup();
        companyRevenueInput.setText(startup.getRevenue());
        companyUidInput.setText(startup.getUid());
       // companyFteInput.setText(startup.getNumberOfFte());
        companyFoundingInput.setText(startup.getFoundingYear());
        companyBreakevenInput.setText(startup.getBreakevenYear());
        companyNameInput.setText(startup.getName());
        //TODO: load current markets
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_company_information:
                processInformation();
                break;
            default:
                break;
        }
    }

    /**
     * Process entered information
     */
    private void processInformation() {
        if(companyBreakevenInput.getText().length() == 0 ||
                companyFteInput.getText().length() == 0 ||
        companyNameInput.getText().length() == 0 ||
        companyUidInput.getText().length() == 0 ||
        companyRevenueInput.getText().length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        int breakevenYear = Integer.parseInt(companyBreakevenInput.getText().toString());
        int fte = Integer.parseInt(companyFteInput.getText().toString());
        String companyName = companyNameInput.getText().toString();
        String companyUid = companyUidInput.getText().toString();
        String revenue = companyRevenueInput.getText().toString();
        ArrayList<Long> markets = new ArrayList<>();
        int foundingYear = Integer.parseInt(companyFoundingInput.getText().toString());
        try {
            RegistrationHandler.saveCompanyInformation(breakevenYear, fte, companyName, companyUid,
                    revenue, markets, foundingYear);
            RegistrationHandler.proceed();

            changeFragment(new RegisterAddressInformationFragment(),
                    "RegisterAddressInformationFragment");
        }catch (IOException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Get continents and add them to multiautocompletetextview
     */
    public void getContinents() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "continent",
                        null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<String> continents = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jresponse = response.getJSONObject(i);
                                continents.add(jresponse.getString("name"));
                            }
                            ArrayAdapter adapterContinents = new ArrayAdapter<>(getContext(),
                                    R.layout.item_dropdown_menu, continents.toArray());
                            companyMarketsInput.setAdapter(adapterContinents);
                        } catch (JSONException e) {
                            // TODO: Proper exception handling
                            Log.d("debugMessage", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        ApiRequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
