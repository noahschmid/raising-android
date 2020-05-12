package com.raising.app.models;

import android.graphics.Bitmap;

import com.raising.app.models.Model;

import lombok.Data;

@Data
public class MatchingCriteriaItem implements Model {
    private Bitmap image;
    private String name;
    private long id;
    private boolean checked;

    public MatchingCriteriaItem(long id, String name, Bitmap image) {
        this.checked = false;
        this.id = id;
        this.name = name;
        this.image = image;
    }
}