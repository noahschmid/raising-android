package com.raising.app.authentication.fragments.registration.investor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;

import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.registration.startup.RegisterAddressInformationFragment;

public class RegisterInvestorMatchingFragment extends RaisingFragment
        implements View.OnClickListener {
    private EditText minSizeInput, maxSizeInput;
    private MultiAutoCompleteTextView continentInput, countryInput;
    private RadioButton radioVc, radioAngel, radioCvc, radioStrategic, radioClub;
    private RadioButton radioMentor, radioBoard, radioPassive;
    private CheckBox checkPreSeed, checkSeed, checkSeriesA, checkSeriesB, checkSeriesC;
    private CheckBox checkEnergy, checkGeneral, checkHealth, checkHighTech, checkSocial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_matching,
                container, false);

        // TODO: fetch VALUES_CONTINENTS from backend
        String [] VALUES_CONTINENTS = new String[] {"Asia", "Europe", "Africa", "Australia",
                "North America", "South America"};
        String [] VALUES_COUNTRIES = new String[] {"Asia", "Europe", "Africa", "Australia",
                "North America", "South America"};

        ArrayAdapter adapterContinents = new ArrayAdapter<>( getContext(),
                R.layout.dropdown_menu_items, VALUES_CONTINENTS);

        ArrayAdapter adapterCountries = new ArrayAdapter<>( getContext(),
                R.layout.dropdown_menu_items, VALUES_COUNTRIES);

        continentInput = view.findViewById(R.id.register_input_investor_matching_continents);
        continentInput.setAdapter(adapterContinents);
        continentInput.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        countryInput = view.findViewById(R.id.register_input_investor_matching_countries);
        continentInput.setAdapter(adapterCountries);
        continentInput.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        switch (v.getId()) {
            case R.id.button_investor_matching:
                changeFragment(new RegisterAddressInformationFragment(),
                        "RegisterAddressInformationFragment");
                break;
            default:
                break;
        }
    }
}
