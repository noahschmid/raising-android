package com.raising.app.models;

import java.io.Serializable;

public class Image implements Serializable {
    private long id;
    private long accountId;
    private String image;

    public Image(String image) { this.image = image; }
}
