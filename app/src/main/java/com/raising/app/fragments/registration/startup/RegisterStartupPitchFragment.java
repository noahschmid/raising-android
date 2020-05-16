package com.raising.app.fragments.registration.startup;

import android.annotation.SuppressLint;
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
import com.raising.app.models.Startup;
import com.raising.app.util.RaisingTextWatcher;
import com.raising.app.util.RegistrationHandler;

import java.io.IOException;

public class RegisterStartupPitchFragment extends RaisingFragment implements RaisingTextWatcher {
    private TextInputLayout sentenceLayout, pitchLayout;
    private EditText sentenceInput, pitchInput;
    private Button btnStartupPitch;
    private Startup startup;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_pitch,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_pitch), true);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //define input views and button
        sentenceLayout = view.findViewById(R.id.register_startup_pitch_sentence);
        sentenceInput = view.findViewById(R.id.register_input_startup_pitch_sentence);

        pitchLayout = view.findViewById(R.id.register_startup_pitch_pitch);
        pitchInput = view.findViewById(R.id.register_input_startup_pitch);

        btnStartupPitch = view.findViewById(R.id.button_startup_pitch);
        btnStartupPitch.setOnClickListener(v -> processInputs());


        //adjust fragment if this fragment is used for profile
        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            btnStartupPitch.setHint(getString(R.string.myProfile_apply_changes));
            btnStartupPitch.setEnabled(false);
            startup = (Startup) accountViewModel.getAccount().getValue();
            editMode = true;
            hideBottomNavigation(false);
        } else {
            startup = RegistrationHandler.getStartup();
        }

        pitchInput.setText(startup.getPitch());
        sentenceInput.setText(startup.getDescription());

        prepareSentenceLayout(startup.getDescription());
        preparePitchLayout(startup.getPitch());

        pitchInput.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_SCROLL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
            }
            return false;
        });

        // if editmode, add text watchers after initial filling with users data
        if(editMode) {
            sentenceInput.addTextChangedListener(this);
            pitchInput.addTextChangedListener(this);
        }
    }


    @Override
    public void onDestroyView() {
        hideBottomNavigation(false);
        super.onDestroyView();
    }

    @Override
    protected void onAccountUpdated() {
        resetTab();
        popFragment(this);
        accountViewModel.updateCompleted();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnStartupPitch.setEnabled(true);
    }

    /**
     * Process given inputs
     */
    private void processInputs() {
        String pitch = pitchInput.getText().toString();
        String description = sentenceInput.getText().toString();

        if (pitch.length() == 0 || description.length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        // check if sentence is too long
        if (splitStringIntoWords(description).length
                > getResources().getInteger(R.integer.pitch_sentence_max_word)) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_pitch_error_long_sentence));
            return;
        }

        // check if pitch is too long
        if (splitStringIntoWords(pitch).length
                > getResources().getInteger(R.integer.pitch_pitch_max_word)) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_pitch_error_long_pitch));
            return;
        }

        startup.setPitch(pitch);
        startup.setDescription(description);

        try {
            if (!editMode) {
                RegistrationHandler.saveStartup(startup);
                changeFragment(new RegisterStartupLabelsFragment());
            } else {
                accountViewModel.update(startup);
            }

        } catch (IOException e) {
            Log.e("RegisterStartupPitch", "Error while saving startup pitch");
        }
    }

    /**
     * Call {@link RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int, String)}
     *
     * @param currentText The text, that is currently in this text view
     */
    private void prepareSentenceLayout(String currentText) {
        prepareRestrictedTextLayout(sentenceLayout, sentenceInput, getResources().getInteger(R.integer.pitch_sentence_max_word), currentText);
    }

    /**
     * Call {@link RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int, String)}
     *
     * @param currentText The text, that is currently in this text view
     */
    private void preparePitchLayout(String currentText) {
        prepareRestrictedTextLayout(pitchLayout, pitchInput, getResources().getInteger(R.integer.pitch_pitch_max_word), currentText);
    }
}
