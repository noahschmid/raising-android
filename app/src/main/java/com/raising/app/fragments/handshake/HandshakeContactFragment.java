package com.raising.app.fragments.handshake;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.HandshakeContact;
import com.raising.app.models.HandshakeContactType;

public class HandshakeContactFragment extends RaisingFragment implements View.OnClickListener{
    private Button coffeeButton, businessplanButton, phoneButton, mailButton, videoButton;
    private CardView contactCoffee, contactBusinessPlan, contactPhone, contactMail, contactVideo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_contact), true);
        return inflater.inflate(R.layout.fragment_handshake_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactCoffee = view.findViewById(R.id.handshake_contact_coffee);
        contactBusinessPlan = view.findViewById(R.id.handshake_contact_businessplan);
        contactPhone = view.findViewById(R.id.handshake_contact_call);
        contactMail = view.findViewById(R.id.handshake_contact_mail);
        contactVideo = view.findViewById(R.id.handshake_contact_video);

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

        //TODO: if exchange has happened, set respective button invisible and call respective card:
        /*
        (.....).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putLong("id", ...);
                args.putSerializable("contactType", (HandshakeContactType));
                Fragment fragment = new HandshakeContactExchangeFragment();
                fragment.setArguments(args);
                changeFragment(fragment);
            }
        });
         */
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
