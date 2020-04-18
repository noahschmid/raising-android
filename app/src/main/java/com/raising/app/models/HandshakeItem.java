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
    private Image image;
    private InteractionState interactionState;

    public HandshakeItem(long id, boolean isStartup, String name, String attribute, int matchingPercent, Image image, InteractionState interactionState) {
        this.id = id;
        this.isStartup = isStartup;
        this.name = name;
        this.attribute = attribute;
        this.matchingPercent = matchingPercent;
        this.image = image;
        this.interactionState = interactionState;
    }

    public Bitmap getBitmap() {
        return image.getBitmap();
    }

    public String getHandshakePercentString() {
        return matchingPercent + " %";
    }
}
