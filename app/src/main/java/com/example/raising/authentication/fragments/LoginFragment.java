package com.example.raising.authentication.fragments;

import androidx.fragment.app.FragmentManager;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.raising.ApiRequestHandler;
import com.example.raising.MatchesFragment;
import com.example.raising.R;
import com.example.raising.authentication.AuthenticationDialog;
import com.example.raising.authentication.view_models.LoginViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText usernameInput;
    private EditText passwordInput;

    final private String LOGIN_ENDPOINT = "http://33383.hostserv.eu:8080/account/login";
    //final private String LOGIN_ENDPOINT = "http://192.168.1.120:8080/account/login";
    private LoginViewModel mViewModel;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameInput = view.findViewById(R.id.editText_register_username);
        passwordInput = view.findViewById(R.id.editText_register_password);

        Button btnLogin = view.findViewById(R.id.button_login);
        btnLogin.setOnClickListener(this);
        Button btnRegister = view.findViewById(R.id.button_register);
        btnRegister.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                prepareLogin();
                break;
            case R.id.button_register:
                goToRegisterFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

    /**
     * Simple helper function that retrieves the users input from the layout
     *      and then calls {@link: login()}.
     * Enables easier testing, since you can give login() some parameters.
     *
     * @author Lorenz Caliezi 03.03.2020
     * @version 1.0
     */
    private void prepareLogin() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        login(username, password);
    }

    /**
     * Send login request to backend and process response
     * @param username
     * @param password
     */
    private void login(String username, String password) {
        if(username.length() == 0 || password.length() == 0) {
            showDialog(getString(R.string.login_dialog_title_empty_credentials),
                    getString(R.string.login_dialog_text_empty_credentials));
            return;
        }
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            JsonObjectRequest loginRequest = new JsonObjectRequest(
                    LOGIN_ENDPOINT, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            changeFragment(new MatchesFragment(), "MatchesFragment");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        if(error.networkResponse.statusCode == 403) {
                            showDialog(
                                    getString(R.string.login_dialog_title_403),
                                    getString(R.string.login_dialog_text_403)
                            );
                        }
                    } catch (NullPointerException e) {
                        Log.d("debugMessage", e.toString());
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
     * @version 1.0
     */
    private void goToRegisterFragment() {
        changeFragment(new RegisterFragment(), "RegisterFragment");
    }

    /**
     * Change from the current fragment to the next
     * @param fragment The fragment, that should be displayed next
     * @param fragmentName The name of the next fragment.
     *                     Allows us to put the fragment on the BackStack
     *
     * @author Lorenz Caliezi 02.03.2020
     * @version 1.0
     */
    private void changeFragment(Fragment fragment, String fragmentName) {
        try {
            getActivitiesFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(fragmentName)
                    .commit();
        } catch (NullPointerException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Opens a simple dialog displaying a message
     * @param dialogMessage The message that is to be displayed
     *
     * @author Lorenz Caliezi 03.03.2020
     * @version 1.1
     */
    private void showDialog(String dialogTitle, String dialogMessage) {
        AuthenticationDialog loginDialog =
                new AuthenticationDialog().newInstance(dialogTitle, dialogMessage);
        loginDialog.show(getActivitiesFragmentManager(), "loginDialog");
    }

    /**
     * This methods retrieves an instance the SupportFragmentManager of the underlying activity
     * @return Instance of SupportFragmentManager of used Activity
     *
     * @author Lorenz Caliezi 02.03.2020
     * @version 1.0
     */
    private FragmentManager getActivitiesFragmentManager() {
        try {
            return getActivity().getSupportFragmentManager();
        } catch (NullPointerException e) {
            Log.d("debugMessage", e.toString());
        }
        return null;
    }
}
