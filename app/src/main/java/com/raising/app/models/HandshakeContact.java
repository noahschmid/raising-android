package com.raising.app.models;

import android.graphics.Bitmap;

import lombok.Data;

@Data
public class HandshakeContact {
    private long id;
    private String name;
    private String email;
    private int phone;
    private Image image;

    public HandshakeContact(long id, String name, String email, int phone, Image image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }

    public Bitmap getBitmap() {
        return image.getBitmap();
    }

}
