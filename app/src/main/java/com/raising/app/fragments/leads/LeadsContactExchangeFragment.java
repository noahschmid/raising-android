package com.raising.app.fragments.leads;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.leads.HandshakeContactType;
import com.raising.app.models.leads.Lead;

public class LeadsContactExchangeFragment extends RaisingFragment {
    private ImageView contactImage;
    private TextView contactName, contactMail, saveContact;
    private Button btnInteract;

    private HandshakeContactType contactType;
    private long id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_contact), true);
        return inflater.inflate(R.layout.fragment_leads_contact_exchange, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments().getSerializable("contactType") != null) {
            id = getArguments().getLong("id");
            contactType = (HandshakeContactType) getArguments().getSerializable("contactType");
        }

        //TODO: store contact fetched with id in this variable
        Lead contact;

        /*
        contactImage = view.findViewById(R.id.handshake_contact_picture);
        contactImage.setImageBitmap(contact.getBitmap());
        contactName = view.findViewById(R.id.handshake_contact_name);
        contactName.setText(contact.getName());
        contactMail = view.findViewById(R.id.handshake_contact_mail);
        contactMail.setText(contact.getEmail());

        saveContact = view.findViewById(R.id.handshake_contact_save_contact);
        saveContact.setOnClickListener(v -> {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getPhone())
                    .putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getEmail())
                    .putExtra(ContactsContract.Intents.Insert.NAME, contact.getName());
            startActivity(intent);
        });

        btnInteract = view.findViewById(R.id.button_handshake_contact_interact);
        btnInteract.setOnClickListener(v -> {
            Intent interactionIntent;
            switch (contactType) {
                case COFFEE:
                    interactionIntent = new Intent(Intent.ACTION_SENDTO);
                    interactionIntent.setType("text/plain");
                    interactionIntent.putExtra(Intent.EXTRA_EMAIL, contact.getEmail());
                    interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.handshake_contact_coffee_subject_template));
                    interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.handshake_contact_coffee_body_template));
                    break;
                case BUSINESS_PLAN:
                    interactionIntent = new Intent(Intent.ACTION_SENDTO);
                    interactionIntent.setType("text/plain");
                    interactionIntent.putExtra(Intent.EXTRA_EMAIL, contact.getEmail());
                    interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.handshake_contact_business_plan_subject_template));
                    interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.handshake_contact_business_plan_body_template));
                    break;
                case E_MAIL:
                    interactionIntent = new Intent(Intent.ACTION_SENDTO);
                    interactionIntent.setType("text/plain");
                    interactionIntent.putExtra(Intent.EXTRA_EMAIL, contact.getEmail());
                    interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.handshake_contact_mail_subject_template));
                    interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.handshake_contact_mail_body_template));
                    break;
                case PHONE:
                    interactionIntent = new Intent(Intent.ACTION_DIAL);
                    String telUri = "tel:" + contact.getPhone();
                    interactionIntent.setData(Uri.parse(telUri));
                    break;
                default:
                    interactionIntent = new Intent();
                    break;
            }
            startActivity(interactionIntent);
        });
        */
    }
}
