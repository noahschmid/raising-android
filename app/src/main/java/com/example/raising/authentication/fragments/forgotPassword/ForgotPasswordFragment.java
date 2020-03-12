package com.example.raising.authentication.fragments.forgotPassword;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.raising.ApiRequestHandler;
import com.example.raising.GenericRequest;
import com.example.raising.R;
import com.example.raising.RaisingFragment;

import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPasswordFragment extends RaisingFragment implements View.OnClickListener{
    private EditText emailInput;
    private final String forgotEndpoint = "https://33383.hostserv.eu:8080/account/forgot";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        emailInput = view.findViewById(R.id.editText_forgot_email);

        hideBottomNavigation(true);

        Button btnSend = view.findViewById(R.id.button_forgot_reset);
        btnSend.setOnClickListener(this);

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
            case R.id.button_forgot_reset:
                prepareForgotPassword();
                break;
            default:
                break;
        }
    }

    /**
     * Send password forgot request to backend and process response
     * @param email the email the account was registered with
     */
    private void forgotPassword(String email) {
        if(email.length() == 0) {
            showSimpleDialog(getString(R.string.forgot_dialog_title_no_input),
                    getString(R.string.forgot_dialog_text_no_input));
            return;
        }

        Log.d("debugMessage", email);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            GenericRequest loginRequest = new GenericRequest(
                    forgotEndpoint, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            changeFragment(new ResetPasswordFragment(), "ResetPasswordFragment");
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
     * Simple helper method, that prepares {@link #forgotPassword (String)}.
     *
     * @author Lorenz Caliezi 09.03.2020
     */
    private void prepareForgotPassword() {
        String email = emailInput.getText().toString();
        forgotPassword(email);
    }
}
