package com.raising.app.models.leads.actions;

import com.raising.app.models.leads.Lead;

public interface HandshakeAction {
    void execute(Lead lead);
}
