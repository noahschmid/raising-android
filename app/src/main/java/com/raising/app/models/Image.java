package com.raising.app.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;

import lombok.Data;

@Data
public class Image implements Serializable {
    private long id;
    private long accountId;
    private String image;

    public Image(String image) { this.image = image; }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Image)) {
            return false;
        }

        Image c = (Image) o;
        return c.getId() == this.getId();
    }

    /**
     * Decode a base64 encoded image and return a bitmap
     * @return decoded bitmap
     */
    public Bitmap getBitmap() {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
