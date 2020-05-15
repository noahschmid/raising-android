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

    private static Subscription activeSubscription;

    private static final String YEARLY_SUBSCRIPTION_ID = "ch.swissef.raisingapp.subscription1y";
    private static final String SIX_MONTH_SUBSCRIPTION = "ch.swissef.raisingapp.subscription6m";
    private static final String THREE_MONTH_SUBSCRIPTION = "ch.swissef.raisingapp.subscription3m";
    private static BillingClient billingClient;

    /**
     * A list containing all skus used in this app. Sku is an internal subscription id of Google Billing
     */
    private static final List<String> SKU_LIST = new ArrayList<>(Arrays.asList(YEARLY_SUBSCRIPTION_ID, SIX_MONTH_SUBSCRIPTION, THREE_MONTH_SUBSCRIPTION));

    /**
     * A list containing all skuDetails used in this app
     */
    private static ArrayList<SkuDetails> skuDetailsArrayList = new ArrayList<>();

    /**
     * Give the SubscriptionHandler a reference to the Google Billing Client
     *
     * @param billingClient the currently used billing client
     */
    public static void setBillingClient(BillingClient billingClient) {
        Log.d(TAG, "setBillingClient: ");
        SubscriptionHandler.billingClient = billingClient;
    }

    /**
     * Check our backend for existing subscription and load the subscription
     */
    public static void loadSubscription() {
        Log.d(TAG, "loadSubscription: ");
        ApiRequestHandler.performGetRequest("subscription/android",
                response -> {
                    try {
                        Log.d(TAG, "loadSubscription: Subscription loaded: " + response.toString());
                        String sku = response.getString("subscriptionId");
                        String purchaseToken = response.getString("purchaseToken");
                        String expirationDate = response.getString("expiresDate");
                        Log.d(TAG, "loadSubscription: " + expirationDate);
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

    private static boolean verifySubscription() {
        AtomicBoolean subscriptionVerified = new AtomicBoolean(false);
        ApiRequestHandler.performGetRequest("/subscription/android/verify",
                response -> {
                    subscriptionVerified.set(true);
                    return null;
                }, volleyError -> {
                    Log.e(TAG, "verifySubscription: Subscription invalid " + volleyError.getMessage());
                    return null;
                });
        return true;
    }

    /**
     * Fetch all subscriptions from the Google Billing server and store them in a variable
     */
    public static ArrayList<SkuDetails> getSkuDetails() {
        Log.d(TAG, "getSkuDetails: ");
        skuDetailsArrayList.clear();
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

    /**
     * Check with our backend, if a purchased subscription is valid
     *
     * @param purchase The purchased subscription that is to be invalidated
     * @return true if subscription is valid, false if subscription is invalid
     */
    public static boolean validatePurchase(Purchase purchase) {
        Log.d(TAG, "validatePurchase: ");
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

    /**
     * Helper function that retrieves skuDetails from our list based on the sku
     *
     * @param sku The sku whose details should be fetched
     * @return The skuDetails of the given sku, if there is no skuDetails with the given sku, return null
     */
    private static SkuDetails getSkuDetailsFromSku(String sku) {
        Log.d(TAG, "getSkuDetailsFromSku: ");
        for (int i = 0; i < skuDetailsArrayList.size(); i++) {
            if (skuDetailsArrayList.get(i).getSku().equals(sku)) {
                Log.d(TAG, "getSkuDetailsFromSku: " + skuDetailsArrayList.get(i).toString());
                return skuDetailsArrayList.get(i);
            }
        }
        return null;
    }

    /**
     * Set users active subscription with a known expiration date.
     * This is only used, when setting a subscription that is fetched from our backend
     *
     * @param sku            The sku of the users subscription
     * @param purchaseToken  The purchaseToken of the users subscription
     * @param expirationDate A String representation of the expiration date of the users subscription
     */
    private static void setActiveSubscriptionWithExpiration(String sku, String purchaseToken, String expirationDate) {
        // get date from String
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = simpleDateFormat.parse(expirationDate);
            Log.d(TAG, "setActiveSubscriptionWithExpiration: Parsed Date " + date);
            if (date != null)
                calendar.setTime(date);
        } catch (ParseException e) {
            Log.e(TAG, "setActiveSubscriptionWithExpiration: ParseException" + e.getMessage());
        }

        if (verifySubscription(calendar)) {
            Log.d(TAG, "setActiveSubscriptionWithExpiration: Subscription verified");
            Subscription subscription = new Subscription();
            subscription.setSku(sku);
            subscription.setPurchaseToken(purchaseToken);

            subscription.setExpirationDate(calendar);
            subscription.setPurchaseDate(getRespectiveDate(calendar, getSkuDurationFromSku(sku), false));

            Log.d(TAG, "setActiveSubscriptionWithExpiration: Selected subscription " + subscription.getSku() + " Expiration: " + subscription.getExpirationDate().getTime());
            activeSubscription = subscription;
        }
    }

    /**
     * Helper method, that determines if the date given in calendar is before or after the current date
     *
     * @param expirationDate The expiration date of a subscription
     * @return true, if expiration is after today, which means the subscription is valid
     * false, if expiration is before today
     */
    private static boolean verifySubscription(Calendar expirationDate) {
        Log.d(TAG, "verifySubscription: " + !(expirationDate.getTime().before(new Date())) + " Expiration: " + expirationDate.getTime() + " Today: " + new Date());
        if (expirationDate.getTime().before(new Date())) {
            activeSubscription = null;
            return false;
        } else {
            return true;
        }
    }

    /**
     * Set users active subscription with a known purchase date.
     * This is used, if the users buys a subscription via our app.
     *
     * @param sku           The sku of the users subscription
     * @param purchaseToken The purchaseToken of the users subscription
     * @param calendar      A calendar instance containing the purchase date of the subscription
     */
    public static void setActiveSubscriptionWithPurchase(String sku, String purchaseToken, Calendar calendar) {
        Subscription subscription = new Subscription();
        subscription.setSku(sku);
        subscription.setPurchaseToken(purchaseToken);

        subscription.setPurchaseDate(calendar);
        subscription.setExpirationDate(getRespectiveDate(calendar, getSkuDurationFromSku(sku), true));
        Log.d(TAG, "setActiveSubscriptionWithPurchase: Expiration" + subscription.getExpirationDate().getTime());
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

    /**
     * Calculate the purchase / expiration date of a subscription based on the duration
     *
     * @param calendar             A calendar instance containing either the purchase or the expiration date
     * @param subscriptionDuration The duration of the subscription whose purchase / expiration date should be calculated
     * @param calculateExpiration  true if an expiration date should be calculated, false if a purchase date should be calculated
     * @return A calendar instance containing the calculated date
     */
    private static Calendar getRespectiveDate(Calendar calendar, int subscriptionDuration, boolean calculateExpiration) {
        if (calculateExpiration) {
            calendar.add(Calendar.MONTH, subscriptionDuration);
        } else {
            calendar.add(Calendar.MONTH, (subscriptionDuration * (-1)));
        }
        Log.d(TAG, "getRespectiveDate: Calculated Date " + calendar.toString());
        return calendar;
    }

    /**
     * Get the users active subscription
     *
     * @return The users active subscription, null if the user does not have a subscription
     */
    public static Subscription getActiveSubscription() {
        if (activeSubscription != null) {
            verifySubscription(activeSubscription.getExpirationDate());
        }
        return activeSubscription;
    }

    /**
     * Check if the user has a valid subscription
     *
     * @return true, if user has valid subscription
     * false, if user does not have a valid subscription
     */
    public static boolean hasValidSubscription() {
        if (getActiveSubscription() != null) {
            verifySubscription(activeSubscription.getExpirationDate());
        }
        //return getActiveSubscription() != null;
        return false;
    }

    public static void removeSubscription() {
        activeSubscription = null;
    }
}
