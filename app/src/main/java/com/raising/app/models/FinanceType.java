package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.Data;

@Data
public class FinanceType implements Serializable, Model {
    private long id;
    private String name;

    @Override
    public Bitmap getImage() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof FinanceType)) {
            return false;
        }

        FinanceType c = (FinanceType) o;
        return c.getId() == this.getId();
    }
}
