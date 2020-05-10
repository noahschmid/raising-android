package com.raising.app.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvestmentPhase implements Serializable, Model {
    private String name;
    private long id;
    private String icon;
    private boolean checked = false;

    public void setChecked(boolean checked) { this.checked = checked; }

    public InvestmentPhase() {}
    public InvestmentPhase(long id) { this.id = id; }

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

        if (!(o instanceof InvestmentPhase)) {
            return false;
        }

        InvestmentPhase c = (InvestmentPhase) o;
        return c.getId() == this.getId();
    }
}
