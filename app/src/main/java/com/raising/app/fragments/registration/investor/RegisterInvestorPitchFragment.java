package com.raising.app.fragments.registration.investor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Investor;
import com.raising.app.util.RaisingTextWatcher;
import com.raising.app.util.RegistrationHandler;

import java.io.IOException;

public class RegisterInvestorPitchFragment extends RaisingFragment implements View.OnClickListener, RaisingTextWatcher {
    private EditText sentenceInput, pitchInput;
    private TextInputLayout sentenceLayout, pitchLayout;
    private Button btnInvestorPitch;
    private Investor investor;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_pitch,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_pitch), true);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //define input views and button
        sentenceLayout = view.findViewById(R.id.register_investor_pitch_sentence);
        sentenceInput = view.findViewById(R.id.register_input_investor_pitch_sentence);

        pitchLayout = view.findViewById(R.id.register_investor_pitch_pitch);
        pitchInput = view.findViewById(R.id.register_input_investor_pitch_pitch);

        btnInvestorPitch = view.findViewById(R.id.button_investor_pitch);
        btnInvestorPitch.setOnClickListener(this);

        //adjust fragment if this fragment is used for profile
        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnInvestorPitch.setHint(getString(R.string.myProfile_apply_changes));
            btnInvestorPitch.setVisibility(View.INVISIBLE);
            investor = (Investor) accountViewModel.getAccount().getValue();
            editMode = true;
            hideBottomNavigation(false);
        } else {
            investor = RegistrationHandler.getInvestor();
        }

        // fill views with users existing data
        pitchInput.setText(investor.getPitch());
        sentenceInput.setText(investor.getDescription());

        prepareSentenceLayout(investor.getDescription());
        preparePitchLayout(investor.getPitch());

        pitchInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK ){
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
                return false;
            }
        });

        // if editmode, add text watchers after initial filling with users data
        if(editMode) {
            sentenceInput.addTextChangedListener(this);
            pitchInput.addTextChangedListener(this);
        }
    }

    @Override
    protected void onAccountUpdated() {
        resetTab();
        popFragment(this);
        accountViewModel.updateCompleted();
    }

    @Override
    public void onDestroyView() {
        hideBottomNavigation(false);
        super.onDestroyView();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnInvestorPitch.setVisibility(View.VISIBLE);
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

        // check if sentence is too long
        if(splitStringIntoWords(sentenceInput.getText().toString()).length
                > getResources().getInteger(R.integer.pitch_sentence_max_word)) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_pitch_error_long_sentence));
            return;
        }

        // check if pitch is too long
        if(splitStringIntoWords(pitchInput.getText().toString()).length
                > getResources().getInteger(R.integer.pitch_pitch_max_word)) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_pitch_error_long_pitch));
            return;
        }

        investor.setDescription(sentenceInput.getText().toString());
        investor.setPitch(pitchInput.getText().toString());

        try {
            if(editMode) {
                accountViewModel.update(investor);
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
     * Call {@link RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int, String)}
     *
     * @param currentText The text, that is currently in this text view
     * @author Lorenz Caliezi 18.03.2020
     */
    private void prepareSentenceLayout(String currentText) {
        prepareRestrictedTextLayout(sentenceLayout, sentenceInput, getResources().getInteger(R.integer.pitch_sentence_max_word), currentText);
    }

    /**
     * Call {@link RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int, String)}
     *
     * @param currentText The text, that is currently in this text view
     * @author Lorenz Caliezi 18.03.2020
     */
    private void preparePitchLayout(String currentText) {
        prepareRestrictedTextLayout(pitchLayout, pitchInput, getResources().getInteger(R.integer.pitch_pitch_max_word), currentText);
    }
}
