package com.raising.app.models.leads;

import com.raising.app.R;
import com.raising.app.models.leads.actions.BusinessPlanAction;
import com.raising.app.models.leads.actions.CoffeeAction;
import com.raising.app.models.leads.actions.ContactExchangeAction;
import com.raising.app.models.leads.actions.HandshakeAction;
import com.raising.app.util.InternalStorageHandler;

import java.io.Serializable;

@SuppressWarnings("ALL")
public enum InteractionType implements Serializable {
    COFFEE(getString(R.string.leads_contact_coffee), new CoffeeAction()),
    BUSINESS_PLAN(getString(R.string.leads_contact_businessplan), new BusinessPlanAction()),
    PHONE_CALL(getString(R.string.leads_contact_phone)),
    EMAIL(getString(R.string.leads_contact_email)),
    VIDEO_CONFERENCE(getString(R.string.leads_contact_video));

    String representation;
    HandshakeAction action;

    InteractionType(String representation) {
        this.action = new ContactExchangeAction();
        this.representation = representation;
    }

    InteractionType(String representation, HandshakeAction action) {
        this.action = action;
        this.representation = representation;
    }

    public String getCaption() { return representation; }
    public void executeAction(Lead lead) { action.execute(lead); }

    private static String getString(int id) {
        return InternalStorageHandler.getContext().getResources().getString(id);
    }
}
