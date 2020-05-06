package com.raising.app.fragments.profile;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.android.material.card.MaterialCardView;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Subscription;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SubscriptionFragment extends RaisingFragment {
    private final String TAG = "SubscriptionsFragment";
    private BillingClient billingClient;
    private final List<String> SKU_LIST = new ArrayList<>();
    private final ArrayList<SkuDetails> skuDetailsArrayList = new ArrayList<>();

    private final String YEARLY_SUBSCRIPTION_ID = "ch.swissef.raisingapp.subscription1y";
    private final String SIX_MONTH_SUBSCRIPTION = "ch.swissef.raisingapp.subscription6m";
    private final String THREE_MONTH_SUBSCRIPTION = "ch.swissef.raisingapp.subscription3m";

    private LinearLayout subscriptionsLayout;
    private Button btnCancelSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customizeAppBar(getString(R.string.toolbar_profile_subscription), true);
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // declare views used for subscriptions
        subscriptionsLayout = view.findViewById(R.id.subscriptions_layout);
        btnCancelSubscription = view.findViewById(R.id.button_cancel_subscription);
        btnCancelSubscription.setVisibility(View.GONE);

        // setup billing client
        SKU_LIST.add(SIX_MONTH_SUBSCRIPTION);
        SKU_LIST.add(THREE_MONTH_SUBSCRIPTION);
        SKU_LIST.add(YEARLY_SUBSCRIPTION_ID);

        billingClient = BillingClient.newBuilder(getContext())
                .setListener((billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                        //TODO: billing successful
                        Log.d(TAG, "onViewCreated: Purchase successful" + billingResult.getResponseCode());
                        Log.d(TAG, "onViewCreated: Purchase list" + list);
                        list.forEach(purchase -> {
                            handlePurchase(purchase);
                        });
                    } else {
                        Log.d(TAG, "onViewCreated: Purchase failed: " + billingResult.getResponseCode());
                        switch (billingResult.getResponseCode()) {
                            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                                break;
                            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                                showSimpleDialog(getString(R.string.billing_connection_failed_title), getString(R.string.billing_connection_issue_text));
                                break;
                            case BillingClient.BillingResponseCode.ERROR:
                                displayGenericError();
                                break;
                        }
                    }
                })
                .enablePendingPurchases()
                .build();

        startBillingConnection();
    }

    private void startBillingConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished: Billing Response Code: " + billingResult.getResponseCode());
                    getSkuDetails();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                showSimpleDialog(getString(R.string.billing_connection_failed_title),
                        getString(R.string.billing_connection_failed_text));
            }
        });
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        grantPurchase(purchase);
                    }
                });
            } else {
                grantPurchase(purchase);
            }
        }
    }

    private void showGoogleBilling(SkuDetails skuDetails, boolean hasSubscription) {
        Log.d(TAG, "showGoogleBilling: SkuDetails: " + skuDetails);
        if (billingClient.isFeatureSupported("subscriptions").getResponseCode() == BillingClient.BillingResponseCode.OK
                && billingClient.isFeatureSupported("subscriptionsUpdate").getResponseCode() == BillingClient.BillingResponseCode.OK) {
            BillingFlowParams flowParams;
            if (hasSubscription) {
                flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .setOldSku(currentAccount.getActiveSubscription().getSku(),
                                currentAccount.getActiveSubscription().getPurchaseToken())
                        .build();
            } else {
                flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
            }
            BillingResult responseCode = billingClient.launchBillingFlow(this.getActivity(), flowParams);
            Log.d(TAG, "showGoogleBilling: BillingResponseCode: " + responseCode.getDebugMessage());
        }
    }

    /**
     * Fetch all subscriptions from the billing server and store them in a variable
     */
    private void getSkuDetails() {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(SKU_LIST).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    Log.d(TAG, "onViewCreated: SkuDetails" + skuDetailsList);
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        Log.d(TAG, "getSkuDetails: Billing Response Code: " + billingResult.getResponseCode());
                        skuDetailsArrayList.addAll(skuDetailsList);
                        refreshSubscriptionsLayout();
                    } else {
                        Log.d(TAG, "onSkuDetailsResponse: Bad response: " + billingResult.getDebugMessage());
                    }
                });
    }

    private void grantPurchase(Purchase purchase) {
        if (currentAccount.getActiveSubscription() == null) {
            skuDetailsArrayList.forEach(skuDetails -> {
                if (skuDetails.getSku().equals(purchase.getSku())) {
                    setActiveSubscription(skuDetails, purchase.getPurchaseToken());
                }
            });
        } else {
            skuDetailsArrayList.forEach(skuDetails -> {
                if (skuDetails.getSku().equals(purchase.getSku())) {
                    setNextSubscription(skuDetails, purchase.getPurchaseToken());
                }
            });
        }
    }

    private void refreshSubscriptionsLayout() {
        subscriptionsLayout.removeAllViews();

        // sort skuDetails based on ascending durations
        Collections.sort(skuDetailsArrayList, Comparator.comparingInt(this::getSkuDuration));
        skuDetailsArrayList.forEach(skuDetails -> {
            Log.d(TAG, "fillSubscriptionsList: SkuDetail" + skuDetails.getSku() + " " + skuDetails.getTitle() + " " + skuDetails.getPrice());

            // setup layout for unselected subscriptions
            View subscriptionLayout = getActivity().getLayoutInflater().inflate(R.layout.item_subscription_detail, null);

            // gather all views of a subscription card
            MaterialCardView card = subscriptionLayout.findViewById(R.id.card_subscription);
            TextView subscriptionTitle = subscriptionLayout.findViewById(R.id.subscription_title);
            TextView subscriptionPriceDuration = subscriptionLayout.findViewById(R.id.subscription_price_duration);
            TextView subscriptionPriceWeek = subscriptionLayout.findViewById(R.id.subscription_price_week);
            TextView subscriptionDate = subscriptionLayout.findViewById(R.id.subscription_expiration);

            // hide views that are not needed for unselected subscriptions
            subscriptionTitle.setVisibility(View.GONE);
            subscriptionDate.setVisibility(View.INVISIBLE);

            subscriptionPriceDuration.setText(createPriceString(skuDetails, true));
            subscriptionPriceWeek.setText(createPriceString(skuDetails, false));
            card.setOnClickListener(v -> processOnCardClick(skuDetails));

            // check if user has a subscription
            if (activeSubscriptionExists()) {
                adjustNotSelectedSubscriptions(subscriptionTitle, getSkuDuration(skuDetails));

                // adjust card of users current subscription
                if (skuDetails.getSku().equals(currentAccount.getActiveSubscription().getSku())) {
                    subscriptionDate.setVisibility(View.VISIBLE);
                    String expiration = getString(R.string.subscription_expires) + " " + currentAccount.getActiveSubscription().getExpirationDateString();
                    subscriptionDate.setText(expiration);
                    // if next subscription != current subscription indicate that current subscription is ending
                    if (!nextSubscriptionExists() || currentNotEqualNextSubscription()) {
                        // signal that this subscription is ending
                        card.setStrokeColor(getResources().getColor(R.color.raisingDarkGrey, null));
                        card.setStrokeWidth(4);
                        subscriptionTitle.setVisibility(View.VISIBLE);
                        subscriptionTitle.setText(getString(R.string.subscription_ending));
                    }
                }

                // adjust card of users next subscription
                if (nextSubscriptionExists() && skuDetails.getSku().equals(currentAccount.getNextSubscription().getSku())) {
                    btnCancelSubscription.setVisibility(View.GONE);
                    Drawable drawable = card.getBackground();
                    drawable = DrawableCompat.wrap(drawable);
                    drawable.setTint(getResources().getColor(R.color.raisingPositiveAccentLight, null));
                    card.setBackground(drawable);
                    card.setStrokeColor(getResources().getColor(R.color.raisingDarkGrey, null));
                    card.setStrokeWidth(8);
                    subscriptionTitle.setVisibility(View.VISIBLE);
                    subscriptionDate.setVisibility(View.VISIBLE);
                    // if current subscription and next subscription are the same
                    if (currentEqualsNextSubscription()) {
                        // if current subscription == next subscription the user has automatic extension
                        subscriptionTitle.setText(getString(R.string.subscription_your_subscriptions));
                        subscriptionDate.setText(getString(R.string.subscription_automatic_extension));

                        btnCancelSubscription.setVisibility(View.VISIBLE);
                        btnCancelSubscription.setOnClickListener(v -> {
                            if (currentEqualsNextSubscription()) {
                                new AlertDialog.Builder(this.getContext())
                                        .setTitle(getString(R.string.subscription_cancel))
                                        .setMessage(getString(R.string.subscription_cancel_text))
                                        .setPositiveButton(getString(R.string.yes_text), (dialog, which) -> {
                                            cancelNextSubscription();
                                            Log.d(TAG, "refreshSubscriptionsLayout: Subscription" + currentAccount.getActiveSubscription());
                                            showSimpleDialog(
                                                    getString(R.string.subscription_cancel_confirmation_title),
                                                    getString(R.string.subscription_cancel_confirmation_text)
                                                            + currentAccount.getActiveSubscription().getExpirationDateString());
                                            refreshSubscriptionsLayout();
                                            btnCancelSubscription.setVisibility(View.GONE);
                                        })
                                        .setNegativeButton(getString(R.string.cancel_text), null)
                                        .show();
                            }
                        });
                    } else {
                        // if current subscription != next subscription the next subscription starts, when the current one expires
                        subscriptionTitle.setText(getString(R.string.subscription_next));
                        String nextSubscriptionStart = getString(R.string.subscription_activated) + " " + currentAccount.getActiveSubscription().getExpirationDateString();
                        subscriptionDate.setText(nextSubscriptionStart);
                    }
                }
            }
            subscriptionsLayout.addView(subscriptionLayout);
        });
    }

    /**
     * Process a click on a certain card
     *
     * @param sku The skuDetails belonging to the card that was clicked
     */
    private void processOnCardClick(SkuDetails sku) {
        if (currentNotEqualNextSubscription()) {
            showSimpleDialog(getString(R.string.subscription_error_cannot_add_title), getString(R.string.subscription_error_cannot_add_text));
        } else if (!activeSubscriptionExists()) {
            // purchase subscription
            if (showAlertDialog(getString(R.string.subscription_dialog_subscribe_title),
                    getString(R.string.subscribtion_dialog_subscribe_text))) {
                showGoogleBilling(sku, false);
            }
        } else {
            // up-/downgrade your subscription
            if (showAlertDialog(getString(R.string.subscription_dialog_subscribe_title),
                    getString(R.string.subscribtion_dialog_subscribe_text))) {
                showGoogleBilling(sku, true);
            }
        }
        refreshSubscriptionsLayout();
    }

    /**
     * Cancel the next subscription, namely disable automatic subscription extension
     */
    private void cancelNextSubscription() {
        currentAccount.setNextSubscription(null);
        Log.d(TAG, "cancelNextSubscription: NextSubscription" + currentAccount.getNextSubscription());
    }

    /**
     * Set the users next subscription changes with the initial selection or if the user wants to up-/ or downgrade
     * This also changes, if the user cancels his subscription
     *
     * @param sku The skuDetails of the subscription that should become the users next subscription
     */
    private void setNextSubscription(SkuDetails sku, String purchaseToken) {
        Subscription subscription = new Subscription();
        subscription.setPurchaseToken(purchaseToken);
        subscription.setSku(sku.getSku());
        subscription.setDuration(getSkuDuration(sku));
        subscription.setSkuDetails(sku);
        subscription.setPurchaseDate(currentAccount.getActiveSubscription().getExpirationDate());

        // set expiration date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentAccount.getActiveSubscription().getPurchaseDate().getTime());
        calendar.add(Calendar.MONTH, subscription.getDuration());
        subscription.setExpirationDate(calendar);

        Log.d(TAG, "setNextSubscription: Next subscription" + subscription.getSku()
                + " " + subscription.getPurchaseDate().getTime().toString()
                + " " + subscription.getExpirationDateString());
        currentAccount.setNextSubscription(subscription);
    }

    /**
     * Set the users current subscription, this only changes, if a subscription expires or the user selects his first subscription
     *
     * @param sku The skuDetails of the chosen subscription
     */
    private void setActiveSubscription(SkuDetails sku, String purchaseToken) {
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
        currentAccount.setActiveSubscription(subscription);
        setNextSubscription(sku, purchaseToken);
    }

    /**
     * Helper method to extract the exact duration of a subscription
     *
     * @param sku The skuDetails of the subscription, this contains a string of the duration
     * @return An integer value of the duration of the subscription
     */
    private int getSkuDuration(SkuDetails sku) {
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

    private String createPriceString(SkuDetails skuDetails, boolean isDuration) {
        if (isDuration) {
            return (skuDetails.getOriginalPrice() + " / " + getSkuDuration(skuDetails) + " " + getString(R.string.subscription_months));
        } else {
            long pricePerWeek = (skuDetails.getOriginalPriceAmountMicros() / (4 * (getSkuDuration(skuDetails)))) / 1000000;
            Log.d(TAG, "createPriceString: pricePerWeek " + pricePerWeek + " " + skuDetails.getSku());
            return skuDetails.getPriceCurrencyCode() + " " + pricePerWeek + " / " + getString(R.string.subscriptions_weeks);
        }
    }

    /**
     * Returns a boolean value wether the current and next subscription are equal, both also cannot be null
     *
     * @return true, if current and next subscription are equal
     * false, if current is not the same as next subscription, or one subscription is null
     */
    private boolean currentEqualsNextSubscription() {
        return (activeSubscriptionExists()
                && nextSubscriptionExists()
                && currentAccount.getActiveSubscription().getSku().equals(currentAccount.getNextSubscription().getSku()));
    }

    /**
     * Returns a boolean value wether the current and next subscription are not equal, both also cannot be null
     *
     * @return true, if current and next subscription are not equal
     * false, if current is the same as next subscription, or one subscription is null
     */
    private boolean currentNotEqualNextSubscription() {
        return (activeSubscriptionExists()
                && nextSubscriptionExists()
                && !(currentAccount.getActiveSubscription().getSku().equals(currentAccount.getNextSubscription().getSku())));
    }

    /**
     * Adjust the title of cards, based on the users choice of subscription. The title will either say "Upgrade" or "Downgrade"
     *
     * @param subscriptionTitle A reference to the text view, whose text should be changed
     * @param duration          The duration of the subscription, where the title of the card should be adjusted
     */
    private void adjustNotSelectedSubscriptions(TextView subscriptionTitle, int duration) {
        // adjust cards of shorter subscription types
        if (duration < currentAccount.getActiveSubscription().getDuration()) {
            // subscriptions cheaper than the one currently active, if current subscription != next subscription user cannot up-/downgrade
            if (currentEqualsNextSubscription()) {
                subscriptionTitle.setVisibility(View.VISIBLE);
                subscriptionTitle.setText(getString(R.string.subscription_downgrade));
            }
        }

        // adjust cards of longer subscription types
        if (duration > currentAccount.getActiveSubscription().getDuration()) {
            // subscriptions more expensive than current subscription, if current subscription != next subscription user cannot up-/downgrade
            if (currentEqualsNextSubscription()) {
                subscriptionTitle.setVisibility(View.VISIBLE);
                subscriptionTitle.setText(getString(R.string.subscription_upgrade));
            }
        }
    }

    /**
     * Returns a boolean wether the user has an active subscription
     *
     * @return true, if user has active subscription
     * false, if user does not have an active subscription
     */
    private boolean activeSubscriptionExists() {
        return (currentAccount.getActiveSubscription() != null);
    }

    /**
     * Returns a boolean wether the user has a next subscription
     *
     * @return true, if user has next subscription
     * false, if user does not have a next subscription
     */
    private boolean nextSubscriptionExists() {
        return (currentAccount.getNextSubscription() != null);
    }
}
