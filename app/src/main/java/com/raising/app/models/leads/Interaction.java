package com.raising.app.models.leads;

import lombok.Data;

@Data
public class Interaction {
    InteractionState interactionState;
    InteractionType interactionType;
}
