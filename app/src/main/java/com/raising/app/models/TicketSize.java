package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class TicketSize implements Serializable {
    private long id;
    private int ticketSize;

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
}
