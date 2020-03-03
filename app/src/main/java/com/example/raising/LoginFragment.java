package com.example.raising;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText username_input;
    private EditText password_input;

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

        username_input = view.findViewById(R.id.editText_register_username);
        password_input = view.findViewById(R.id.editText_register_password);

        Button btn_login = view.findViewById(R.id.button_login);
        btn_login.setOnClickListener(this);
        Button btn_register = view.findViewById(R.id.button_register);
        btn_register.setOnClickListener(this);

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
        String username = username_input.getText().toString();
        String password = password_input.getText().toString();
        login(username, password);
    }


    private void login(String username, String password) {

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            JsonObjectRequest loginRequest = new JsonObjectRequest(
                    LOGIN_ENDPOINT, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("debugMessageSuccess", response.toString());
                            changeFragment(new MatchesFragment(), "MatchesFragment");
                            /*
                            try {
                                username_input.setText(response.getString("message"));

                            } catch (JSONException e) {
                                Log.d("debugMessage", e.getMessage());
                                return;
                            }
                             */
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("debugMessageErrResp", error.toString());
                    try {
                        Log.d("debugMessage1", error.networkResponse.toString());
                        Log.d("debugMessage1", error.networkResponse.data.toString());
                        if(error.networkResponse.statusCode == 403) {
                            String body = new String(error.networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(body);
                                showDialog(
                                        getString(R.string.login_dialog_title_403),
                                        getString(R.string.login_dialog_text_403)
                                );
                            } catch (JSONException e) {
                                Log.d("debugMessage2", e.toString());
                            }
                        }
                        Log.d("debugMessage3", error.toString());
                    } catch (NullPointerException e) {
                        Log.d("debugMessage4", e.toString());
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
     * Opens a dialog displaying an error message
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
     * This methods retrieves an instance the SupportFragmentManager of the used Activity
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
