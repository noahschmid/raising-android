package com.raising.app.fragments.leads;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.LeadsInteraction;

import lombok.Data;

@Data
public class LeadsContactFragment extends RaisingFragment {
    private long id;
    Lead contact;

    public ImageView arrowCoffee, arrowBusinessplan, arrowPhone, arrowEmail, arrowVideo;
    public ImageButton confirmCoffee, declineCoffee, confirmBusinessplan, declineBusinessplan,
            confirmPhone, declinePhone, confirmEmail, declineEmail, confirmVideo, declineVideo;
    public CardView contactCoffee, contactBusinessPlan, contactPhone, contactEmail, contactVideo;
    public Button coffeeButton, businessplanButton, phoneButton, emailButton, videoButton;
    private LeadsInteraction coffee, businessplan, phone, email, video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_contact), true);
        return inflater.inflate(R.layout.fragment_leads_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            id = getArguments().getLong("id");

            //TODO: store contact of id in following object:
            contact = new Lead();
        }

        // initially find all arrow indicators
        arrowCoffee = view.findViewById(R.id.handshake_coffee_arrow);
        arrowBusinessplan = view.findViewById(R.id.handshake_businessplan_arrow);
        arrowPhone = view.findViewById(R.id.handshake_phone_arrow);
        arrowEmail = view.findViewById(R.id.handshake_email_arrow);
        arrowVideo = view.findViewById(R.id.handshake_video_arrow);

        // initially find all confirm/decline image buttons
        // confirmCoffee = view.findViewById(R.id.button_handshake_confirm_coffee);
        declineCoffee = view.findViewById(R.id.button_handshake_decline_coffee);
        // confirmBusinessplan = view.findViewById(R.id.button_handshake_confirm_businessplan);
        declineBusinessplan = view.findViewById(R.id.button_handshake_decline_businessplan);
        // confirmPhone = view.findViewById(R.id.button_handshake_confirm_phone);
        declinePhone = view.findViewById(R.id.button_handshake_decline_phone);
        // confirmEmail = view.findViewById(R.id.button_handshake_confirm_email);
        declineEmail = view.findViewById(R.id.button_handshake_decline_email);
        // confirmVideo = view.findViewById(R.id.button_handshake_confirm_video);
        declineVideo = view.findViewById(R.id.button_handshake_decline_video);

        // initially find all contact cards
        contactCoffee = view.findViewById(R.id.handshake_contact_coffee);
        contactBusinessPlan = view.findViewById(R.id.handshake_contact_businessplan);
        contactPhone = view.findViewById(R.id.handshake_contact_call);
        contactEmail = view.findViewById(R.id.handshake_contact_mail);
        contactVideo = view.findViewById(R.id.handshake_contact_video);

        // initially find all contact buttons
        coffeeButton = view.findViewById(R.id.button_handshake_contact_coffee);
        businessplanButton = view.findViewById(R.id.button_handshake_contact_businessplan);
        phoneButton = view.findViewById(R.id.button_handshake_contact_phone);
        emailButton = view.findViewById(R.id.button_handshake_contact_email);
        videoButton = view.findViewById(R.id.button_handshake_contact_video);

        // create a class instance for all interaction types
        coffee = new LeadsInteraction(contact.getCoffee(), this);
        businessplan = new LeadsInteraction(contact.getBusinessplan(), this);
        phone = new LeadsInteraction(contact.getPhone(), this);
        email = new LeadsInteraction(contact.getEmail(), this);
        video = new LeadsInteraction(contact.getVideo(), this);
    }

    public void enterInteractionExchange(Bundle args) {
        Fragment fragment = new LeadsContactExchangeFragment();
        fragment.setArguments(args);
        changeFragment(fragment, "LeadContactExchangeFragment");
    }
}
