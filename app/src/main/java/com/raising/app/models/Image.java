package com.raising.app.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import lombok.Data;

@Data
public class Image implements Serializable {
    private long id = -1;
    private long accountId = -1;
    private Bitmap image;


    public Image(Long id, Bitmap image) {
        this.image = image;
        this.id = id;
    }

    public Image(Bitmap image) {
        this.image = image;
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
}
