package com.raising.app.util;

import android.os.Looper;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.raising.app.models.Subscription;

import org.json.JSONException;
import org.json.JSONObject;

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

    private static boolean validSubscription = false;
    private static Subscription activeSubscription;
    private static Subscription nextSubscription;

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

    /**
     * Set the users current subscription, this only changes, if a subscription expires or the user selects his first subscription
     *
     * @param sku The skuDetails of the chosen subscription
     */
     public static void setActiveSubscription(SkuDetails sku, String purchaseToken) {
        Subscription subscription = new Subscription();
        subscription.setPurchaseToken(purchaseToken);
        subscription.setSku(sku.getSku());
        subscription.setDuration(getSkuDuration(sku));
        subscription.setSkuDetails(sku);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        subscription.setPurchaseDate(calendar);
        // set expiration date
        calendar.add(Calendar.MONTH, subscription.getDuration());
        subscription.setExpirationDate(calendar);

        Log.d(TAG, "setCurrentSubscription: Selected subscription " + subscription.getSku()
                + " " + subscription.getPurchaseDate().getTime().toString()
                + " " + subscription.getExpirationDateString());
        activeSubscription = subscription;
        setNextSubscription(sku, purchaseToken);
    }

    /**
     * Set the users next subscription changes with the initial selection or if the user wants to up-/ or downgrade
     * This also changes, if the user cancels his subscription
     *
     * @param sku The skuDetails of the subscription that should become the users next subscription
     */
    public static void setNextSubscription(SkuDetails sku, String purchaseToken) {
        Subscription subscription = new Subscription();
        subscription.setPurchaseToken(purchaseToken);
        subscription.setSku(sku.getSku());
        subscription.setDuration(getSkuDuration(sku));
        subscription.setSkuDetails(sku);
        subscription.setPurchaseDate(activeSubscription.getExpirationDate());

        // set expiration date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(activeSubscription.getPurchaseDate().getTime());
        calendar.add(Calendar.MONTH, subscription.getDuration());
        subscription.setExpirationDate(calendar);

        Log.d(TAG, "setNextSubscription: Next subscription" + subscription.getSku()
                + " " + subscription.getPurchaseDate().getTime().toString()
                + " " + subscription.getExpirationDateString());
        nextSubscription = subscription;
    }

    /**
     * Helper method to extract the exact duration of a subscription
     *
     * @param sku The skuDetails of the subscription, this contains a string of the duration
     * @return An integer value of the duration of the subscription
     */
    public static int getSkuDuration(SkuDetails sku) {
        switch (sku.getSubscriptionPeriod()) {
            case "P1Y":
                return 12;
            case "P6M":
                return 6;
            case "P3M":
                return 3;
            default:
                return 0;
        }
    }

    public static Subscription getActiveSubscription() {
        return activeSubscription;
    }

    public static Subscription getNextSubscription() {
        return nextSubscription;
    }
}
