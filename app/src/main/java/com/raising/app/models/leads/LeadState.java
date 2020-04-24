package com.raising.app.models.leads;

import java.io.Serializable;

public enum LeadState implements Serializable {
    YOUR_TURN,
    PENDING,
    CLOSED,
    OPEN_REQUEST
}
