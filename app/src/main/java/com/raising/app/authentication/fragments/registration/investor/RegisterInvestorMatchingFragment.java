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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.slider.Slider;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.models.Investor;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterInvestorMatchingFragment extends RaisingFragment
        implements View.OnClickListener {
    private Slider ticketSize;
    private TextView ticketSizeText;
    private MultiAutoCompleteTextView continentInput, countryInput;
    private LinearLayout industryLayout;
    private LinearLayout investmentPhaseLayout;
    private LinearLayout supportLayout;
    private RadioGroup investorTypeGroup;

    private View fragmentView;
    private int investorType = -1;

    private int minimumTicketSize, maximumTicketSize;
    private ArrayList<String> ticketSizeStrings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_matching,
                container, false);

        hideBottomNavigation(true);

        if(RegistrationHandler.hasBeenVisited()) {
            RegistrationHandler.skip();
            changeFragment(new RegisterInvestorPitchFragment(),
                    "RegisterInvestorPitchFragment");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        investorTypeGroup = view.findViewById(R.id.register_investor_matching_radio_investor);

        continentInput = view.findViewById(R.id.register_input_investor_matching_continents);
        continentInput.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        countryInput = view.findViewById(R.id.register_input_investor_matching_countries);
        countryInput.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        prepareTicketSizeSlider(view);

        Investor investor = RegistrationHandler.getInvestor();

        if(investor.getInvestmentMin() != 0 && investor.getInvestmentMax() != 0)
            ticketSize.setValues((float)investor.getInvestmentMin(), (float)investor.getInvestmentMax());

        industryLayout = view.findViewById(R.id.register_investor_matching_industry_layout);
        investmentPhaseLayout = view.findViewById(R.id.register_investor_matching_phase_layout);
        supportLayout = view.findViewById(R.id.register_investor_matching_support_layout);

        investorTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if (isChecked) {
                    Log.d("debugMessage", "id: " + checkedRadioButton.getContentDescription());
                    investorType = Integer.parseInt((String) checkedRadioButton.getContentDescription());
                } else {
                    investorType = -1;
                }
            }
        });

        getContinents();
        getCountries();
        getInvestorTypes();
        getSupportTypes();
        getIndustries();
        getInvestmentPhases();

        //TODO: restore lists

        Button btnInvestorMatching = view.findViewById(R.id.button_investor_matching);
        btnInvestorMatching.setOnClickListener(this);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_investor_matching:
                processMatchingInformation();
                break;
            default:
                break;
        }
    }

    /**
     * Check if all information is valid and save it
     */
    private void processMatchingInformation() {
        if(investorType == -1 || countryInput.getText().length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        float ticketSizeMin =  minimumTicketSize;
        float ticketSizeMax =  maximumTicketSize;

        ArrayList<Long> industries = new ArrayList<>();
        for (int i = 0; i < industryLayout.getChildCount(); ++i) {
            View v = industryLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                industries.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        ArrayList<Long> investmentPhases = new ArrayList<>();
        for (int i = 0; i < investmentPhaseLayout.getChildCount(); ++i) {
            View v = investmentPhaseLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                investmentPhases.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        ArrayList<Long> support = new ArrayList<>();
        for (int i = 0; i < supportLayout.getChildCount(); ++i) {
            View v = supportLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                support.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        if(industries.size() == 0 || investmentPhases.size() == 0 || support.size() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        ArrayList<Long> countries = new ArrayList<>();
        try {
            RegistrationHandler.saveInvestorMatchingFragment(ticketSizeMin, ticketSizeMax,
                    investorType, investmentPhases, industries, support, countries);
            RegistrationHandler.proceed();
            changeFragment(new RegisterInvestorPitchFragment(),
                    "RegisterInvestorPitchFragment");
        } catch (IOException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Get investor types and add them to radio group
     */
    public void getInvestorTypes() {
        String countries;
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "public/investortype",
                        null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            final RadioButton[] rb = new RadioButton[response.length() + 1];
                            // rg.setOrientation(RadioGroup.HORIZONTAL);

                            for(int i=0; i<response.length(); i++){
                                JSONObject type = response.getJSONObject(i);
                                rb[i]  = new RadioButton(getContext());
                                rb[i].setText(type.getString("name"));
                                rb[i].setContentDescription(String.valueOf(type.getLong("id")));
                                rb[i].setId(View.generateViewId());
                                investorTypeGroup.addView(rb[i]);
                            }
                        } catch (Exception e) {
                            // TODO: Proper exception handling
                            Log.d("debugMessage0", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        ApiRequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Get support types and add them to radio group
     */
    public void getSupportTypes() {
        String countries;
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "public/support",
                        null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            final CheckBox[] rb = new CheckBox[response.length() + 1];

                            for(int i=0; i<response.length(); i++){
                                JSONObject type = response.getJSONObject(i);

                                rb[i]  = new CheckBox(getContext());
                                rb[i].setText(type.getString("name"));
                                rb[i].setContentDescription(String.valueOf(type.getLong("id")));
                                rb[i].setId(View.generateViewId());
                                supportLayout.addView(rb[i]);
                            }
                        } catch (Exception e) {
                            // TODO: Proper exception handling
                            Log.d("debugMessage1", e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        ApiRequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Get industries and add them to checkboxes
     */
    public void getIndustries() {
        String countries;
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "public/industry",
                        null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            final CheckBox cb[] = new CheckBox[response.length() + 1];
                            for(int i=0; i<response.length(); i++){
                                JSONObject type = response.getJSONObject(i);
                                cb[i] = new CheckBox(getContext());
                                cb[i].setText(type.getString("name"));
                                cb[i].setContentDescription(String.valueOf(type.getLong("id")));
                                cb[i].setId(View.generateViewId());
                                industryLayout.addView(cb[i]);
                                //industriesCheckboxes.add(cb[i]);
                            }
                        } catch (Exception e) {
                            // TODO: Proper exception handling
                            Log.d("debugMessage1", e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        ApiRequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Get investment phases and add them to checkboxes
     */
    public void getInvestmentPhases() {
        String countries;
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "public/investmentphase",
                        null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            final CheckBox cb[] = new CheckBox[response.length() + 1];
                            for(int i=0; i<response.length(); i++){
                                JSONObject type = response.getJSONObject(i);
                                Log.d("debugMessage", type.getString("name"));
                                cb[i] = new CheckBox(getContext());
                                cb[i].setText(type.getString("name"));
                                cb[i].setContentDescription(String.valueOf(type.getLong("id")));
                                cb[i].setId(View.generateViewId());
                                investmentPhaseLayout.addView(cb[i]);
                            }
                        } catch (Exception e) {
                            // TODO: Proper exception handling
                            Log.d("debugMessage1", e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        ApiRequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Get continents and add them to combobox
     */
    public void getContinents() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "public/continent",
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
                            continentInput.setAdapter(adapterContinents);
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

    /**
     * Get countries and add them to combobox
     */
    private void getCountries() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "public/country",
                        null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<String> countries = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jresponse = response.getJSONObject(i);
                                countries.add(jresponse.getString("name"));
                            }
                            ArrayAdapter adapterCountries = new ArrayAdapter<>(getContext(),
                                    R.layout.item_dropdown_menu, countries.toArray());
                            countryInput.setAdapter(adapterCountries);
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

    private void prepareTicketSizeSlider(View view) {

        ticketSizeStrings = new ArrayList<String>();
        //TODO: fetch String array of ticket size steps from backend, store in ticketSizeStrings

        ticketSizeText = view.findViewById(R.id.register_investor_matching_ticket_size_text);
        ticketSize = view.findViewById(R.id.register_investor_matching_ticket_size);
        ticketSize.addOnChangeListener(new Slider.OnChangeListener() {

            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ticketSizeText.setText(adaptSliderValues(
                        (int) slider.getMaximumValue(), (int) slider.getMinimumValue()));
            }
        });
        // TODO: if array fetched from backend, replace following line,  ticketSize.setValueTo(ticketSizeString().size());
        ticketSize.setValueTo(getResources().getStringArray(R.array.matching_ticket_sizes_string).length);

        ticketSize.setValues(
                (float) getResources().getInteger(R.integer.ticket_size_slider_min_value),
                (float) getResources().getInteger(R.integer.ticket_size_slider_starting_value));
    }

    private String adaptSliderValues(int maxValue, int minValue) {
        String [] ticketSizes = getResources().getStringArray(R.array.matching_ticket_sizes_string);
        int [] intTicketSizes = getResources().getIntArray(R.array.matching_ticket_sizes);
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Currently selected ticket size: ");
        //TODO: replace with stringBuilder.append(ticketSizeStrings[minValue - 1]);
        stringBuilder.append(ticketSizes[minValue - 1]);
        minimumTicketSize = intTicketSizes[minValue - 1];

        stringBuilder.append(" - ");
        //TODO: replace with stringBuilder.append(ticketSizeStrings[maxValue - 1]);
        stringBuilder.append(ticketSizes[maxValue - 1]);
        maximumTicketSize = intTicketSizes[maxValue - 1];

        return stringBuilder.toString();
    }
}