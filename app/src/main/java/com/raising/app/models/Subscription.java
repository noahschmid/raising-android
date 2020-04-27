package com.raising.app.models;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Subscription implements Serializable {

    private SubscriptionType subscriptionType;
    private Date expirationDate;
    private boolean automaticExtension = false;
}
