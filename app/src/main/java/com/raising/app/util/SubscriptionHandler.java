package com.raising.app.util;

import android.os.Looper;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.raising.app.models.Subscription;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SubscriptionHandler {
    private static final String TAG = "SubscriptionHandler";

    private static final String YEARLY_SUBSCRIPTION_ID = "ch.swissef.raisingapp.subscription1y";
    private static final String SIX_MONTH_SUBSCRIPTION = "ch.swissef.raisingapp.subscription6m";
    private static final String THREE_MONTH_SUBSCRIPTION = "ch.swissef.raisingapp.subscription3m";
    private static BillingClient billingClient;

    private static Subscription activeSubscription;

    private static final List<String> SKU_LIST = new ArrayList<>(Arrays.asList(YEARLY_SUBSCRIPTION_ID, SIX_MONTH_SUBSCRIPTION, THREE_MONTH_SUBSCRIPTION));
    private static ArrayList<SkuDetails> skuDetailsArrayList = new ArrayList<>();

    public static void setBillingClient(BillingClient billingClient) {
        SubscriptionHandler.billingClient = billingClient;
    }

    /**
     * Fetch all subscriptions from the billing server and store them in a variable
     */
    public static ArrayList<SkuDetails> getSkuDetails() {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(SKU_LIST).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    Log.d(TAG, "onViewCreated: SkuDetails" + skuDetailsList);
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        Log.d(TAG, "getSkuDetails: Billing Response Code: " + billingResult.getResponseCode());
                        skuDetailsArrayList.addAll(skuDetailsList);
                        throw new RuntimeException();
                    } else {
                        Log.d(TAG, "onSkuDetailsResponse: Bad response: " + billingResult.getDebugMessage());
                    }
                });
        try {
            Looper.loop();
        } catch (RuntimeException e) {
        }
        return skuDetailsArrayList;
    }

    public static boolean validatePurchase(Purchase purchase) {
        JSONObject object = new JSONObject();
        try {
            object.put("purchaseToken", purchase.getPurchaseToken());
            object.put("subscriptionId", purchase.getSku());
        } catch (JSONException e) {
            Log.e(TAG, "validatePurchaseWithServer: Error creating JSON" + e.getMessage());
        }
        AtomicBoolean purchaseValid = new AtomicBoolean(false);
        Log.d(TAG, "validatePurchase: JSON: " + object.toString());

        ApiRequestHandler.performPatchRequest("subscription/android",
                response -> {
                    Log.d(TAG, "validatePurchaseWithServer: Purchase valid");
                    purchaseValid.set(true);
                    throw new RuntimeException();
                }, volleyError -> {
                    Log.d(TAG, "validatePurchaseWithServer: Server Error " + volleyError.getMessage());
                    return null;
                }, object);
        try {
            Looper.loop();
        } catch (RuntimeException e) {
        }
        return purchaseValid.get();
    }

    public static void loadSubscription() {
        Log.d(TAG, "loadSubscription: ");
        ApiRequestHandler.performGetRequest("subscription/android",
                response -> {
                    try {
                        Log.d(TAG, "loadSubscription: Subscription loaded: " + response.toString());
                        String sku = response.getString("subscriptionId");
                        String purchaseToken = response.getString("purchaseToken");
                        String expirationDate = response.getString("expiresDate");
                        setActiveSubscriptionWithExpiration(sku, purchaseToken, expirationDate);
                    } catch (JSONException e) {
                        Log.e(TAG, "loadSubscription: JSONException loading subscription" + e.getMessage());
                        e.printStackTrace();
                    }
                    return null;
                }, volleyError -> {
                    Log.e(TAG, "loadSubscription: Error loading subscription: " + volleyError.getMessage());
                    return null;
                });
    }

    private static SkuDetails getSkuDetailsFromSku(String sku) {
        for (int i = 0; i < skuDetailsArrayList.size(); i++) {
            if (skuDetailsArrayList.get(i).getSku().equals(sku)) {
                Log.d(TAG, "getSkuDetailsFromSku: " + skuDetailsArrayList.get(i).toString());
                return skuDetailsArrayList.get(i);
            }
        }
        return null;
    }

    private static void setActiveSubscriptionWithExpiration(String sku, String purchaseToken, String expirationDate) {
        Subscription subscription = new Subscription();
        subscription.setSku(sku);
        subscription.setPurchaseToken(purchaseToken);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = simpleDateFormat.parse(expirationDate);
            Log.d(TAG, "setActiveSubscriptionWithExpiration: Parsed Date " + date);
            if (date != null)
                calendar.setTime(date);
        } catch (ParseException e) {
            Log.e(TAG, "setActiveSubscriptionWithExpiration: ParseException" + e.getMessage());
        }

        subscription.setExpirationDate(calendar);
        subscription.setPurchaseDate(getRespectiveDate(calendar, getSkuDurationFromSku(sku), false));

        Log.d(TAG, "setActiveSubscriptionWithExpiration: Selected subscription " + subscription.toString());
        activeSubscription = subscription;
    }


    public static void setActiveSubscriptionWithPurchase(String sku, String purchaseToken, Calendar calendar) {
        Subscription subscription = new Subscription();
        subscription.setSku(sku);
        subscription.setPurchaseToken(purchaseToken);

        subscription.setPurchaseDate(calendar);
        subscription.setExpirationDate(getRespectiveDate(calendar, getSkuDurationFromSku(sku), true));
        Log.d(TAG, "setActiveSubscriptionWithPurchase: Selected Subscription " + subscription.toString());
        activeSubscription = subscription;
    }

    /**
     * Helper method to extract the exact duration of a subscription
     *
     * @param sku The skuDetails of the subscription, this contains a string of the duration
     * @return An integer value of the duration of the subscription
     */
    public static int getSkuDurationFromSku(String sku) {
        switch (sku) {
            case YEARLY_SUBSCRIPTION_ID:
                return 12;
            case SIX_MONTH_SUBSCRIPTION:
                return 6;
            case THREE_MONTH_SUBSCRIPTION:
                return 3;
            default:
                return 0;
        }
    }

    public static Calendar getRespectiveDate(Calendar calendar, int subscriptionDuration, boolean calculateExpiration) {
        if (calculateExpiration) {
            calendar.add(Calendar.MONTH, subscriptionDuration);
        } else {
            calendar.add(Calendar.MONTH, (subscriptionDuration * (-1)));
        }
        Log.d(TAG, "getRespectiveDate: Calculated Date " + calendar.toString());
        return calendar;
    }

    public static Subscription getActiveSubscription() {
        return activeSubscription;
    }

    public static boolean hasValidSubscription() {
        return getActiveSubscription() != null;
    }
}
