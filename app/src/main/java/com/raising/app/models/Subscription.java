package com.raising.app.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import lombok.Data;

@Data
public class Subscription implements Serializable {

    private SubscriptionType subscriptionType;
    private Calendar purchaseDate;
    private Calendar expirationDate;

    public String getExpirationDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(expirationDate.getTime());
    }
}
