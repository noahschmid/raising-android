package com.raising.app;

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

import com.raising.app.authentication.fragments.LoginFragment;

public class SettingsFragment extends Fragment implements View.OnClickListener {

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

}
