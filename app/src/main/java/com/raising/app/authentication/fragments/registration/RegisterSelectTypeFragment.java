package com.raising.app.authentication.fragments.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.registration.startup.RegisterStartupMatchingFragment;
import com.raising.app.util.RegistrationHandler;

public class RegisterSelectTypeFragment extends RaisingFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_select_type, container, false);
        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSelectStartUp = view.findViewById(R.id.button_register_as_startup);
        btnSelectStartUp.setOnClickListener(this);
        Button btnSelectInvestor = view.findViewById(R.id.button_register_as_investor);
        btnSelectInvestor.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_register_as_startup:
                RegistrationHandler.setAccountType("startup");
                changeFragment(new RegisterLoginInformationFragment(), "RegisterLoginInformationFragment");
                break;
            case R.id.button_register_as_investor:
                RegistrationHandler.setAccountType("investor");
                changeFragment(new RegisterLoginInformationFragment(), "RegisterLoginInformationFragment");
                break;
            default:
                break;
        }
    }
}
