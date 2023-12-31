package com.raising.app.models.leads.actions;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.raising.app.R;
import com.raising.app.models.ContactData;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.ContactDataHandler;
import com.raising.app.util.InternalStorageHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CoffeeAction implements HandshakeAction {
    private static String TAG = "CoffeeAction";

    public void execute(Lead lead) {
        ContactData contactData = ContactDataHandler.getContactData(lead.getAccountId());

        if (contactData == null) {
            return;
        }

        Intent interactionIntent = new Intent(Intent.ACTION_SENDTO);
        interactionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String subject = InternalStorageHandler.getContext()
                .getResources().getString(R.string.leads_contact_coffee_subject_template);
        String body = InternalStorageHandler.getContext()
                .getResources().getString(R.string.leads_contact_coffee_body_template);

        String uriText = "mailto:" + contactData.getEmail()
                + "?subject=" + subject
                + "&body=" + body;

        interactionIntent.setData(Uri.parse(uriText));
        InternalStorageHandler.getActivity().startActivity(interactionIntent);
    }
}
