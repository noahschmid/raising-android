package com.raising.app.models.leads.actions;

import android.content.Intent;
import android.net.Uri;

import com.raising.app.R;
import com.raising.app.models.ContactData;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.ContactDataHandler;
import com.raising.app.util.InternalStorageHandler;

import java.net.URLEncoder;

public class BusinessPlanAction implements HandshakeAction {
    public void execute(Lead lead) {
        ContactData contactData = ContactDataHandler.getContactData(lead.getAccountId());
        Intent interactionIntent = new Intent(Intent.ACTION_SENDTO);
        interactionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String subject = InternalStorageHandler.getContext()
                .getResources().getString(R.string.leads_contact_business_plan_subject_template);
        String body = InternalStorageHandler.getContext()
                .getResources().getString(R.string.leads_contact_business_plan_body_template);

        String uriText = "mailto:" + contactData.getEmail() +
                "?subject=" + URLEncoder.encode(subject) +
                "&body=" + URLEncoder.encode(body);

        interactionIntent.setData(Uri.parse(uriText));
        InternalStorageHandler.getActivity().startActivity(Intent.createChooser(interactionIntent,
                "Send Email Using: "));
    }
}
