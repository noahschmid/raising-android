package com.raising.app.models;

import lombok.Data;

@Data
public class MatchlistItem {
    private String name;
    private String attribute;
    private String sentence;
    private Image image;
    private int matchingPercent;

    public MatchlistItem(String name, String attribute, String sentence, Image image, int matchingPercent) {
        this.name = name;
        this.attribute = attribute;
        this.sentence = sentence;
        this.image = image;
        this.matchingPercent = matchingPercent;
    }

    public String getMatchingPercentString() {
        return matchingPercent + "%";
    }
}
