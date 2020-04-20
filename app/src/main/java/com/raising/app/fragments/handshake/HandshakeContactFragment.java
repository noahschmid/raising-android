package com.raising.app.fragments.handshake;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.HandshakeContact;
import com.raising.app.models.HandshakeContactType;
import com.raising.app.models.InteractionState;

import java.util.Objects;

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
        // toggleContactButton(coffeeButton, (...));
        businessplanButton = view.findViewById(R.id.button_handshake_contact_businessplan);
        businessplanButton.setOnClickListener(this);
        // toggleContactButton(businessplanButton, (...));
        phoneButton = view.findViewById(R.id.button_handshake_contact_phone);
        phoneButton.setOnClickListener(this);
        // toggleContactButton(phoneButton, (...));
        mailButton = view.findViewById(R.id.button_handshake_contact_email);
        mailButton.setOnClickListener(this);
        // toggleContactButton(mailButton, (...));
        videoButton = view.findViewById(R.id.button_handshake_contact_video);
        videoButton.setOnClickListener(this);
        // toggleContactButton(videoButton, (...));

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
                changeFragment(fragment, "HandshakeContactExchangeFragment");
            }
        });
         */
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_handshake_contact_coffee:
                // toggleContactButton(coffeeButton, (...));
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

    private void toggleContactButton(Button button, InteractionState state) {
        Drawable drawable = button.getBackground();
        drawable = DrawableCompat.wrap(drawable);
        if (state == InteractionState.REQUESTED) {
            drawable.setTint(ContextCompat.getColor(
                    Objects.requireNonNull(this.getContext()), R.color.raisingPositiveAccent));
            button.setText(getString(R.string.requested_text));
        } else if(state == InteractionState.ACCEPTED ) {
            drawable.setTint(ContextCompat.getColor(
                    Objects.requireNonNull(this.getContext()), R.color.raisingPositive));
            button.setText(getString(R.string.confirm_text));
        }
    }
}
