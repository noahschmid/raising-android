package com.raising.app.models;

import android.graphics.Bitmap;

import lombok.Data;

@Data
public class HandshakeOpenRequestItem {
    private long id;
    private boolean isStartup;
    private String name;
    private String attribute;
    private Image image;

    public HandshakeOpenRequestItem(long id, boolean isStartup, String name, String attribute, Image image) {
        this.id = id;
        this.isStartup = isStartup;
        this.name = name;
        this.attribute = attribute;
        this.image = image;
    }

    public Bitmap getBitmap() {
        return image.getBitmap();
    }
}
