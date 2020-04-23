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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.leads.Interaction;
import com.raising.app.models.leads.InteractionType;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.LeadsInteraction;

import lombok.Data;
import lombok.Getter;


public class LeadsContactFragment extends RaisingFragment {
    private long id;
    private Lead contact;
    private boolean disableContact = false;
    private boolean declinedContact = false;

    @Getter
    private ImageView arrowCoffee, arrowBusinessplan, arrowPhone, arrowEmail, arrowVideo;

    @Getter
    private ImageButton confirmCoffee, declineCoffee, confirmBusinessplan, declineBusinessplan,
            confirmPhone, declinePhone, confirmEmail, declineEmail, confirmVideo, declineVideo;

    @Getter
    private CardView contactCoffee, contactBusinessPlan, contactPhone, contactEmail, contactVideo;

    @Getter
    private Button coffeeButton, businessplanButton, phoneButton, emailButton, videoButton;

    @Getter
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
            if(getArguments().getBoolean("disableContact")) {
                disableContact = true;
            }
            if(getArguments().getBoolean("declinedContact")) {
                declinedContact = true;
            }
            id = getArguments().getLong("id");

            //TODO: store contact of id in following object:
            contact = new Lead();
        }

        // initially find all arrow indicators
        arrowCoffee = view.findViewById(R.id.leads_coffee_arrow);
        arrowBusinessplan = view.findViewById(R.id.leads_businessplan_arrow);
        arrowPhone = view.findViewById(R.id.leads_phone_arrow);
        arrowEmail = view.findViewById(R.id.leads_email_arrow);
        arrowVideo = view.findViewById(R.id.leads_video_arrow);

        // initially find all confirm/decline image buttons
        // confirmCoffee = view.findViewById(R.id.button_leads_confirm_coffee);
        declineCoffee = view.findViewById(R.id.button_leads_decline_coffee);
        // confirmBusinessplan = view.findViewById(R.id.button_leads_confirm_businessplan);
        declineBusinessplan = view.findViewById(R.id.button_leads_decline_businessplan);
        // confirmPhone = view.findViewById(R.id.button_leads_confirm_phone);
        declinePhone = view.findViewById(R.id.button_leads_decline_phone);
        // confirmEmail = view.findViewById(R.id.button_leads_confirm_email);
        declineEmail = view.findViewById(R.id.button_leads_decline_email);
        // confirmVideo = view.findViewById(R.id.button_leads_confirm_video);
        declineVideo = view.findViewById(R.id.button_leads_decline_video);

        // initially find all contact cards
        contactCoffee = view.findViewById(R.id.leads_contact_coffee);
        contactBusinessPlan = view.findViewById(R.id.leads_contact_businessplan);
        contactPhone = view.findViewById(R.id.leads_contact_call);
        contactEmail = view.findViewById(R.id.leads_contact_mail);
        contactVideo = view.findViewById(R.id.leads_contact_video);

        // initially find all contact buttons
        coffeeButton = view.findViewById(R.id.button_leads_contact_coffee);
        businessplanButton = view.findViewById(R.id.button_leads_contact_businessplan);
        phoneButton = view.findViewById(R.id.button_leads_contact_phone);
        emailButton = view.findViewById(R.id.button_leads_contact_email);
        videoButton = view.findViewById(R.id.button_leads_contact_video);

        // create a class instance for all interaction types
        coffee = new LeadsInteraction(contact.getInteraction(InteractionType.COFFEE), this);
        businessplan = new LeadsInteraction(contact.getInteraction(InteractionType.BUSINESSPLAN), this);
        phone = new LeadsInteraction(contact.getInteraction(InteractionType.PHONE_CALL), this);
        email = new LeadsInteraction(contact.getInteraction(InteractionType.EMAIL), this);
        video = new LeadsInteraction(contact.getInteraction(InteractionType.VIDEO_CALL), this);

        TextView disableContactText = view.findViewById(R.id.leads_contact_disabled_text);
        LinearLayout contactItemsLayout = view.findViewById(R.id.leads_contact_items_layout);
        LinearLayout blurOverlay = view.findViewById(R.id.leads_contact_blur_overlay);
        blurOverlay.setVisibility(View.GONE);
        if(disableContact) {
            disableContactText.setText(getString(R.string.leads_contact_disabled_contact_text));
            blurOverlay.setVisibility(View.VISIBLE);
        }

        if(declinedContact) {
            disableContactText.setText(getString(R.string.leads_contact_declined_contact_text));
            blurOverlay.setVisibility(View.VISIBLE);
        }
    }

    public void enterInteractionExchange(Interaction interaction) {
        Bundle args = new Bundle();
        args.putLong("id", contact.getId());
        args.putSerializable("contactType", interaction.getInteractionType());
        Fragment fragment = new LeadsContactExchangeFragment();
        fragment.setArguments(args);
        changeFragment(fragment, "LeadContactExchangeFragment");
    }
}
