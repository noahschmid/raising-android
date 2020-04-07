package com.raising.app.fragments.registration.investor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.models.Account;
import com.raising.app.models.Investor;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Function;

public class RegisterInvestorPitchFragment extends RaisingFragment implements View.OnClickListener {
    private EditText sentenceInput, pitchInput;
    private TextInputLayout sentenceLayout, pitchLayout;
    private Investor investor;
    private boolean editMode = false;

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

        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.GONE);
            btnInvestorPitch.setHint(getString(R.string.myProfile_apply_changes));
            investor = (Investor) AccountService.getAccount();
            editMode = true;
        } else {
            investor = RegistrationHandler.getInvestor();
        }
        pitchInput.setText(investor.getPitch());
        sentenceInput.setText(investor.getDescription());
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

        investor.setDescription(sentenceInput.getText().toString());
        investor.setPitch(pitchInput.getText().toString());

        try {
            if(editMode) {
                AccountService.updateAccount(investor, v -> {
                    popCurrentFragment(this);
                    return null;
                });
            } else {
                RegistrationHandler.saveInvestor(investor);
                changeFragment(new RegisterInvestorImagesFragment());
            }
        } catch (IOException e) {
            Log.d("RegisterInvestorPitchFragment", "Error in processInputs: " +
                    e.getMessage());
        }
    }

    /**
     * Call {@link RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int)}
     *
     * @author Lorenz Caliezi 18.03.2020
     */
    private void prepareSentenceLayout() {
        prepareRestrictedTextLayout(sentenceLayout, sentenceInput, getResources().getInteger(R.integer.pitch_sentence_max_word));
    }

    /**
     * Call {@link RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int)}
     *
     * @author Lorenz Caliezi 18.03.2020
     */
    private void preparePitchLayout() {
        prepareRestrictedTextLayout(pitchLayout, pitchInput, getResources().getInteger(R.integer.pitch_pitch_max_word));
    }
}
