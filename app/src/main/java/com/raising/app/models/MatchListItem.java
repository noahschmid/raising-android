package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.Data;

@Data
public class MatchListItem implements Serializable {
    private long id;
    private boolean isStartup;
    private String name;
    private String attribute;
    private String sentence;
    private Image image;
    private float matchingPercent;

    public MatchListItem(long id, boolean isStartup, String name, String attribute, String sentence,
                         Image image, float matchingPercent) {
        this.id = id;
        this.isStartup = isStartup;
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
