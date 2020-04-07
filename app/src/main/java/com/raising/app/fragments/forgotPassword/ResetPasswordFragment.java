package com.raising.app.fragments.forgotPassword;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.GenericRequest;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;

import org.json.JSONObject;

import java.util.HashMap;

public class ResetPasswordFragment extends RaisingFragment implements View.OnClickListener {
    private EditText codeInput;
    private EditText passwordInput;

    private final String resetEndpoint = ApiRequestHandler.getDomain() + "account/reset";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        codeInput = view.findViewById(R.id.forgot_input_reset_code);
        passwordInput = view.findViewById(R.id.forgot_input_new_password);

        hideBottomNavigation(true);

        Button loginWithToken = view.findViewById(R.id.button_forgot_loginWithToken);
        loginWithToken.setOnClickListener(this);

        //showSimpleDialog(getString(R.string.forgot_dialog_title), getString(R.string.forgot_dialog_text));

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
            case R.id.button_forgot_loginWithToken:
                preparePasswordReset();
                break;
            default:
                break;
        }
    }

    /**
     * Send password reset request to backend and process response
     * @param code the reset code
     */
    private void resetPassword(String code, String password) {
        if(code.length() == 0 || password.length() == 0) {
            showSimpleDialog(getString(R.string.reset_dialog_title_no_input),
                    getString(R.string.reset_dialog_text_no_input));
            return;
        }
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("code", code);
            params.put("password", password);
            GenericRequest loginRequest = new GenericRequest(
                    resetEndpoint, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                changeFragment(new MatchesFragment(), "LoginFragment");
                            } catch(Exception e) {
                                showSimpleDialog(getString(R.string.generic_error_title),
                                        e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showSimpleDialog(
                            getString(R.string.generic_error_title),
                            getString(R.string.generic_error_text)
                    );
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
     * Simple helper method, that prepares {@link #preparePasswordReset()} (String)}.
     *
     * @author Lorenz Caliezi 09.03.2020
     */
    private void preparePasswordReset() {
        String code = codeInput.getText().toString();
        String password = passwordInput.getText().toString();

        resetPassword(code, password);
    }
}
