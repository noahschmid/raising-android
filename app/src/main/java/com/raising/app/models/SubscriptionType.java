package com.raising.app.models;

import com.raising.app.R;
import com.raising.app.util.InternalStorageHandler;

import java.io.Serializable;

import lombok.Getter;

public enum SubscriptionType implements Serializable {
    THREE(3 , getInteger(R.integer.threeMonthSubscriptionPrice)),
    SIX(6 , getInteger(R.integer.sixMonthSubscriptionPrice)),
    TWELVE(12 , getInteger(R.integer.twelveMonthSubscriptionPrice));

    @Getter
    int duration;
    @Getter
    int price;

    SubscriptionType(int duration, int price) {
        this.duration = duration;
        this.price = price;
    }

    public String getTitle() {
        return getString(R.string.currency) + " " + price + " / " + duration + " " + getString(R.string.subscription_months);
    }

    private static String getString(int id) {
        return InternalStorageHandler.getContext().getResources().getString(id);
    }

    private static int getInteger(int id) {
        return InternalStorageHandler.getContext().getResources().getInteger(id);
    }
}
