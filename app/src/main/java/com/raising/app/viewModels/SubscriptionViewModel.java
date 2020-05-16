package com.raising.app.viewModels;

import android.app.Application;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.raising.app.models.PersonalSettings;
import com.raising.app.models.Subscription;
import com.raising.app.models.ViewState;
import com.raising.app.util.ApiRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SubscriptionViewModel extends AndroidViewModel {
    private static final String TAG = "SubscriptionHandler";

    private MutableLiveData<Subscription> activeSubscription = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();

    private final String YEARLY_SUBSCRIPTION_ID = "ch.swissef.raisingapp.subscription1y";
    private final String SIX_MONTH_SUBSCRIPTION = "ch.swissef.raisingapp.subscription6m";
    private final String THREE_MONTH_SUBSCRIPTION = "ch.swissef.raisingapp.subscription3m";
    private BillingClient billingClient;

    /**
     * A list containing all skus used in this app. Sku is an internal subscription id of Google Billing
     */
    private final List<String> SKU_LIST = new ArrayList<>(Arrays.asList(YEARLY_SUBSCRIPTION_ID,
            SIX_MONTH_SUBSCRIPTION, THREE_MONTH_SUBSCRIPTION));

    /**
     * A list containing all skuDetails used in this app
     */
    private MutableLiveData<ArrayList<SkuDetails>> skuDetailsArrayList = new MutableLiveData<>();

    public SubscriptionViewModel(@NonNull Application application) {
        super(application);
        viewState.setValue(ViewState.EMPTY);
        skuDetailsArrayList.setValue(new ArrayList<>());
        activeSubscription.setValue(null);
    }

    public LiveData<ArrayList<SkuDetails>> getSkuDetailsArrayList() { return skuDetailsArrayList; }

    /**
     * Get state of view model
     * @return get current state of view model
     */
    public LiveData<ViewState> getViewState() { return viewState; }

    /**
     * Check our backend for existing subscription and load the subscription
     */
    public void loadSubscription() {
        viewState.setValue(ViewState.LOADING);
        ApiRequestHandler.performGetRequest("subscription/android",
                response -> {
                    try {
                        Log.d(TAG, "loadSubscription: Subscription loaded: " + response.toString());
                        String sku = response.getString("subscriptionId");
                        String purchaseToken = response.getString("purchaseToken");
                        String expirationDate = response.getString("expiresDate");

                        viewState.setValue(ViewState.RESULT);
                        setActiveSubscriptionWithExpiration(sku, purchaseToken, expirationDate);

                        /*

                        ApiRequestHandler.performGetRequest("subscription/android/verify",
                                v -> {
                                    viewState.setValue(ViewState.RESULT);
                                    setActiveSubscriptionWithExpiration(sku, purchaseToken, expirationDate);
                                    return null;
                                }, volleyError -> {
                                    viewState.setValue(ViewState.RESULT);
                                    Log.e(TAG, "verifySubscription: Subscription invalid " + volleyError.getMessage());
                                    removeSubscription();
                                    return null;
                                });*/
                    } catch (JSONException e) {
                        viewState.setValue(ViewState.ERROR);
                        Log.e(TAG, "loadSubscription: JSONException loading subscription" + e.getMessage());
                        e.printStackTrace();
                    }
                    return null;
                }, volleyError -> {
                    if(volleyError.networkResponse != null) {
                        if(volleyError.networkResponse.statusCode == 402) {
                            removeSubscription();
                        } else {
                            viewState.setValue(ViewState.ERROR);
                            Log.e(TAG, "loadSubscription: Error loading subscription: " + volleyError.getMessage());
                        }
                    } else {
                        viewState.setValue(ViewState.ERROR);
                        Log.e(TAG, "loadSubscription: Error loading subscription: " + volleyError.getMessage());
                    }

                    return null;
                });
    }

    /**
     * Fetch all subscriptions from the Google Billing server and store them in a variable
     */
    public void loadSkuDetails(BillingClient billingClient) {
        this.billingClient = billingClient;
        viewState.setValue(ViewState.LOADING);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(SKU_LIST).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    viewState.setValue(ViewState.RESULT);
                    skuDetailsArrayList.getValue().clear();
                    loadSubscription();
                    Log.d(TAG, "onViewCreated: SkuDetails" + skuDetailsList);
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        Log.d(TAG, "getSkuDetails: Billing Response Code: " + billingResult.getResponseCode());
                        skuDetailsArrayList.getValue().addAll(skuDetailsList);
                    } else {
                        Log.d(TAG, "onSkuDetailsResponse: Bad response: " + billingResult.getDebugMessage());
                    }
                });
    }

    /**
     * Check with our backend, if a purchased subscription is valid
     *
     * @param purchase The purchased subscription that is to be invalidated
     * @return true if subscription is valid, false if subscription is invalid
     */
    public boolean validatePurchase(Purchase purchase) {
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
    private SkuDetails getSkuDetailsFromSku(String sku) {
        Log.d(TAG, "getSkuDetailsFromSku: ");
        for (int i = 0; i < skuDetailsArrayList.getValue().size(); i++) {
            if (skuDetailsArrayList.getValue().get(i).getSku().equals(sku)) {
                return skuDetailsArrayList.getValue().get(i);
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
    private void setActiveSubscriptionWithExpiration(String sku, String purchaseToken, String expirationDate) {
        // get date from String
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = simpleDateFormat.parse(expirationDate);
            if (date != null)
                calendar.setTime(date);
        } catch (ParseException e) {
            Log.e(TAG, "setActiveSubscriptionWithExpiration: ParseException" + e.getMessage());
        }

        Log.d(TAG, "setActiveSubscriptionWithExpiration: Subscription verified");
        Subscription subscription = new Subscription();
        subscription.setSku(sku);
        subscription.setPurchaseToken(purchaseToken);

        subscription.setExpirationDate(calendar);
        subscription.setPurchaseDate(getRespectiveDate(calendar, getSkuDurationFromSku(sku), false));

        Log.d(TAG, "setActiveSubscriptionWithExpiration: Selected subscription " + subscription.getSku());
        activeSubscription.setValue(subscription);
    }

    /**
     * Set users active subscription with a known purchase date.
     * This is used, if the users buys a subscription via our app.
     *
     * @param sku           The sku of the users subscription
     * @param purchaseToken The purchaseToken of the users subscription
     * @param calendar      A calendar instance containing the purchase date of the subscription
     */
    public void setActiveSubscriptionWithPurchase(String sku, String purchaseToken, Calendar calendar) {
        Subscription subscription = new Subscription();
        subscription.setSku(sku);
        subscription.setPurchaseToken(purchaseToken);

        subscription.setPurchaseDate(calendar);
        subscription.setExpirationDate(getRespectiveDate(calendar, getSkuDurationFromSku(sku), true));
        Log.d(TAG, "setActiveSubscriptionWithPurchase: Selected Subscription " + subscription.toString());
        activeSubscription.setValue(subscription);
    }

    /**
     * Helper method to extract the exact duration of a subscription
     *
     * @param sku The skuDetails of the subscription, this contains a string of the duration
     * @return An integer value of the duration of the subscription
     */
    public int getSkuDurationFromSku(String sku) {
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
    private Calendar getRespectiveDate(Calendar calendar, int subscriptionDuration, boolean calculateExpiration) {
        if (calculateExpiration) {
            calendar.add(Calendar.MONTH, subscriptionDuration);
        } else {
            calendar.add(Calendar.MONTH, (subscriptionDuration * (-1)));
        }
        return calendar;
    }

    /**
     * Get the users active subscription
     *
     * @return The users active subscription, null if the user does not have a subscription
     */
    public LiveData<Subscription> getActiveSubscription() {
        return activeSubscription;
    }

    /**
     * Check if the user has a valid subscription
     *
     * @return true, if user has valid subscription
     * false, if user does not have a valid subscription
     */
    public boolean hasValidSubscription() {
        return getActiveSubscription() != null;
    }

    /**
     * Remove the users current subscription
     */
    public void removeSubscription() {
        activeSubscription.setValue(null);
    }
}
