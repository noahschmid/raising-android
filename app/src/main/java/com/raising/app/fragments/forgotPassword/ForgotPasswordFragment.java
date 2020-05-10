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
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.GenericRequest;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;

import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPasswordFragment extends RaisingFragment {
    private EditText emailInput;
    private final String forgotEndpoint = ApiRequestHandler.getDomain() + "account/forgot";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        emailInput = view.findViewById(R.id.forgot_input_email);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_forgot_email), true);

        Button btnSend = view.findViewById(R.id.button_forgot_reset);
        btnSend.setOnClickListener(v -> forgotPassword());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    /**
     * Send password forgot request to backend and process response
     */
    private void forgotPassword() {
        String email = emailInput.getText().toString().trim();
        if (email.length() == 0) {
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
                    response -> {
                        Fragment fragment = new ResetPasswordFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("email", email);
                        fragment.setArguments(bundle);

                        changeFragment(fragment);
                    }, error -> {
                        Log.d("debugMessage", error.toString());
                if (error.networkResponse.statusCode == 500) {
                    showSimpleDialog(
                            getString(R.string.generic_error_title),
                            getString(R.string.reset_email_not_found_text));
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
        }
    }
}
