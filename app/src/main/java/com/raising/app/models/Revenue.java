package com.raising.app.models;

import com.raising.app.R;

import java.io.Serializable;

import lombok.Data;

@Data
public class Revenue implements Serializable {
    private int id;
    private int revenueMinId;
    private int revenueMaxId;
    private int revenueMin;
    private int revenueMax;

    public String toString(String currency, String[] units) {
        int min = revenueMin;
        int max = revenueMax;
        String minUnit = "";
        String maxUnit = "";

        int i = 0;

        while(Math.log10(min) >= 3 && i < units.length) {
            min /= 1000;
            minUnit = units[i];
            ++i;
        }

        i = 0;
        while(Math.log10(max) >= 3 && i < units.length) {
            max /= 1000;
            maxUnit = units[i];
            ++i;
        }

        if(revenueMin == revenueMax - 1) {
            return currency + " " + max + maxUnit + "+";
        }

        return currency + " " + min + minUnit + " - " + max + maxUnit;
    }
}
