package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.Data;

@Data
public class TicketSize implements Serializable, Model {
    private long id;
    private int ticketSize;

    public String getName() { return toString("CHF", new String[] {"k", "M", "B"}); }

    public TicketSize(long id, int ticketSize) {
        this.id = id;
        this.ticketSize = ticketSize;
    }
    @Override
    public Bitmap getImage() {
        return null;
    }

    public void setName(String name) { }
    public String toString(String currency, String[] units) {
        String unit = "";
        int val = ticketSize;
        int i = 0;

        while(Math.log10(val) >= 3 && i < units.length) {
            val /= 1000;
            unit = units[i];
            ++i;
        }

        return currency + " " + val + unit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof TicketSize)) {
            return false;
        }

        TicketSize c = (TicketSize) o;
        return c.getId() == this.getId();
    }
}
