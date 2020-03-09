package com.example.raising.authentication.fragments.forgotPassword;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.raising.MainActivity;
import com.example.raising.R;
import com.example.raising.RaisingFragment;
import com.example.raising.authentication.SimpleMessageDialog;

public class ForgotPasswordTokenFragment extends RaisingFragment implements View.OnClickListener {
    private EditText tokenInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_token, container, false);

        tokenInput = view.findViewById(R.id.editText_forgot_token);

        hideBottomNavigation(true);

        Button loginWithToken = view.findViewById(R.id.button_forgot_loginWithToken);
        loginWithToken.setOnClickListener(this);

        showSimpleDialog(getString(R.string.forgot_dialog_title), getString(R.string.forgot_dialog_text));

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

    private void prepareLoginWithToken() {
        String token = tokenInput.getText().toString();
        loginWithToken(token);
    }

    private void loginWithToken(String token) {

    }
}
