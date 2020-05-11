package com.raising.app.models;

import com.android.billingclient.api.SkuDetails;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import lombok.Data;

@Data
public class Subscription implements Serializable {

    private String sku;
    private SkuDetails skuDetails;
    private String purchaseToken;
    private Calendar purchaseDate;
    private Calendar expirationDate;

    public String getExpirationDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(expirationDate.getTime());
    }
}
