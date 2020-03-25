package com.raising.app.authentication.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.slider.Slider;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.models.Startup;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterStartupMatchingFragment extends RaisingFragment
        implements View.OnClickListener {
    private Slider ticketSize;
    private LinearLayout investorTypeLayout;
    private LinearLayout supportLayout;
    private RadioGroup investmentPhaseGroup, industryGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_matching,
                container, false);

        if(RegistrationHandler.hasBeenVisited()) {
            RegistrationHandler.skip();
            changeFragment(new RegisterStartupPitchFragment(),
                    "RegisterStartupPitchFragment");
        }

        ticketSize = view.findViewById(R.id.register_startup_matching_ticket_size);
        ticketSize.setValues(
                (float) getResources().getInteger(R.integer.ticket_size_slider_min_value),
                (float) getResources().getInteger(R.integer.ticket_size_slider_starting_value));

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        investorTypeLayout = view.findViewById(R.id.register_startup_investor_type_layout);
        supportLayout = view.findViewById(R.id.register_support_matching_support_layout);
        investmentPhaseGroup = view.findViewById(R.id.register_startup_matching_radio_phase);
        industryGroup = view.findViewById(R.id.register_startup_matching_radio_industry);

        Startup startup = RegistrationHandler.getStartup();
        if(startup.getInvestmentMin() != 0 && startup.getInvestmentMax() != 0)
            ticketSize.setValues((float)startup.getInvestmentMin(), (float)startup.getInvestmentMax());


        getIndustries();
        getInvestmentPhases();
        getInvestorTypes();
        getSupportTypes();

        Button btnStartUpMatching = view.findViewById(R.id.button_startup_matching);
        btnStartUpMatching.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
        Log.d("debugMessage", "onDestroy()");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_startup_matching:
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
        float ticketSizeMin =  ticketSize.getMinimumValue();
        float ticketSizeMax =  ticketSize.getMaximumValue();

        ArrayList<Long> industries = new ArrayList<>();
        for (int i = 0; i < industryGroup.getChildCount(); ++i) {
            View v = industryGroup.getChildAt(i);
            if(((RadioButton)v).isChecked() && ((String)((RadioButton)v).getContentDescription()).length() > 0) {
                industries.add(Long.parseLong((String)((RadioButton)v).getContentDescription()));
            }
        }

        ArrayList<Long> investmentPhases = new ArrayList<>();
        for (int i = 0; i < investmentPhaseGroup.getChildCount(); ++i) {
            View v = investmentPhaseGroup.getChildAt(i);
            if(((RadioButton)v).isChecked() && ((String)((RadioButton)v).getContentDescription()).length() > 0) {
                investmentPhases.add(Long.parseLong((String)((RadioButton)v).getContentDescription()));
            }
        }

        ArrayList<Long> support = new ArrayList<>();
        for (int i = 0; i < supportLayout.getChildCount(); ++i) {
            View v = supportLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                support.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        ArrayList<Long> investorTypes = new ArrayList<>();
        for (int i = 0; i < investorTypeLayout.getChildCount(); ++i) {
            View v = investorTypeLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                investorTypes.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        if(industries.size() == 0 || investmentPhases.size() == 0 || support.size() == 0 ||
        investorTypes.size() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        ArrayList<Long> countries = new ArrayList<>();
        try {
            RegistrationHandler.proceed();
            RegistrationHandler.saveStartupMatchingFragment(ticketSizeMin, ticketSizeMax,
                    investorTypes, investmentPhases, industries, support);

            changeFragment(new RegisterStartupPitchFragment(),
                    "RegisterStartupPitchFragment");
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
                            final CheckBox[] rb = new CheckBox[response.length() + 1];
                            // rg.setOrientation(RadioGroup.HORIZONTAL);

                            for(int i=0; i<response.length(); i++){
                                JSONObject type = response.getJSONObject(i);
                                rb[i]  = new CheckBox(getContext());
                                rb[i].setText(type.getString("name"));
                                rb[i].setContentDescription(String.valueOf(type.getLong("id")));
                                rb[i].setId(View.generateViewId());
                                investorTypeLayout.addView(rb[i]);
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
     * Get support types and add them to checkboxes
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
                            final RadioButton cb[] = new RadioButton[response.length() + 1];
                            for(int i=0; i<response.length(); i++){
                                JSONObject type = response.getJSONObject(i);
                                cb[i] = new RadioButton(getContext());
                                cb[i].setText(type.getString("name"));
                                cb[i].setContentDescription(String.valueOf(type.getLong("id")));
                                cb[i].setId(View.generateViewId());
                                industryGroup.addView(cb[i]);
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
                            final RadioButton cb[] = new RadioButton[response.length() + 1];
                            for(int i=0; i<response.length(); i++){
                                JSONObject type = response.getJSONObject(i);
                                Log.d("debugMessage", type.getString("name"));
                                cb[i] = new RadioButton(getContext());
                                cb[i].setText(type.getString("name"));
                                cb[i].setContentDescription(String.valueOf(type.getLong("id")));
                                cb[i].setId(View.generateViewId());
                                investmentPhaseGroup.addView(cb[i]);
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
}
