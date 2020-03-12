package com.example.raising.authentication.fragments.registration.startup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raising.R;
import com.example.raising.RaisingFragment;

public class RegisterFinancialRequirementsFragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_financial_requirements, container, false);
    }
}
