package com.raising.app.models.leads;

import com.raising.app.R;
import com.raising.app.util.InternalStorageHandler;

import java.io.Serializable;

public enum InteractionType implements Serializable {
    COFFEE(getString(R.string.leads_contact_coffee)),
    BUSINESSPLAN(getString(R.string.leads_contact_businessplan)),
    PHONE_CALL(getString(R.string.leads_contact_phone)),
    EMAIL(getString(R.string.leads_contact_email)),
    VIDEO_CONFERENCE(getString(R.string.leads_contact_video));

    String representation;
    InteractionType(String representation) {
        this.representation = representation;
    }

    public String getCaption() { return representation; }

    private static String getString(int id) {
        return InternalStorageHandler.getContext().getResources().getString(id);
    }
}
