package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.Data;

@Data
public class Support implements Serializable, Model {
    private long id;
    private String name;
    private Bitmap image;

    public Support() {}

    public Support(long id) { this.id = id; }

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
