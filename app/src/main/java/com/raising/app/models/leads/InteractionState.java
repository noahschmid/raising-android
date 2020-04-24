package com.raising.app.models.leads;

import java.io.Serializable;

public enum InteractionState implements Serializable {
    EMPTY,
    STARTUP_ACCEPTED,
    INVESTOR_ACCEPTED,
    STARTUP_DECLINED,
    INVESTOR_DECLINED,
    HANDSHAKE
}