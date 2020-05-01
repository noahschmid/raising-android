package com.raising.app.models;

import com.raising.app.R;
import com.raising.app.util.InternalStorageHandler;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import lombok.Data;

@Data
public class Subscription implements Serializable {

    private SubscriptionType subscriptionType;
    private boolean subscriptionActive;
    private Calendar purchaseDate;
    private Calendar expirationDate;

    public String getExpirationDateString() {
        if(subscriptionActive) {
            return getString(R.string.subscription_automatic_extension);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return getString(R.string.subscription_expires) + " " + dateFormat.format(expirationDate.getTime());
        }
    }

    private String getString(int id) {
        return InternalStorageHandler.getContext().getResources().getString(id);
    }
}
