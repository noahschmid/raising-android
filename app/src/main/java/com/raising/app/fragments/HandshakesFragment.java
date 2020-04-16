package com.raising.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;

public class HandshakesFragment extends RaisingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customizeAppBar(getString(R.string.toolbar_title_handshakes), false);
        return inflater.inflate(R.layout.fragment_handshakes, container, false);
    }
}
