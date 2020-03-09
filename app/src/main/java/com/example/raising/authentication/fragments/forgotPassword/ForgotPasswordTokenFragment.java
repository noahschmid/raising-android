package com.example.raising.authentication.fragments.forgotPassword;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.raising.MainActivity;
import com.example.raising.R;
import com.example.raising.authentication.AuthenticationDialog;

public class ForgotPasswordTokenFragment extends Fragment implements View.OnClickListener {
    private EditText tokenInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_token, container, false);

        tokenInput = view.findViewById(R.id.editText_forgot_token);

        hideBottomNavigation(true);

        Button loginWithToken = view.findViewById(R.id.button_forgot_loginWithToken);
        loginWithToken.setOnClickListener(this);

        showDialog(getString(R.string.forgot_dialog_title), getString(R.string.forgot_dialog_text));

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
                ;
                break;
            default:
                break;
        }
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

    private void prepareLoginWithToken() {
        String token = tokenInput.getText().toString();
        loginWithToken(token);
    }

    private void loginWithToken(String token) {

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
        try {
            loginDialog.show(getActivity().getSupportFragmentManager(), "forgotTokenDialog");
        } catch (NullPointerException e) {
            Log.d("debugMessage", e.toString());
        }
    }


}
