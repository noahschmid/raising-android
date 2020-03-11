package com.example.raising.authentication.fragments.forgotPassword;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.raising.R;
import com.example.raising.RaisingFragment;

public class ForgotPasswordEmailFragment extends RaisingFragment implements View.OnClickListener{
    private EditText emailInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_email, container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailInput = view.findViewById(R.id.editText_forgot_email);

        Button btnSend = view.findViewById(R.id.button_forgot_reset);
        btnSend.setOnClickListener(this);
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
                prepareResetPassword();
                break;
            default:
                break;
        }
    }

    /**
     * Simple helper method, that prepares {@link #resetPassword(String)}.
     *
     * @author Lorenz Caliezi 09.03.2020
     */
    private void prepareResetPassword() {
        String email = emailInput.getText().toString();
        resetPassword(email);
    }

    private void resetPassword(String email) {
        // TODO: Implement logic to reset password
    }
}
