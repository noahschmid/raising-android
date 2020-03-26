package com.raising.app.authentication.fragments.registration.investor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.LoginFragment;
import com.raising.app.models.Account;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RegisterInvestorPitchFragment extends RaisingFragment implements View.OnClickListener {
    private EditText sentenceInput, pitchInput;
    private TextInputLayout sentenceLayout, pitchLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_pitch,
                container, false);

        hideBottomNavigation(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sentenceLayout = view.findViewById(R.id.register_investor_pitch_sentence);
        sentenceInput = view.findViewById(R.id.register_input_investor_pitch_sentence);

        pitchLayout = view.findViewById(R.id.register_investor_pitch_pitch);
        pitchInput = view.findViewById(R.id.register_input_investor_pitch_pitch);

        prepareSentenceLayout();
        preparePitchLayout();

        Button btnInvestorPitch = view.findViewById(R.id.button_investor_pitch);
        btnInvestorPitch.setOnClickListener(this);

        if(this.getArguments() != null && this.getArguments().getBoolean("isProfileFragment")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.GONE);
            btnInvestorPitch.setHint(getString(R.string.myProfile_apply_changes));
            btnInvestorPitch.setOnClickListener(v -> popCurrentFragment(this));
        }

        Account account = RegistrationHandler.getAccount();
        pitchInput.setText(account.getPitch());
        sentenceInput.setText(account.getDescription());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_investor_pitch:
                processInputs();
                break;
            default:
                break;
        }
    }

    /**
     * Process inputs, save them and submit registration
     */
    private void processInputs() {
        if(sentenceInput.getText().length() == 0 || pitchInput.getText().length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        try {
            RegistrationHandler.savePitch(sentenceInput.getText().toString(),
                    pitchInput.getText().toString());
            Gson gson = new Gson();
            String investor = gson.toJson(RegistrationHandler.getInvestor());
            ApiRequestHandler.performPostRequest("/investor/register", registerCallback,
                    errorCallback, new JSONObject(investor), getContext());
            Log.d("debugMessage", investor);
        } catch (IOException | JSONException e) {
            //TODO: Proper exception handling
            Log.d("debugMessage", e.getMessage());
        }
    }


    Function<JSONObject, Void> registerCallback = response -> {
        RegistrationHandler.cancel();
        getActivitiesFragmentManager().popBackStack("LoginFragment", 0);
        return null;
    };

    Function<VolleyError, Void> errorCallback = response -> {
        Log.d("debugMessage", response.getStackTrace().toString());
        return null;
    };

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
}
