package com.raising.app.models.leads;

import com.raising.app.R;
import com.raising.app.util.InternalStorageHandler;

public enum InteractionType {
    COFFEE(getString(R.string.leads_contact_coffee)),
    BUSINESS_PLAN(getString(R.string.leads_contact_businessplan)),
    PHONE(getString(R.string.leads_contact_phone)),
    EMAIL(getString(R.string.leads_contact_email)),
    VIDEO(getString(R.string.leads_contact_video));

    String representation;
    InteractionType(String representation) {
        this.representation = representation;
    }

    public String getCaption() { return representation; }

    private static String getString(int id) {
        return InternalStorageHandler.getContext().getResources().getString(id);
    }
}
