package com.raising.app.fragments.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.investor.RegisterProfileInformationFragment;
import com.raising.app.fragments.registration.startup.RegisterCompanyInformationFragment;
import com.raising.app.util.RegistrationHandler;

import java.io.IOException;

public class RegisterSelectTypeFragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_account_type), true);

        return inflater.inflate(R.layout.fragment_register_select_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find all views
        Button btnSelectStartUp = view.findViewById(R.id.button_register_as_startup);
        btnSelectStartUp.setOnClickListener(v -> registerAsStartup());
        Button btnSelectInvestor = view.findViewById(R.id.button_register_as_investor);
        btnSelectInvestor.setOnClickListener(v -> registerAsInvestor());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    /**
     * Set account type of new account to investor
     */
    private void registerAsInvestor() {
        try {
            RegistrationHandler.setAccountType("investor");
            changeFragment(new RegisterProfileInformationFragment(),
                    "RegisterProfileInformationFragment");
        } catch (IOException e) {
            //TODO: Proper error handling
            Log.d("debugMessage", e.getLocalizedMessage());
        }
    }

    /**
     * Set account type of new account to startup
     */
    private void registerAsStartup() {
        try {
            RegistrationHandler.setAccountType("startup");
            changeFragment(new RegisterCompanyInformationFragment(),
                    "RegisterCompanyInformationFragment");
        } catch (IOException e) {
            Log.d("debugMessage", e.getLocalizedMessage());
        }
    }
}
