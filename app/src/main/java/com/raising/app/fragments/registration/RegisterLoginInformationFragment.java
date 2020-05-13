package com.raising.app.fragments.registration;

import android.app.AlertDialog;
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
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.profile.MyProfileFragment;
import com.raising.app.models.Account;
import com.raising.app.models.Investor;
import com.raising.app.models.Startup;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.RaisingTextWatcher;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Function;

import lombok.Getter;

public class RegisterLoginInformationFragment extends RaisingFragment implements RaisingTextWatcher {
    private final String TAG = "RegisterLoginInformationFragment";
    private EditText firstNameInput, lastNameInput, emailInput, passwordInput;
    private Button btnLoginInformation;
    private boolean load = false;
    private boolean editMode = false;
    private Account account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_login_information, container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_login_information), true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //define input views and button
        firstNameInput = view.findViewById(R.id.register_input_first_name);
        lastNameInput = view.findViewById(R.id.register_input_last_name);
        emailInput = view.findViewById(R.id.register_input_email);
        passwordInput = view.findViewById(R.id.register_input_password);

        btnLoginInformation = view.findViewById(R.id.button_login_information);
        btnLoginInformation.setOnClickListener(v -> processLoginInformation());

        //adjust fragment if this fragment is used for profile
        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            btnLoginInformation.setHint(getString(R.string.myProfile_apply_changes));
            btnLoginInformation.setVisibility(View.INVISIBLE);
            editMode = true;
            hideBottomNavigation(false);
            account = currentAccount;
            account.setEmail(AuthenticationHandler.getEmail());
            view.findViewById(R.id.register_password).setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onViewCreated: getting account from registration handler");
            account = RegistrationHandler.getAccount();
        }

        // fill text inputs with existing user data
        firstNameInput.setText(account.getFirstName());
        lastNameInput.setText(account.getLastName());
        emailInput.setText(account.getEmail());
        passwordInput.setText(account.getPassword());

        // if editmode, add text watchers after initial filling with users data
        if(editMode) {
            firstNameInput.addTextChangedListener(this);
            lastNameInput.addTextChangedListener(this);
            emailInput.addTextChangedListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        hideBottomNavigation(false);

    }

    @Override
    public void onAccountUpdated() {
        popCurrentFragment(this);
        accountViewModel.updateCompleted();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d(TAG, "onTextChanged: Text has changed");
        btnLoginInformation.setVisibility(View.VISIBLE);
    }

    /**
     * Check whether login information is valid and if so save them and move to next fragment
     */
    private void processLoginInformation() {
        final String firstName = firstNameInput.getText().toString();
        final String lastName = lastNameInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if(firstName.length() == 0 || lastName.length() == 0  || email.length() == 0  ||
                (password.length() == 0 && !editMode)) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        if(firstName.length() > getResources().getInteger(R.integer.raisingMaximumNameLength) || lastName.length() > getResources().getInteger(R.integer.raisingMaximumNameLength)) {
            showSimpleDialog(getString(R.string.register_dialog_title), getString(R.string.register_dialog_long_name));
            return;
        }

        if(!email.contains("@")) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_invalid_email));
            return;
        }

        if(password.length() < 6 && !editMode) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_weak_password));
            return;
        }

        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmail(email);

        try {
            if(!email.equals(AuthenticationHandler.getEmail()) ||
                    !AuthenticationHandler.isLoggedIn()) {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);

                viewStateViewModel.startLoading();

                ApiRequestHandler.performPostRequest("account/valid",
                        callback, errorHandler, new JSONObject(params));
            } else {
                accountViewModel.update(account);
            }
        } catch(Exception e) {
            Log.e("RegisterLoginInformation", "" + e.getMessage());
            return;
        }
    }

    Function<JSONObject, Void> callback = response -> {
        final String firstName = firstNameInput.getText().toString();
        final String lastName = lastNameInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        viewStateViewModel.stopLoading();

        try {
            if(!editMode) {
                RegistrationHandler.saveLoginInformation(firstName, lastName, email, password);
                changeFragment(new RegisterSelectTypeFragment(),
                        "RegisterSelectTypeFragment");
            } else {
                account.setFirstName(firstName);
                account.setLastName(lastName);
                account.setEmail(email);
                accountViewModel.update(account);
            }
        } catch (IOException e) {
            Log.e("RegisterLoginInformation","Error while saving login informaion: "
                    + e.getMessage());
        }
        return null;
    };

    Function<VolleyError, Void> errorHandler = error -> {
        viewStateViewModel.stopLoading();
        try {
            if(error.networkResponse.statusCode == 400) {
                showSimpleDialog(
                        getString(R.string.register_dialog_title),
                        getString(R.string.register_dialog_text_email_registered)
                );
            }
        } catch (NullPointerException e) {
            showSimpleDialog(
                    getString(R.string.login_dialog_server_error_title),
                    getString(R.string.login_dialog_server_error_text)
            );
            Log.e("RegisterLoginInformation", "" + e.toString());
        }
        return null;
    };
}
