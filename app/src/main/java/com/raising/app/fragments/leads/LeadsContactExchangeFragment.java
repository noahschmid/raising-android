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
import com.raising.app.models.ContactData;
import com.raising.app.models.leads.InteractionType;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.ContactDataHandler;

public class LeadsContactExchangeFragment extends RaisingFragment {
    private ImageView contactImage;
    private TextView contactName, contactMail, saveContact;
    private Button btnInteract;

    private ContactData contactData;
    private long id;
    private Lead lead;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_contact), true);
        return inflater.inflate(R.layout.fragment_leads_contact_exchange, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null) {
            lead = (Lead)getArguments().getSerializable("lead");
            contactData = ContactDataHandler.getContactData(lead.getAccountId());

            contactImage = view.findViewById(R.id.leads_contact_picture);
            loadProfileImage(lead.getProfilePictureId(), contactImage);
            contactName = view.findViewById(R.id.leads_contact_name);
            contactName.setText(lead.getTitle());

            if(contactData != null) {
                contactMail = view.findViewById(R.id.leads_contact_mail);
                contactMail.setText(contactData.getEmail());
            } else {
                contactMail.setVisibility(View.GONE);
            }
            /*
            c

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
                switch (contactType) {
                    case COFFEE:
                        interactionIntent = new Intent(Intent.ACTION_SENDTO);
                        interactionIntent.setType("text/plain");
                        interactionIntent.putExtra(Intent.EXTRA_EMAIL, contact.getEmail());
                        interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.leads_contact_coffee_subject_template));
                        interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.leads_contact_coffee_body_template));
                        break;
                    case BUSINESS_PLAN:
                        interactionIntent = new Intent(Intent.ACTION_SENDTO);
                        interactionIntent.setType("text/plain");
                        interactionIntent.putExtra(Intent.EXTRA_EMAIL, contact.getEmail());
                        interactionIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.leads_contact_business_plan_subject_template));
                        interactionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.leads_contact_business_plan_body_template));
                        break;
                    case E_MAIL:
                        interactionIntent = new Intent(Intent.ACTION_SENDTO);
                        interactionIntent.setType("text/plain");
                        interactionIntent.putExtra(Intent.EXTRA_EMAIL, contact.getEmail());
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
}
