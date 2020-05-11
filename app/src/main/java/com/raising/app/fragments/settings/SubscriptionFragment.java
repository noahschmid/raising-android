package com.raising.app.fragments.settings;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.google.android.material.tabs.TabLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Subscription;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.SubscriptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SubscriptionFragment extends RaisingFragment {
    private final String TAG = "SubscriptionsFragment";
    private BillingClient billingClient;
    private ArrayList<SkuDetails> skuDetailsArrayList = new ArrayList<>();

    private LinearLayout subscriptionsLayout;
    private Button btnManageSubscription;
    private TextView manageSubscriptionText;

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
        btnManageSubscription = view.findViewById(R.id.button_manage_subscription);
        btnManageSubscription.setVisibility(View.GONE);
        manageSubscriptionText = view.findViewById(R.id.subscription_manage_text);
        manageSubscriptionText.setVisibility(View.GONE);

        billingClient = BillingClient.newBuilder(getContext())
                .setListener((billingResult, list) -> {
                    // onPurchasesUpdated
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                        Log.d(TAG, "onViewCreated: Purchase successful" + billingResult.getResponseCode());
                        Log.d(TAG, "onViewCreated: Purchase list" + list);
                        //TODO: with Deferred the subscription list comes back as null but transaction has completed
                        list.forEach(purchase -> {
                            if (SubscriptionHandler.validatePurchase(purchase)) {
                                handlePurchase(purchase);
                            }
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
                                refreshSubscriptionsLayout();
                                break;
                            case BillingClient.BillingResponseCode.ERROR:
                                showGenericError();
                                break;
                        }
                    }
                })
                .enablePendingPurchases()
                .build();

        startBillingConnection();
        SubscriptionHandler.setBillingClient(billingClient);
    }

    private void startBillingConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished: Billing Response Code: " + billingResult.getResponseCode());
                    skuDetailsArrayList = SubscriptionHandler.getSkuDetails();
                    refreshSubscriptionsLayout();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                showSimpleDialog(getString(R.string.billing_connection_failed_title),
                        getString(R.string.billing_connection_failed_text));
            }
        });
    }

    private void showGoogleBilling(SkuDetails skuDetails, boolean hasSubscription) {
        Log.d(TAG, "showGoogleBilling: SkuDetails: " + skuDetails);
        if (billingClient.isFeatureSupported("subscriptions").getResponseCode() == BillingClient.BillingResponseCode.OK
                && billingClient.isFeatureSupported("subscriptionsUpdate").getResponseCode() == BillingClient.BillingResponseCode.OK) {
            BillingFlowParams flowParams;
            if (hasSubscription) {
                Log.d(TAG, "showGoogleBilling: Change subscription");
                flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .setOldSku(SubscriptionHandler.getActiveSubscription().getSku(),
                                SubscriptionHandler.getActiveSubscription().getPurchaseToken())
                        .build();
            } else {
                Log.d(TAG, "showGoogleBilling: New subscription");
                flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
            }
            BillingResult responseCode = billingClient.launchBillingFlow(this.getActivity(), flowParams);
            Log.d(TAG, "showGoogleBilling: BillingResponseCodeMessage: " + responseCode.getDebugMessage());
        }
    }

    private void handlePurchase(Purchase purchase) {
        Log.d(TAG, "handlePurchase: Purchase State " + purchase.getPurchaseState());
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            Log.d(TAG, "handlePurchase: Purchase Acknowledged " + purchase.isAcknowledged());
            if (!purchase.isAcknowledged()) {
                Log.d(TAG, "handlePurchase: Purchase Token: " + purchase.getPurchaseToken());
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    Log.d(TAG, "handlePurchase: AcknowledgementCode " + billingResult.getResponseCode());
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "handlePurchase: Purchase Acknowledged " + purchase.isAcknowledged());
                        grantPurchase(purchase);
                    }
                });
            } else {
                grantPurchase(purchase);
            }
        }
    }

    private void grantPurchase(Purchase purchase) {
        skuDetailsArrayList.forEach(skuDetails -> {
            if (skuDetails.getSku().equals(purchase.getSku())) {
                SubscriptionHandler.setActiveSubscription(skuDetails, purchase.getPurchaseToken());
            }
        });
        refreshSubscriptionsLayout();
    }


    private void refreshSubscriptionsLayout() {
        subscriptionsLayout.removeAllViews();

        // sort skuDetails based on ascending durations
        Collections.sort(skuDetailsArrayList, Comparator.comparingInt(SubscriptionHandler::getSkuDuration));
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

            if (SubscriptionHandler.getNextSubscription() != null) {
                btnManageSubscription.setVisibility(View.VISIBLE);
                manageSubscriptionText.setVisibility(View.VISIBLE);
                btnManageSubscription.setOnClickListener(v -> {
                            String baseUri = "https://play.google.com/store/account/subscriptions";
                            if (!currentEqualsNextSubscription()) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUri));
                                startActivity(browserIntent);
                            } else if (currentEqualsNextSubscription()) {
                                String uri = baseUri + "?sku=" + SubscriptionHandler.getNextSubscription().getSku() + "&package=" + "com.raising.app";
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(browserIntent);
                            }
                            refreshSubscriptionsLayout();
                        }
                );
            }
            // check if user has a subscription
            if (activeSubscriptionExists()) {
                adjustNotSelectedSubscriptions(subscriptionTitle, SubscriptionHandler.getSkuDuration(skuDetails));

                // adjust card of users active subscription
                if (skuDetails.getSku().equals(SubscriptionHandler.getActiveSubscription().getSku())) {
                    Drawable drawable = card.getBackground();
                    drawable = DrawableCompat.wrap(drawable);
                    drawable.setTint(getResources().getColor(R.color.raisingPositiveAccentLight, null));
                    card.setBackground(drawable);
                    card.setStrokeColor(getResources().getColor(R.color.raisingDarkGrey, null));
                    card.setStrokeWidth(8);
                    subscriptionTitle.setVisibility(View.VISIBLE);
                    subscriptionDate.setVisibility(View.VISIBLE);

                    subscriptionTitle.setText(getString(R.string.subscription_your_subscriptions));
                    subscriptionDate.setText(getString(R.string.subscription_automatic_extension));
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
            if (showActionDialog(getString(R.string.subscription_dialog_subscribe_title),
                    getString(R.string.subscribtion_dialog_subscribe_text))) {
                showGoogleBilling(sku, false);
            }
        } else {
            // up-/downgrade your subscription
            if (showActionDialog(getString(R.string.subscription_dialog_subscribe_title),
                    getString(R.string.subscribtion_dialog_subscribe_text))) {
                showGoogleBilling(sku, true);
            }
        }
        refreshSubscriptionsLayout();
    }

    private String createPriceString(SkuDetails skuDetails, boolean isDuration) {
        if (isDuration) {
            return (skuDetails.getOriginalPrice() + " / " + SubscriptionHandler.getSkuDuration(skuDetails) + " " + getString(R.string.subscription_months));
        } else {
            long pricePerWeek = (skuDetails.getOriginalPriceAmountMicros() / (4 * (SubscriptionHandler.getSkuDuration(skuDetails)))) / 1000000;
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
                && SubscriptionHandler.getActiveSubscription().getSku().equals(SubscriptionHandler.getNextSubscription().getSku()));
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
                && !(SubscriptionHandler.getActiveSubscription().getSku().equals(SubscriptionHandler.getNextSubscription().getSku())));
    }

    /**
     * Adjust the title of cards, based on the users choice of subscription. The title will either say "Upgrade" or "Downgrade"
     *
     * @param subscriptionTitle A reference to the text view, whose text should be changed
     * @param duration          The duration of the subscription, where the title of the card should be adjusted
     */
    private void adjustNotSelectedSubscriptions(TextView subscriptionTitle, int duration) {
        // adjust cards of shorter subscription types
        if (duration < SubscriptionHandler.getSkuDuration(SubscriptionHandler.getActiveSubscription().getSkuDetails())) {
            // subscriptions cheaper than the one currently active, if current subscription != next subscription user cannot up-/downgrade
            if (currentEqualsNextSubscription()) {
                subscriptionTitle.setVisibility(View.VISIBLE);
                subscriptionTitle.setText(getString(R.string.subscription_downgrade));
            }
        }

        // adjust cards of longer subscription types
        if (duration > SubscriptionHandler.getSkuDuration(SubscriptionHandler.getActiveSubscription().getSkuDetails())) {
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
        return (SubscriptionHandler.getActiveSubscription() != null);
    }

    /**
     * Returns a boolean wether the user has a next subscription
     *
     * @return true, if user has next subscription
     * false, if user does not have a next subscription
     */
    private boolean nextSubscriptionExists() {
        return (SubscriptionHandler.getNextSubscription() != null);
    }
}
