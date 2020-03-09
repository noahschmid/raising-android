package com.example.raising.authentication.fragments.forgotPassword;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.raising.MainActivity;
import com.example.raising.R;

public class ForgotPasswordEmailFragment extends Fragment implements View.OnClickListener{
    private EditText emailInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_email, container, false);

        emailInput = view.findViewById(R.id.editText_forgot_email);

        hideBottomNavigation(true);

        Button btnSend = view.findViewById(R.id.button_forgot_send);
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
            case R.id.button_forgot_send:
                prepareResetEmail();
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

    private void prepareResetEmail() {
        String email = emailInput.getText().toString();
        resetEmail(email);
    }

    private void resetEmail(String email) {

    }
}
