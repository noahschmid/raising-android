package com.raising.app.models;

import lombok.Data;

@Data
public class Interaction {
    InteractionState interactionState;
    InteractionType interactionType;
}
