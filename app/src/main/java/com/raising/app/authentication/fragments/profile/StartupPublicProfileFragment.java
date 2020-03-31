package com.raising.app.authentication.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.models.Startup;

public class StartupPublicProfileFragment extends RaisingFragment {
    Startup profileStartup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startup_public_profile,
                container, false);

        profileStartup = (Startup) getArguments().get("startup");

        return view;
    }
}
