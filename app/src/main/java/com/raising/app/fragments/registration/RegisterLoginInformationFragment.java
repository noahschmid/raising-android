package com.raising.app.fragments.registration;

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
import com.raising.app.models.Account;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Function;

public class RegisterLoginInformationFragment extends RaisingFragment implements View.OnClickListener {
    private EditText firstNameInput, lastNameInput, emailInput, passwordInput;
    private boolean load = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_login_information, container, false);
        hideBottomNavigation(true);

        RegistrationHandler.setCancelAllowed(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstNameInput = view.findViewById(R.id.register_input_first_name);
        lastNameInput = view.findViewById(R.id.register_input_last_name);
        emailInput = view.findViewById(R.id.register_input_email);
        passwordInput = view.findViewById(R.id.register_input_password);

        Account account = RegistrationHandler.getAccount();
        firstNameInput.setText(account.getFirstName());
        lastNameInput.setText(account.getLastName());
        emailInput.setText(account.getEmail());
        passwordInput.setText(account.getPassword());


        Button btnLoginInformation = view.findViewById(R.id.button_login_information);
        btnLoginInformation.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_login_information:
                final String firstName = firstNameInput.getText().toString();
                final String lastName = lastNameInput.getText().toString();
                final String email = emailInput.getText().toString();
                final String password = passwordInput.getText().toString();
                processLoginInformation(firstName, lastName, email, password);
                break;
            default:
                break;
        }
    }

    /**
     * Check whether login information is valid and if so save them and move to next fragment
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     */
    private void processLoginInformation(final String firstName, final String lastName, final String email, final String password) {
        if(firstName.length() == 0 || lastName.length() == 0  || email.length() == 0  ||
                password.length() == 0 ) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        if(!email.contains("@")) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_invalid_email));
            return;
        }

        if(password.length() < 6) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_weak_password));
            return;
        }

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);

            Log.d("debugMessage", "sending request...");

            ApiRequestHandler.performPostRequest("account/valid",
                    callback, errorHandler, new JSONObject(params), getContext());
        } catch(Exception e) {
            Log.d("debugMessage", e.getMessage());
            Log.d("debugMessage", e.toString());
            return;
        }
    }

    Function<JSONObject, Void> callback = response -> {
        final String firstName = firstNameInput.getText().toString();
        final String lastName = lastNameInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        try {
            Log.d("debugMessage", "successful response");
            RegistrationHandler.saveLoginInformation(firstName, lastName, email, password);
            changeFragment(new RegisterSelectTypeFragment(),
                    "RegisterSelectTypeFragment");
        } catch (IOException e) {
            // TODO: Display error message
            Log.d("debugMessage", e.getMessage());
        }
        return null;
    };

    Function<VolleyError, Void> errorHandler = error -> {
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
            Log.d("debugMessage", e.toString());
            Log.d("debugMessage", error.toString());
        }
        return null;
    };
}