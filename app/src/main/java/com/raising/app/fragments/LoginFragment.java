package com.raising.app.fragments;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.raising.app.fragments.registration.RegisterLoginInformationFragment;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;

import com.raising.app.R;
import com.raising.app.fragments.forgotPassword.ForgotPasswordFragment;

import com.raising.app.util.RegistrationHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


public class LoginFragment extends RaisingFragment implements View.OnClickListener {
    private EditText emailInput, passwordInput;

    final private String loginEndpoint = ApiRequestHandler.getDomain() + "account/login";

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        hideBottomNavigation(true);

        // if registration was in progress but user pressed back button, cancel it
        if (RegistrationHandler.isInProgress(getContext())) {
            if(RegistrationHandler.shouldCancel())
                RegistrationHandler.cancel();
            else
                changeFragment(new RegisterLoginInformationFragment(),
                        "RegisterLoginInformationFragment");
        }
        emailInput = view.findViewById(R.id.login_input_email);
        passwordInput = view.findViewById(R.id.login_input_password);

        Button btnLogin = view.findViewById(R.id.button_login);
        btnLogin.setOnClickListener(this);
        Button btnRegister = view.findViewById(R.id.button_login_goTo_register);
        btnRegister.setOnClickListener(this);
        Button btnForgot = view.findViewById(R.id.button_login_forgot_password);
        btnForgot.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                prepareLogin();
                break;
            case R.id.button_login_goTo_register:
                goToRegisterFragment();
                break;
            case R.id.button_login_forgot_password:
                goToForgotFragment();
                break;
            default:
                break;
        }
    }


    /**
     * Simple helper function that retrieves the users input from the layout
     *      and then calls {@link #login(String, String)}.
     * Enables easier testing, since you can give login() some parameters.
     *
     * @author Lorenz Caliezi 03.03.2020
     * @version 1.0
     */
    private void prepareLogin() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        login(email, password);
    }

    /**
     * Send login request to backend and process response
     * @param email
     * @param password
     */
    private void login(String email, String password) {
        if(email.length() == 0 || password.length() == 0) {
            showSimpleDialog(getString(R.string.login_dialog_title),
                    getString(R.string.login_dialog_text_empty_credentials));
            return;
        }
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);
            JsonObjectRequest loginRequest = new JsonObjectRequest(
                    loginEndpoint, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                AuthenticationHandler.login(response.getString("token"),
                                        response.getLong("id"), getContext());
                                // getActivitiesFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                changeFragment(new MatchesFragment(), "MatchesFragment");
                            } catch(Exception e) {
                                showSimpleDialog(getString(R.string.generic_error_title),
                                        e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        if(error.networkResponse.statusCode == 403) {
                            showSimpleDialog(
                                    getString(R.string.login_dialog_title),
                                    getString(R.string.login_dialog_text_403)
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
                }
            }){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };
            loginRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

            ApiRequestHandler.getInstance(getContext()).addToRequestQueue(loginRequest);
        } catch(Exception e) {
            Log.d("debugMessage", e.getMessage());
            Log.d("debugMessage", e.toString());
            return;
        }
    }

    /**
     * Change to the RegisterFragment, if user wants to register, not log in
     *
     * @author Lorenz Caliezi 02.03.2020
     */
    private void goToRegisterFragment() {
        try {
            RegistrationHandler.begin();
            changeFragment(new RegisterLoginInformationFragment(),
                    "RegisterLoginInformationFragment");
            Log.d("debugMessage", "success");
        } catch (IOException e) {
            //TODO: Display error message
            Log.d("debugMessage", e.getStackTrace().toString());
        }
    }

    /**
     * Change to ForgotPasswordEmailFragment, if the user wants to get a new password
     *
     * @author Lorenz Caliezi 09.03.2020
     */
    private void goToForgotFragment() {
        changeFragment(new ForgotPasswordFragment(), "ForgotPasswordEmailFragment");
    }
}