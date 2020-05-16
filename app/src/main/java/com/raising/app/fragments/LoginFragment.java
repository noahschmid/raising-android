package com.raising.app.fragments;

import androidx.fragment.app.Fragment;

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
import com.raising.app.fragments.onboarding.OnboardingPost1Fragment;
import com.raising.app.fragments.profile.ContactDataInput;
import com.raising.app.fragments.registration.RegisterLoginInformationFragment;
import com.raising.app.models.NotificationSettings;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;

import com.raising.app.R;
import com.raising.app.fragments.forgotPassword.ForgotPasswordFragment;

import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.SubscriptionHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class LoginFragment extends RaisingFragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";
    private EditText emailInput, passwordInput;

    final private String loginEndpoint = ApiRequestHandler.getDomain() + "account/login";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        hideBottomNavigation(true);
        hideToolbar(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailInput = view.findViewById(R.id.login_input_email);
        passwordInput = view.findViewById(R.id.login_input_password);

        Button btnLogin = view.findViewById(R.id.button_login);
        btnLogin.setOnClickListener(this);
        Button btnRegister = view.findViewById(R.id.button_login_goTo_register);
        btnRegister.setOnClickListener(this);
        Button btnForgot = view.findViewById(R.id.button_login_forgot_password);
        btnForgot.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        hideToolbar(false);
        hideBottomNavigation(false);

        super.onDestroyView();
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
            Log.d(TAG, "login: Login Params" + params);
            viewStateViewModel.startLoading();
            JsonObjectRequest loginRequest = new JsonObjectRequest(
                    loginEndpoint, new JSONObject(params),
                    response -> {
                        Log.d("LoginFragment", "login successful.");
                        try {
                            viewStateViewModel.stopLoading();
                            boolean isStartup = response.getBoolean("startup");
                            if(!isStartup && !response.getBoolean("investor")) {
                                showSimpleDialog(getString(R.string.generic_error_title),
                                        getString(R.string.no_profile_text));
                                return;
                            }

                            if(AccountService.loadContactData(response.getLong("id"))) {
                                AuthenticationHandler.login(email,
                                        response.getString("token"),
                                        response.getLong("id"), isStartup);
                                accountViewModel.loadAccount();
                                settingsViewModel.loadSettings();
                                SubscriptionHandler.loadSubscription();

                                if(isFirstAppLaunch() && !isDisablePostOnboarding()) {
                                    clearBackstackAndReplace(new OnboardingPost1Fragment());
                                } else {
                                    clearBackstackAndReplace(new MatchesFragment());
                                }
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("isStartup", isStartup);
                                bundle.putString("email", email);
                                bundle.putString("token", response.getString("token"));
                                bundle.putLong("id", response.getLong("id"));
                                Fragment fragment = new ContactDataInput();
                                fragment.setArguments(bundle);
                                changeFragment(fragment);
                            }
                        } catch(Exception e) {
                            viewStateViewModel.stopLoading();
                            showSimpleDialog(getString(R.string.generic_error_title),
                                    e.getMessage());
                        }
                    }, error -> {
                        viewStateViewModel.stopLoading();
                        try {
                            if(error.networkResponse.statusCode == 500 ||
                            error.networkResponse.statusCode == 403) {
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
     */
    private void goToRegisterFragment() {
        try {
            if(!RegistrationHandler.isInProgress()) {
                RegistrationHandler.begin();
            }
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
     */
    private void goToForgotFragment() {
        changeFragment(new ForgotPasswordFragment(), "ForgotPasswordEmailFragment");
    }
}
