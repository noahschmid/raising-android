package com.raising.app.fragments.forgotPassword;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.onboarding.OnboardingPost1Fragment;
import com.raising.app.fragments.profile.ContactDataInput;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.GenericRequest;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;

import org.json.JSONObject;

import java.util.HashMap;

public class ResetPasswordFragment extends RaisingFragment {
    private EditText codeInput;
    private EditText passwordInput;
    private String email;

    private final String resetEndpoint = ApiRequestHandler.getDomain() + "account/reset";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        codeInput = view.findViewById(R.id.forgot_input_reset_code);
        passwordInput = view.findViewById(R.id.forgot_input_new_password);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_reset_password), true);

        Button loginWithToken = view.findViewById(R.id.button_forgot_loginWithToken);
        loginWithToken.setOnClickListener(v -> resetPassword());

        if (getArguments().getString("email") != null) {
            email = getArguments().getString("email");
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    /**
     * Send password reset request to backend and process response
     */
    private void resetPassword() {
        String code = codeInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (code.length() == 0 || password.length() == 0) {
            showSimpleDialog(getString(R.string.simple_dialog_invalid_input_title),
                    getString(R.string.reset_dialog_text_no_input));
            return;
        }
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("code", code);
            params.put("password", password);
            viewStateViewModel.startLoading();
            GenericRequest loginRequest = new GenericRequest(
                    resetEndpoint, new JSONObject(params),
                    response -> {
                        viewStateViewModel.stopLoading();
                        showSimpleDialog(getString(R.string.reset_password_success_title), getString(R.string.reset_password_success_text));
                        changeFragment(new LoginFragment());
                        /*
                        try {
                            if(AccountService.loadContactData(response.getLong("id"))) {
                                AuthenticationHandler.login(email,
                                        response.getString("token"),
                                        response.getLong("id"), isStartup);
                                accountViewModel.loadAccount();

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
                            dismissLoadingPanel();
                        }
                        */
                    }, error -> {
                        viewStateViewModel.stopLoading();
                        Log.d("debugMessage", error.toString());
                        if (error.networkResponse.statusCode == 500) {
                            showSimpleDialog(getString(R.string.generic_error_title),
                                    getString(R.string.wrong_reset_code));
                        } else {
                            showGenericError();
                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };
            loginRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

            ApiRequestHandler.getInstance(getContext()).addToRequestQueue(loginRequest);
        } catch (Exception e) {
            Log.d("debugMessage", e.getMessage());
            Log.d("debugMessage", e.toString());
            return;
        }
    }
}
