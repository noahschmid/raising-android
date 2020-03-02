package com.example.raising;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterFragment extends Fragment implements View.OnClickListener  {

    private EditText username_input;
    private EditText password_input;
    private EditText confirm_password_input;

    private RegisterViewModel mViewModel;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        username_input = view.findViewById(R.id.editText_register_username);
        password_input = view.findViewById(R.id.editText_register_password);
        confirm_password_input = view.findViewById(R.id.editText_register_confirmPassword);

        Button btn_continue = view.findViewById(R.id.button_register_continue);
        btn_continue.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register_continue:
                register();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel
    }

    private void register() {
        final String USERNAME = username_input.getText().toString();
        final String PASSWORD = password_input.getText().toString();
        final String CONFIRM_PASSWORD = confirm_password_input.getText().toString();

        // check if password and confirm password are equal
        if(PASSWORD.contentEquals(CONFIRM_PASSWORD)) {

            try {
                String endpoint = "http://33383.hostserv.eu:8080/account/register";
                JSONObject params = new JSONObject();
                params.put("username", USERNAME);
                params.put("password", PASSWORD);
                JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                        endpoint,
                        params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //TODO: display success
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 403) {
                            String body = new String(error.networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(body);
                                showErrorDialog(
                                        getString(R.string.register_error_dialog_title),
                                        response.getString("message")
                                );
                            } catch (JSONException e) {
                                Log.d("debugMessage", e.toString());
                            }
                        }
                        Log.d("debugMessage", error.toString());
                    }
                });
                ApiRequestHandler.getInstance(getContext()).addToRequestQueue(loginRequest);
            } catch(Exception e) {
                Log.d("debugMessage", e.getMessage());
            }
        // if PASSWORD does not equal CONFIRM_PASSWORD
        } else {
            showErrorDialog(
                    getString(R.string.register_error_dialog_title_password_match),
                    getString(R.string.register_error_dialog_text_password_match));
        }
    }

    /**
     * Opens a dialog displaying an error message
     * @param dialogMessage The message that is to be displayed
     * @param dialogTitle The title of the dialog
     */
    private void showErrorDialog(String dialogTitle, String dialogMessage) {
        try {
            LoginDialog loginErrorFragment = new LoginDialog().newInstance(dialogTitle, dialogMessage);
            loginErrorFragment.show(getActivity().getSupportFragmentManager(), "registerErrorDialog");
        } catch (NullPointerException e) {
            Log.d("debugMessage", e.getMessage());
        }

    }
}
