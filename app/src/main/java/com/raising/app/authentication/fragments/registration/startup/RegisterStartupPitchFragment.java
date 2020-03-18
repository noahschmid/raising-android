package com.raising.app.authentication.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.R;
import com.raising.app.RaisingFragment;

public class RegisterStartupPitchFragment extends RaisingFragment implements View.OnClickListener {
    private EditText sentenceInput, pitchInput;
    private TextInputLayout sentenceLayout, pitchLayout;
    private CheckBox checkSef4Kmu, checkVentureKick, checkInnosuisse, checkUpscaler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_pitch,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareSentenceLayout(view);
        preparePitchLayout(view);

        sentenceLayout = view.findViewById(R.id.register_startup_pitch_sentence);
        sentenceInput = view.findViewById(R.id.register_input_startup_pitch_sentence);

        pitchLayout = view.findViewById(R.id.register_startup_pitch_pitch);
        pitchInput = view.findViewById(R.id.register_input_startup_pitch);

        checkSef4Kmu = view.findViewById(R.id.register_startup_check_sef4kmu);
        checkVentureKick = view.findViewById(R.id.register_startup_check_venture_kick);
        checkInnosuisse = view.findViewById(R.id.register_startup_check_innosuisse);
        checkUpscaler = view.findViewById(R.id.register_startup_check_swissef_upscaler);

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
        switch(getId()) {
            case R.id.button_startup_pitch:
                //TODO: insert method
                break;
            default:
                break;
        }
    }

    /**
     * Call {@link com.raising.app.RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int)}
     *
     * @author Lorenz Caliezi 18.03.2020
     */
    private void prepareSentenceLayout(View view) {
        prepareRestrictedTextLayout(sentenceLayout, sentenceInput, getResources().getInteger(R.integer.pitch_sentence_max_word));
    }

    /**
     * Call {@link com.raising.app.RaisingFragment#prepareRestrictedTextLayout(TextInputLayout, EditText, int)}
     *
     * @author Lorenz Caliezi 18.03.2020
     */
    private void preparePitchLayout(View view) {
        prepareRestrictedTextLayout(pitchLayout, pitchInput, getResources().getInteger(R.integer.pitch_pitch_max_word));
    }
}
