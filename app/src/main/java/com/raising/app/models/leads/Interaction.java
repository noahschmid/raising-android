package com.raising.app.models.leads;

import java.io.Serializable;

import lombok.Data;

@Data
public class Interaction implements Serializable {
    long id;
    long partnerId;
    InteractionState interactionState;
    InteractionType interactionType;

    public Interaction(long id, InteractionType type, InteractionState state, long partnerId) {
        this.interactionState = state;
        this.interactionType = type;
        this.partnerId = partnerId;
        this.id = id;
    }

    public Interaction() {}
}
