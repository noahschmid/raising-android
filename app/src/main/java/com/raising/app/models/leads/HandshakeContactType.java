package com.raising.app.models.leads;

import com.raising.app.R;
import com.raising.app.util.InternalStorageHandler;

public enum HandshakeContactType {
    COFFEE(getString(R.string.handshake_contact_coffee)),
    BUSINESS_PLAN(getString(R.string.handshake_contact_businessplan)),
    PHONE(getString(R.string.handshake_contact_phone)),
    EMAIL(getString(R.string.handshake_contact_email)),
    VIDEO(getString(R.string.handshake_contact_video));

    String representation;
    HandshakeContactType(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }

    private static String getString(int id) {
        return InternalStorageHandler.getContext().getResources().getString(id);
    }
}
