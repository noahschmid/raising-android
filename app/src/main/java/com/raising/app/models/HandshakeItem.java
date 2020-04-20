package com.raising.app.models;

import android.graphics.Bitmap;

import lombok.Data;

@Data
public class HandshakeItem {
    private long id;
    private boolean isStartup;
    private String name;
    private String attribute;
    private int matchingPercent;
    private long pictureId;
    private InteractionState interactionState;

    public String getHandshakePercentString() {
        return matchingPercent + " %";
    }
}
