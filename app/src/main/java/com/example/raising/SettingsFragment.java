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

import com.example.raising.authentication.fragments.LoginFragment;

public class SettingsFragment extends RaisingFragment implements View.OnClickListener {

    private SettingsViewModel mViewModel;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button btnLogout = view.findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_logout:
                logout();
                break;
            default:
                break;
        }
    }

    private void logout() {
        Log.d("debugMessage", "logout()");
        AuthenticationHandler.logout(getContext());
        changeFragment(new LoginFragment(), "LoginFragment");
    }
}
