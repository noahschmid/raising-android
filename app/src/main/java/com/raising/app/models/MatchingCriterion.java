package com.raising.app.models;

import lombok.Data;

@Data
public class MatchingCriterion  {
    private String title;
    private String image;

    public MatchingCriterion(String title, String image) {
        this.title = title;
        this.image = image;
    }
}
