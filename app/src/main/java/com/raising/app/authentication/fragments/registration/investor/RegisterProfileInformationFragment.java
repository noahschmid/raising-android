package com.raising.app.authentication.fragments.registration.investor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.registration.RegisterSelectTypeFragment;
import com.raising.app.models.Account;
import com.raising.app.models.PrivateProfile;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONObject;

public class RegisterProfileInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText profileCompanyInput, profileWebsiteInput, profileStreetInput,
            profileZipInput, profileCityInput;
    private MultiAutoCompleteTextView profileCountryInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_profile_information,
                container, false);

        // TODO: fetch VALUES_CONTINENTS from backend
        String [] VALUES_COUNTRIES = new String[] {"Switzerland",
                "South Africa", "Peru", "Sweden", "Vietnam"};

        ArrayAdapter adapterCountries = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_COUNTRIES);

        profileCountryInput = view.findViewById(R.id.register_input_profile_countries);
        profileCountryInput.setAdapter(adapterCountries);
        profileCountryInput.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        hideBottomNavigation(true);

        if(RegistrationHandler.hasBeenVisited()) {
            RegistrationHandler.skip();
            changeFragment(new RegisterInvestorMatchingFragment(),
                    "RegisterInvestorMatchingFragment");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileCompanyInput = view.findViewById(R.id.register_input_profile_company);
        profileWebsiteInput = view.findViewById(R.id.register_input_profile_website);
        profileStreetInput = view.findViewById(R.id.register_input_profile_street);
        profileZipInput = view.findViewById(R.id.register_input_profile_zip);
        profileCityInput = view.findViewById(R.id.register_input_profile_city);
        profileCountryInput = view.findViewById(R.id.register_input_profile_countries);

        PrivateProfile profile = RegistrationHandler.getPrivateProfile();
        profileCompanyInput.setText(profile.getCompany());
        profileWebsiteInput.setText(profile.getWebsite());
        profileStreetInput.setText(profile.getStreet());
        profileZipInput.setText(profile.getZipCode());
        profileCityInput.setText(profile.getCity());
        profileCountryInput.setText(profile.getCountry());

        Button btnProfileInformation = view.findViewById(R.id.button_profile_information);
        btnProfileInformation.setOnClickListener(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_profile_information:
                processProfileInformation();
                // changeFragment(new RegisterInvestorMatchingFragment(), "RegisterInvestorMatchingFragment");
                break;
            default:
                break;
        }
    }

    /**
     * Check whether information is valid, then save profile information and
     * switch to next fragment
     */
    private void processProfileInformation() {
        String company = profileCompanyInput.getText().toString();
        String city = profileCityInput.getText().toString();
        String country = profileCountryInput.getText().toString();
        String street = profileStreetInput.getText().toString();
        String website = profileWebsiteInput.getText().toString();
        String zipCode = profileZipInput.getText().toString();

        //TODO: also test country for length == 0
        if(city.length() == 0 || street.length() == 0 || zipCode.length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        try {
            RegistrationHandler.saveProfileInformation(company, city, street, zipCode, website, country);
            RegistrationHandler.proceed();
            changeFragment(new RegisterInvestorMatchingFragment(),
                    "RegisterInvestorMatchingFragment");

        } catch (Exception e) {
            Log.d("debugMessage", e.getMessage());
        }
    }


    /**
     * Get countries and add them to combobox
     */
    public void getCountries() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "public/country",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //add countries to combobox
                        //TODO: add combobox
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        ApiRequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
