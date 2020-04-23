package com.raising.app.models.leads;

import lombok.Data;

@Data
public class Interaction {
    InteractionState interactionState;
    InteractionType interactionType;

    public Interaction(InteractionType type, InteractionState state) {
        this.interactionState = state;
        this.interactionType = type;
    }

    public Interaction() {}
}
