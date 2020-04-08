package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvestmentPhase implements Serializable, Model {
    private String name;
    private long id;
    private Bitmap image;

    public InvestmentPhase() {}
    public InvestmentPhase(long id) { this.id = id; }

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
