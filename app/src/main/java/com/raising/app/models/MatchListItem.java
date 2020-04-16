package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.Data;

@Data
public class MatchListItem implements Serializable {
    private long accountId;
    private boolean isStartup;
    private String name;
    private String description;
    private int score;
    private String attribute;
    private long pictureId;
}
