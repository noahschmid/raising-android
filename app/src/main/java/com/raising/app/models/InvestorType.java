package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvestorType implements Serializable, Model {
    private long id;
    private String name;
    private Bitmap image;

    public InvestorType() {}
    public InvestorType(long id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof InvestorType)) {
            return false;
        }

        InvestorType c = (InvestorType) o;
        return c.getId() == this.getId();
    }
}
