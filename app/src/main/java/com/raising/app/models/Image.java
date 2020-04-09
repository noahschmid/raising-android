package com.raising.app.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import lombok.Data;

@Data
public class Image implements Serializable {
    private long id;
    private long accountId;
    private String image;

    public Image(String image) { this.image = image; }

    public Image(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        this.image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

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
        byte[] decodedBytes = Base64.decode(image, 0);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes,
                0, decodedBytes.length);
        return decodedBitmap;
    }
}
