package com.raising.app.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvestorType implements Serializable, Model {
    private long id;
    private String name;
    private String icon;
    private boolean checked = false;

    public void setChecked(boolean checked) { this.checked = checked; }

    public InvestorType() {}
    public InvestorType(long id) { this.id = id; }

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

        if (!(o instanceof InvestorType)) {
            return false;
        }

        InvestorType c = (InvestorType) o;
        return c.getId() == this.getId();
    }
}
