package com.raising.app.models;

import android.graphics.Bitmap;

import lombok.Data;

@Data
public class MatchlistItem {
    private long id;
    private String name;
    private String attribute;
    private String sentence;
    private Image image;
    private float matchingPercent;

    public MatchlistItem(long id, String name, String attribute, String sentence,
                         Image image, float matchingPercent) {
        this.id = id;
        this.name = name;
        this.attribute = attribute;
        this.sentence = sentence;
        this.image = image;
        this.matchingPercent = matchingPercent;
    }

    public Bitmap getBitmap() {
        return image.getBitmap();
    }

    public String getMatchingPercentString() {
        return matchingPercent + "%";
    }
}
