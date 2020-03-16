package com.raising.app.authentication.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.raising.app.R;
import com.raising.app.RaisingFragment;

public class RegisterStartupMatchingFragment extends RaisingFragment
        implements View.OnClickListener {
    private EditText scopeInput, minSizeInput, maxSizeInput;
    private CheckBox checkVC, checkAngel, checkCVC, checkStrategic, checkClub;
    private CheckBox checkMentor, checkBoard, checkPassive;
    private RadioButton radioPreSeed, radioSeed, radioSeriesA, radioSeriesB, radioSeriesC;
    private RadioButton radioEnergy, radioGeneral, radioHealth, radioHighTech, radioSocial;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_matching,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scopeInput = view.findViewById(R.id.register_input_startup_matching_scope);
        minSizeInput = view.findViewById(R.id.register_input_startup_matching_min_ticket);
        maxSizeInput = view.findViewById(R.id.register_input_startup_matching_max_ticket);

        checkVC = view.findViewById(R.id.register_startup_matching_check_vc);
        checkAngel = view.findViewById(R.id.register_startup_matching_check_angel);
        checkCVC = view.findViewById(R.id.register_startup_matching_check_cvc);
        checkStrategic = view.findViewById(R.id.register_startup_matching_check_strategic);
        checkClub = view.findViewById(R.id.register_startup_matching_check_club);

        checkMentor = view.findViewById(R.id.register_startup_matching_check_mentor);
        checkBoard = view.findViewById(R.id.register_startup_matching_check_board);
        checkPassive = view.findViewById(R.id.register_startup_matching_check_passive);

        radioPreSeed = view.findViewById(R.id.register_startup_matching_radio_preseed);
        radioSeed = view.findViewById(R.id.register_startup_matching_radio_seed);
        radioSeriesA = view.findViewById(R.id.register_startup_matching_radio_seriesA);
        radioSeriesB = view.findViewById(R.id.register_startup_matching_radio_seriesB);
        radioSeriesC = view.findViewById(R.id.register_startup_matching_radio_seriesC);

        radioEnergy = view.findViewById(R.id.register_startup_matching_radio_energy);
        radioGeneral = view.findViewById(R.id.register_startup_matching_radio_general);
        radioHealth = view.findViewById(R.id.register_startup_matching_radio_health);
        radioHighTech = view.findViewById(R.id.register_startup_matching_radio_hightech);
        radioSocial = view.findViewById(R.id.register_startup_matching_radio_social);

        Button btnStartUpMatching = view.findViewById(R.id.button_startup_matching);
        btnStartUpMatching.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View v) {
        switch (getId()) {
            case R.id.button_startup_matching:
                //TODO: insert methods
                break;
            default:
                break;
        }
    }
}
