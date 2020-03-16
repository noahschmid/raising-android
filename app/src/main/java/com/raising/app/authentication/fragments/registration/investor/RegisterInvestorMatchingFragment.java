package com.raising.app.authentication.fragments.registration.investor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.raising.app.R;
import com.raising.app.RaisingFragment;

public class RegisterInvestorMatchingFragment extends RaisingFragment
        implements View.OnClickListener {
    private EditText scopeInput, minSizeInput, maxSizeInput;
    private RadioButton radioVc, radioAngel, radioCvc, radioStrategic, radioClub;
    private RadioButton radioMentor, radioBoard, radioPassive;
    private CheckBox checkPreSeed, checkSeed, checkSeriesA, checkSeriesB, checkSeriesC;
    private CheckBox checkEnergy, checkGeneral, checkHealth, checkHighTech, checkSocial;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_matching,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scopeInput = view.findViewById(R.id.register_input_investor_matching_scope);
        minSizeInput = view.findViewById(R.id.register_input_investor_matching_min_ticket);
        maxSizeInput = view.findViewById(R.id.register_input_investor_matching_max_ticket);

        radioVc = view.findViewById(R.id.register_investor_matching_radio_vc);
        radioAngel = view.findViewById(R.id.register_investor_matching_radio_angel);
        radioCvc = view.findViewById(R.id.register_investor_matching_radio_cvc);
        radioStrategic = view.findViewById(R.id.register_investor_matching_radio_strategic);
        radioClub = view.findViewById(R.id.register_investor_matching_radio_club);

        radioMentor = view.findViewById(R.id.register_investor_matching_radio_mentor);
        radioBoard = view.findViewById(R.id.register_investor_matching_radio_board);
        radioPassive = view.findViewById(R.id.register_investor_matching_radio_passive);

        checkPreSeed = view.findViewById(R.id.register_investor_matching_check_preseed);
        checkSeed = view.findViewById(R.id.register_investor_matching_check_seed);
        checkSeriesA = view.findViewById(R.id.register_investor_matching_check_seriesA);
        checkSeriesB = view.findViewById(R.id.register_investor_matching_check_seriesB);
        checkSeriesC = view.findViewById(R.id.register_investor_matching_check_seriesC);

        checkEnergy = view.findViewById(R.id.register_investor_matching_check_energy);
        checkGeneral = view.findViewById(R.id.register_investor_matching_check_general);
        checkHealth = view.findViewById(R.id.register_investor_matching_check_health);
        checkHighTech = view.findViewById(R.id.register_investor_matching_check_hightech);
        checkSocial = view.findViewById(R.id.register_investor_matching_check_social);

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
        switch (getId()) {
            case R.id.button_investor_matching:
                //TODO: insert methods
                break;
            default:
                break;
        }
    }
}
