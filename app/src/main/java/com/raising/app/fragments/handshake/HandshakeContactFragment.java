package com.raising.app.fragments.handshake;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;

public class HandshakeContactFragment extends RaisingFragment implements View.OnClickListener{
    private Button coffeeButton, businessplanButton, phoneButton, mailButton, videoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_handshake_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coffeeButton = view.findViewById(R.id.button_handshake_contact_coffee);
        coffeeButton.setOnClickListener(this);
        businessplanButton = view.findViewById(R.id.button_handshake_contact_businessplan);
        businessplanButton.setOnClickListener(this);
        phoneButton = view.findViewById(R.id.button_handshake_contact_phone);
        phoneButton.setOnClickListener(this);
        mailButton = view.findViewById(R.id.button_handshake_contact_email);
        mailButton.setOnClickListener(this);
        videoButton = view.findViewById(R.id.button_handshake_contact_video);
        videoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_handshake_contact_coffee:
                //TODO: implement action
                break;
            case R.id.button_handshake_contact_businessplan:
                //TODO: implement action
                break;
            case R.id.button_handshake_contact_phone:
                //TODO: implement action
                break;
            case R.id.button_handshake_contact_email:
                //TODO: implement action
                break;
            case R.id.button_handshake_contact_video:
                //TODO: implement action
                break;
            default:
                break;
        }
    }
}
