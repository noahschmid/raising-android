package com.raising.app.fragments.leads;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.ContactsContract;
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
import com.raising.app.util.InternalStorageHandler;

import java.net.URLEncoder;

import static com.raising.app.models.leads.InteractionType.COFFEE;

public class LeadsContactExchangeFragment extends RaisingFragment {
    private ImageView contactImage;
    private TextView contactName, contactPhone, contactMail, saveContact;
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

            contactMail = view.findViewById(R.id.leads_contact_mail);
            contactPhone = view.findViewById(R.id.leads_contact_phone);
            
            if(contactData.getEmail() != null) {
                contactMail.setText(contactData.getEmail());
            } else {
                contactMail.setVisibility(View.GONE);
            }

            if(contactData.getPhone() != null) {
                contactPhone.setText(contactData.getPhone());
            } else {
                contactPhone.setVisibility(View.GONE);
            }

            saveContact = view.findViewById(R.id.leads_contact_save_contact);
            saveContact.setOnClickListener(v -> {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, "" + contactData.getPhone())
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, "" + contactData.getEmail())
                        .putExtra(ContactsContract.Intents.Insert.NAME, lead.getFirstName() + " "
                        + lead.getLastName())
                .putExtra(ContactsContract.Intents.Insert.COMPANY, "" + lead.getCompanyName());
                startActivity(intent);
            });

            btnInteract = view.findViewById(R.id.button_leads_contact_interact);
            if(contactData.getEmail() == null) {
                btnInteract.setVisibility(View.GONE);
            } else {
                btnInteract.setOnClickListener(v -> {
                    Intent interactionIntent = new Intent(Intent.ACTION_SENDTO);
                    interactionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String subject = getString(R.string.leads_contact_mail_subject_template);
                    String body = getString(R.string.leads_contact_mail_body_template);

                    String uriText = "mailto:" + contactData.getEmail() +
                            "?subject=" + URLEncoder.encode(subject) +
                            "&body=" + URLEncoder.encode(body);

                    interactionIntent.setData(Uri.parse(uriText));
                    startActivity(Intent.createChooser(interactionIntent,
                            "Send Email Using: "));
                });
            }
        }
    }
}
