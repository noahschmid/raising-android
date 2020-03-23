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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.models.PrivateProfile;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterAddressInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText addressCityInput, addressZipInput,
            addressStreetInput, addressWebsiteInput;
    private AutoCompleteTextView addressCountryInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_address_information, container, false);

        addressCountryInput = view.findViewById(R.id.register_input_address_country);
        getCountries();
        hideBottomNavigation(true);

        if(RegistrationHandler.hasBeenVisited()) {
            RegistrationHandler.skip();
            changeFragment(new RegisterStartupMatchingFragment(),
                    "RegisterStartupMatchingFragment");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addressCityInput = view.findViewById(R.id.register_input_address_city);
        addressZipInput = view.findViewById(R.id.register_input_address_zip);
        addressStreetInput = view.findViewById(R.id.register_input_address_street);
        addressWebsiteInput = view.findViewById(R.id.register_input_address_website);
        addressCountryInput = view.findViewById(R.id.register_input_profile_countries);

        PrivateProfile profile = RegistrationHandler.getPrivateProfile();
        addressCityInput.setText(profile.getCity());
        addressWebsiteInput.setText(profile.getWebsite());
        addressStreetInput.setText(profile.getStreet());
        addressZipInput.setText(profile.getZipCode());
        addressCountryInput.setText(profile.getCountry());

        Button btnAddressInformation = view.findViewById(R.id.button_address_information);
        btnAddressInformation.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_address_information:
                processInputs();
                break;
            default:
                break;
        }
    }

    /**
     * Process given inputs and go to next fragment if inputs are valid
     */
    private void processInputs() {
        if(addressCityInput.getText().length() == 0 || addressCountryInput.getText().length() == 0 ||
        addressStreetInput.getText().length() == 0 || addressZipInput.getText().length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        try {
            String street = addressStreetInput.getText().toString();
            String zipCode = addressZipInput.getText().toString();
            String country = addressCountryInput.getText().toString();
            String website = addressWebsiteInput.getText().toString();
            String city = addressCityInput.getText().toString();
            RegistrationHandler.saveProfileInformation("", city, street, zipCode,
                    website, country);
            RegistrationHandler.proceed();
            changeFragment(new RegisterStartupMatchingFragment());
        } catch (IOException e) {
            //TODO: proper error handling
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Get countries and add them to multiautocompletetextview
     */
    public void getCountries() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "public/country",
                        null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<String> continents = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jresponse = response.getJSONObject(i);
                                continents.add(jresponse.getString("name"));
                            }
                            ArrayAdapter adapterCountries = new ArrayAdapter<>(getContext(),
                                    R.layout.item_dropdown_menu, continents.toArray());
                            addressCountryInput.setAdapter(adapterCountries);
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
