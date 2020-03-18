package com.raising.app.authentication.fragments.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.MatchesFragment;
import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.util.RegistrationHandler;

public class    RegisterPitchFragment extends RaisingFragment implements View.OnClickListener {
    private EditText sentenceInput, pitchInput;
    private TextInputLayout sentenceLayout, pitchLayout;
    private CheckBox checkSef4Kmu, checkVentureKick, checkInnosuisse, checkUpscaler;

    private final int SENTENCE_WORD_MAXIMUM = 15;
    private final int PITCH_WORD_MAXIMUM = 250;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_pitch,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareSentenceLayout(view);
        preparePitchLayout(view);

        checkSef4Kmu = view.findViewById(R.id.register_check_sef4kmu);
        checkVentureKick = view.findViewById(R.id.register_check_venture_kick);
        checkInnosuisse = view.findViewById(R.id.register_check_innosuisse);
        checkUpscaler = view.findViewById(R.id.register_check_swissef_upscaler);

        Button btnPitch = view.findViewById(R.id.button_pitch);
        btnPitch.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_pitch:
                RegistrationHandler.submit();
                changeFragment(new MatchesFragment(), "MatchesFragment");
                break;
            default:
                break;
        }
    }

    /**
     * Prepares the textInputLayout for the sentence
     * by linking all UI-elements and creating the initial helper text
     * @param view The view in which the layout lies
     *
     * @author Lorenz Caliezi 16.03.2020
     */
    private void prepareSentenceLayout(View view) {
        sentenceLayout = view.findViewById(R.id.register_pitch_sentence);
        sentenceLayout.setHelperText(0 + "/" + SENTENCE_WORD_MAXIMUM);
        sentenceInput = view.findViewById(R.id.register_input_pitch_sentence);
        sentenceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String oneSentence = sentenceInput.getText().toString();
                oneSentence.replace("\n", " ");
                String [] oneSentenceArray = oneSentence.split(" ");
                sentenceLayout.setHelperText(oneSentenceArray.length + "/" + SENTENCE_WORD_MAXIMUM);

                if(oneSentenceArray.length > SENTENCE_WORD_MAXIMUM) {
                    sentenceLayout.setError(getString(R.string.register_error_word_limit_overflow));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Prepares the textInputLayout for the pitch
     * by linking all UI-elements and creating the initial helper text
     * @param view The view in which the layout lies
     *
     * @author Lorenz Caliezi 16.03.2020
     */
    private void preparePitchLayout(View view) {
        pitchLayout = view.findViewById(R.id.register_pitch_pitch);
        pitchLayout.setHelperText(0 + "/" + PITCH_WORD_MAXIMUM);
        pitchInput = view.findViewById(R.id.register_input_pitch);
        pitchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String oneSentence = pitchInput.getText().toString();
                oneSentence.replace("\n", " ");
                String [] oneSentenceArray = oneSentence.split(" ");
                pitchLayout.setHelperText(oneSentenceArray.length + "/" + PITCH_WORD_MAXIMUM );

                if(oneSentenceArray.length > PITCH_WORD_MAXIMUM) {
                    pitchLayout.setError(getString(R.string.register_error_word_limit_overflow));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
