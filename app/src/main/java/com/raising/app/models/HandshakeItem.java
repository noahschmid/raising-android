package com.raising.app.models;

import android.graphics.Bitmap;

import com.raising.app.models.Image;

import lombok.Data;

@Data
public class HandshakeItem {
    private long id;
    private boolean isStartup;
    private String name;
    private String attribute;
    private String sentence;
    private Image image;
    private Image statusIcon;
    private float matchingPercent;

    public HandshakeItem(long id, boolean isStartup, String name, String attribute, String sentence,
                         Image image, Image statusIcon, float matchingPercent) {
        this.id = id;
        this.isStartup = isStartup;
        this.name = name;
        this.attribute = attribute;
        this.sentence = sentence;
        this.image = image;
        this.statusIcon = statusIcon;
        this.matchingPercent = matchingPercent;
    }

    public Bitmap getBitmap() {
        return image.getBitmap();
    }

    public String getHandshakePercentString() {
        return matchingPercent + "%";
    }
}
