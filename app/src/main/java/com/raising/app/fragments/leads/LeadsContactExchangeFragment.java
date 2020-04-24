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
import com.raising.app.models.leads.InteractionType;
import com.raising.app.models.leads.Lead;

public class LeadsContactExchangeFragment extends RaisingFragment {
    private ImageView contactImage;
    private TextView contactName, contactMail, saveContact;
    private Button btnInteract;

    private InteractionType contactType;
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
            contactType = (InteractionType) getArguments().getSerializable("contactType");
        }

        //TODO: store contact fetched with id in this variable
        Lead contact;

        /*
        contactImage = view.findViewById(R.id.leads_contact_picture);
        contactImage.setImageBitmap(contact.getBitmap());
        contactName = view.findViewById(R.id.leads_contact_name);
        contactName.setText(contact.getName());
        contactMail = view.findViewById(R.id.leads_contact_mail);
        contactMail.setText(contact.getEmail());

        saveContact = view.findViewById(R.id.leads_contact_save_contact);
        saveContact.setOnClickListener(v -> {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getPhone())
                    .putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getEmail())
                    .putExtra(ContactsContract.Intents.Insert.NAME, contact.getName());
            startActivity(intent);
        });

        btnInteract = view.findViewById(R.id.button_leads_contact_interact);
        btnInteract.setOnClickListener(v -> {
            Intent interactionIntent;
            String [] addresses = {contact.getEmail()};
            switch (contactType) {
                case COFFEE:
                    interactionIntent = new Intent(Intent.ACTION_SENDTO);
                    interactionIntent.setData(Uri.parse("mailto:"));
                    interactionIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
                    interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.leads_contact_coffee_subject_template));
                    interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.leads_contact_coffee_body_template));
                    break;
                case BUSINESS_PLAN:
                    interactionIntent = new Intent(Intent.ACTION_SENDTO);
                    interactionIntent.setData(Uri.parse("mailto:"));
                    interactionIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
                    interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.leads_contact_business_plan_subject_template));
                    interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.leads_contact_business_plan_body_template));
                    break;startActivity(interactionIntent);
                case E_MAIL:
                    interactionIntent = new Intent(Intent.ACTION_SENDTO);
                    interactionIntent.setData(Uri.parse("mailto:"));
                    interactionIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
                    interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.leads_contact_mail_subject_template));
                    interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.leads_contact_mail_body_template));
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
