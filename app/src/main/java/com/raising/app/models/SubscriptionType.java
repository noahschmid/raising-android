package com.raising.app.models;

import com.raising.app.R;
import com.raising.app.util.InternalStorageHandler;

import java.io.Serializable;

import lombok.Getter;

public enum SubscriptionType implements Serializable {
    NONE("", 0),
    THREE(getString(R.string.subscription_three_months), getInteger(R.integer.threeMonthSubscriptionPrice)),
    SIX(getString(R.string.subscription_six_months), getInteger(R.integer.sixMonthSubscriptionPrice)),
    TWELVE(getString(R.string.subscription_twelve_months), getInteger(R.integer.twelveMonthSubscriptionPrice));

    String representation;
    int price;

    SubscriptionType(String representation, int price) {
        this.representation = representation;
        this.price = price;
    }

    public String getTitle() {
        return getString(R.string.currency) + price + " / " + representation;
    }

    private static String getString(int id) {
        return InternalStorageHandler.getContext().getResources().getString(id);
    }

    private static int getInteger(int id) {
        return InternalStorageHandler.getContext().getResources().getInteger(id);
    }
}
