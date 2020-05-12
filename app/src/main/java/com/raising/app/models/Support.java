package com.raising.app.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;

import lombok.Data;

@Data
public class Support implements Serializable, Model {
    private long id;
    private String name;
    private String icon;
    private boolean checked = false;

    public void setChecked(boolean checked) { this.checked = checked; }

    public Support() {}

    public Support(long id) { this.id = id; }

    @Override
    public Bitmap getImage() {
        byte[] decodedString = Base64.decode(icon, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Support)) {
            return false;
        }

        Support c = (Support) o;
        return c.getId() == this.getId();
    }
}
