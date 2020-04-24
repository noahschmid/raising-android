package com.raising.app.models.leads.actions;

import android.content.Intent;
import android.net.Uri;

import com.raising.app.R;
import com.raising.app.models.ContactData;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.ContactDataHandler;
import com.raising.app.util.InternalStorageHandler;

public class PhoneCallAction implements HandshakeAction{
    public void execute(Lead lead) {
        ContactData contactData = ContactDataHandler.getContactData(lead.getAccountId());
        Intent interactionIntent = new Intent(Intent.ACTION_DIAL);
        interactionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String telUri = "tel:" + contactData.getPhone();
        interactionIntent.setData(Uri.parse(telUri));
        InternalStorageHandler.getActivity().startActivity(interactionIntent);
    }
}
