package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class MatchListItem implements Serializable {
    private long accountId;
    private long relationshipId;
    private boolean isStartup;
    private String name;
    private String description;
    private int score;
    private String attribute;
    private long pictureId;
    private Timestamp accountLastChanged;
}
