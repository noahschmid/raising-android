package com.raising.app.authentication.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.models.Startup;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterStartupPitchFragment extends RaisingFragment implements View.OnClickListener {
    private TextInputLayout sentenceLayout, pitchLayout;
    private EditText sentenceInput, pitchInput;
    private LinearLayout labelsLayout;

    private CheckBox checkSef4Kmu, checkVentureKick, checkInnosuisse, checkUpscaler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_pitch,
                container, false);

        hideBottomNavigation(true);

        if(RegistrationHandler.hasBeenVisited()) {
            RegistrationHandler.skip();
            changeFragment(new RegisterFinancialRequirementsFragment(),
                    "RegisterFinancialRequirementsFragment");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sentenceLayout = view.findViewById(R.id.register_startup_pitch_sentence);
        sentenceInput = view.findViewById(R.id.register_input_startup_pitch_sentence);

        pitchLayout = view.findViewById(R.id.register_startup_pitch_pitch);
        pitchInput = view.findViewById(R.id.register_input_startup_pitch);

        labelsLayout = view.findViewById(R.id.register_startup_pitch_labels);

        Startup startup = RegistrationHandler.getStartup();
        pitchInput.setText(startup.getPitch());
        sentenceInput.setText(startup.getDescription());

        prepareSentenceLayout();
        preparePitchLayout();

        getLabels();

        Button btnStartupPitch = view.findViewById(R.id.button_startup_pitch);
        btnStartupPitch.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_startup_pitch:
                processInputs();
                break;
            default:
                break;
        }
    }

    /**
     * Process given inputs
     */
    private void processInputs() {
        String pitch = pitchInput.getText().toString();
        String description = sentenceInput.getText().toString();

        if(pitch.length() == 0 || description.length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        ArrayList<Long> labels = new ArrayList<>();
        for (int i = 0; i < labelsLayout.getChildCount(); ++i) {
            View v = labelsLayout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                labels.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        try {
            RegistrationHandler.proceed();
            RegistrationHandler.saveStartupPitch(pitch, description, labels);
            changeFragment(new RegisterFinancialRequirementsFragment(),
                    "RegisterFinancialRequirementsFragment");
        } catch(IOException e) {

        }

    }

    /**
     * Call {@link com.raising.app.RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int)}
     *
     * @author Lorenz Caliezi 18.03.2020
     */
    private void prepareSentenceLayout() {
        prepareRestrictedTextLayout(sentenceLayout, sentenceInput, getResources().getInteger(R.integer.pitch_sentence_max_word));
    }

    /**
     * Call {@link com.raising.app.RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int)}
     *
     * @author Lorenz Caliezi 18.03.2020
     */
    private void preparePitchLayout() {
        prepareRestrictedTextLayout(pitchLayout, pitchInput, getResources().getInteger(R.integer.pitch_pitch_max_word));
    }

    /**
     * Get support types and add them to checkboxes
     */
    public void getLabels() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, ApiRequestHandler.getDomain() + "label",
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
                                labelsLayout.addView(rb[i]);
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
