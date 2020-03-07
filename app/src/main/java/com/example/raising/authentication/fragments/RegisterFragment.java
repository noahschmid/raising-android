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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.raising.ApiRequestHandler;
import com.example.raising.AuthenticationHandler;
import com.example.raising.MainActivity;
import com.example.raising.MatchesFragment;
import com.example.raising.R;
import com.example.raising.authentication.AuthenticationDialog;
import com.example.raising.authentication.view_models.RegisterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterFragment extends Fragment implements View.OnClickListener  {

    private EditText username_input;
    private EditText password_input;
    private EditText confirm_password_input;

    final private String REGISTER_ENDPOINT = "https://33383.hostserv.eu:8080/account/register";

    private RegisterViewModel mViewModel;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        hideBottomNavigation(true);

        username_input = view.findViewById(R.id.editText_register_username);
        password_input = view.findViewById(R.id.editText_register_password);
        confirm_password_input = view.findViewById(R.id.editText_register_confirmPassword);

        Button btn_continue = view.findViewById(R.id.button_register_continue);
        btn_continue.setOnClickListener(this);

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
            case R.id.button_register_continue:
                prepareRegistration();
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

    /**
     * Call {@link com.example.raising.MainActivity#hideBottomNavigation(boolean)}
     * @param isHidden if true, the bottomNavigation should be invisible,
     *                 if false, the bottomNavigation should be visible
     *
     * @author Lorenz Caliezi 06.03.2020
     */

    private void hideBottomNavigation(boolean isHidden) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null)
            activity.hideBottomNavigation(isHidden);
    }

    /**
     * Simple helper function that retrieves the users input from the layout
     *      and then calls {@link: register()}.
     * Enables easier testing, since you can give register() some parameters.
     */
    private void prepareRegistration() {
        String username = username_input.getText().toString();
        String password = password_input.getText().toString();
        String confirmPassword = confirm_password_input.getText().toString();
        String email = "testemail";

        register(username, password, confirmPassword, email);
    }

    /**
     * Send registration request to backend and process response
     * @param username the username to register
     * @param password the password of the user
     * @param confirmPassword the password confirmation input
     */
    private void register(String username, String password, String confirmPassword, String email) {
        if(username.length() == 0 || password.length() == 0 || confirmPassword.length() == 0) {
            showDialog(getString(R.string.register_dialog_title_empty_credentials),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        if(password.contentEquals(confirmPassword)) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("email", email);
                JsonObjectRequest loginRequest = new JsonObjectRequest(
                        REGISTER_ENDPOINT,
                        new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                showDialog(
                                        getString(R.string.register_dialog_title_success),
                                        getString(R.string.register_dialog_text_success)
                                );

                                try {
                                    AuthenticationHandler.login(response.getString("token"),
                                            response.getLong("id"), getContext());
                                    changeFragment(new MatchesFragment(), "MatchesFragment");
                                } catch(Exception e) {
                                    Log.d("debugMessage", e.getMessage());
                                    showDialog(getString(R.string.generic_error_title),
                                            getString(R.string.generic_error_text));
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 400) {
                            showDialog(
                                    getString(R.string.register_dialog_title_400),
                                    getString(R.string.register_dialog_text_400)
                            );
                        }

                        showDialog(getString(R.string.generic_error_title), error.getLocalizedMessage());
                    }
                });
                ApiRequestHandler.getInstance(getContext()).addToRequestQueue(loginRequest);
            } catch(Exception e) {
                Log.d("debugMessage", e.getMessage());
            }
        // if PASSWORD does not equal CONFIRM_PASSWORD
        } else {
            showDialog(
                    getString(R.string.register_dialog_title_password_match),
                    getString(R.string.register_dialog_text_password_match));
        }
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

    /**
     * Opens a dialog displaying an error message
     * @param dialogMessage The message that is to be displayed
     * @param dialogTitle The title of the dialog
     *
     * @author Lorenz Caliezi 03.03.2020
     * @version 1.1
     */
    private void showDialog(String dialogTitle, String dialogMessage) {
        try {
            AuthenticationDialog registerDialog =
                    new AuthenticationDialog().newInstance(dialogTitle, dialogMessage);
            registerDialog.show(getActivity().getSupportFragmentManager(), "registerDialog");
        } catch (NullPointerException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }
}
