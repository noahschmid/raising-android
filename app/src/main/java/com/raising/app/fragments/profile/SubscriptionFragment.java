package com.raising.app.fragments.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.card.MaterialCardView;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Account;
import com.raising.app.models.Subscription;
import com.raising.app.models.SubscriptionType;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SubscriptionFragment extends RaisingFragment {
    private final String TAG = "SubscriptionsFragment";
    private BillingClient billingClient;
    private final List<String> SKU_LIST = new ArrayList<>();

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
        billingClient = BillingClient.newBuilder(getContext()).setListener((billingResult, list) -> {
            //TODO: add action
        }).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //TODO: billing client is ready for action
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                //TODO: restart billing server here
            }
        });
        SKU_LIST.add("ch.swissef.raisingapp.subscription1y");
        SKU_LIST.add("ch.swissef.raisingapp.subscription6m");
        SKU_LIST.add("ch.swissef.raisingapp.subscription3m");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(SKU_LIST).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null)  {
                            fillSubscriptionsList(skuDetailsList);
                            //TODO: successful transaction
                    } else {
                        Log.d(TAG, "onSkuDetailsResponse: " + billingResult.getDebugMessage());
                    }
                });


        refreshSubscriptionsLayout();
    }

    private void fillSubscriptionsList(List<SkuDetails> skuDetailsList) {
        skuDetailsList.forEach(skuDetails -> {
            //TODO: create instances of subscription items
        });
    }

    private void refreshSubscriptionsLayout() {

        subscriptionsLayout.removeAllViews();

        Arrays.asList(SubscriptionType.values()).forEach(subscriptionType -> {
            // create card for all subscription types except no subscription
            if (subscriptionType != SubscriptionType.NONE) {
                // setup layout for unselected subscriptions
                View subscriptionLayout = getActivity().getLayoutInflater().inflate(R.layout.item_subscription_detail, null);

                // gather all views of a subscription card
                MaterialCardView card = subscriptionLayout.findViewById(R.id.card_subscription);
                TextView activeSubscription = subscriptionLayout.findViewById(R.id.subscription_active_subscription);
                TextView subscriptionTitle = subscriptionLayout.findViewById(R.id.subscription_title);
                TextView subscriptionDate = subscriptionLayout.findViewById(R.id.subscription_expiration);

                // hide views that are not needed for unselected subscriptions
                activeSubscription.setVisibility(View.GONE);
                subscriptionDate.setVisibility(View.INVISIBLE);

                subscriptionTitle.setText(subscriptionType.getTitle());
                card.setOnClickListener(v -> {
                    if (currentAccount.getActiveSubscription() != null
                            && currentAccount.getNextSubscription() != null
                            && currentAccount.getActiveSubscription().getSubscriptionType()
                            != currentAccount.getNextSubscription().getSubscriptionType()) {
                        showSimpleDialog(getString(R.string.subscription_error_cannot_add_title), getString(R.string.subscription_error_cannot_add_text));
                    } else if (currentAccount.getActiveSubscription() == null) {
                        // purchase subscription
                        setCurrentSubscription(subscriptionType);
                    } else {
                        // up-/downgrade your subscription
                        setNextSubscription(subscriptionType);
                    }
                    refreshSubscriptionsLayout();
                });

                // check if user has a subscription
                if (currentAccount.getActiveSubscription() != null && currentAccount.getNextSubscription() != null) {

                    // adjust cards of shorter subscription types
                    if (subscriptionType.getDuration() < currentAccount.getActiveSubscription().getSubscriptionType().getDuration()) {
                        // subscriptions cheaper than the one currently active, if current subscription != next subscription user cannot up-/downgrade
                        if (currentAccount.getActiveSubscription().getSubscriptionType() == currentAccount.getNextSubscription().getSubscriptionType()) {
                            activeSubscription.setVisibility(View.VISIBLE);
                            activeSubscription.setText(getString(R.string.subscription_downgrade));
                        }
                    }

                    // adjust cards of longer subscription types
                    if (subscriptionType.getDuration() > currentAccount.getActiveSubscription().getSubscriptionType().getDuration()) {
                        // subscriptions more expensive than current subscription, if current subscription != next subscription user cannot up-/downgrade
                        if (currentAccount.getActiveSubscription().getSubscriptionType() == currentAccount.getNextSubscription().getSubscriptionType()) {
                            activeSubscription.setVisibility(View.VISIBLE);
                            activeSubscription.setText(getString(R.string.subscription_upgrade));
                        }
                    }

                    // adjust card of users current subscription
                    if (subscriptionType == currentAccount.getActiveSubscription().getSubscriptionType()) {
                        subscriptionDate.setVisibility(View.VISIBLE);
                        String expiration = getString(R.string.subscription_expires) + " " + currentAccount.getActiveSubscription().getExpirationDateString();
                        subscriptionDate.setText(expiration);
                        // if next subscription != current subscription indicate that current subscription is ending
                        if (currentAccount.getActiveSubscription().getSubscriptionType() != currentAccount.getNextSubscription().getSubscriptionType()) {
                            // signal that this subscription is ending
                            card.setStrokeColor(getResources().getColor(R.color.raisingDarkGrey, null));
                            card.setStrokeWidth(4);
                            activeSubscription.setVisibility(View.VISIBLE);
                            activeSubscription.setText(getString(R.string.subscription_ending));
                        }
                    }

                    // adjust card of users next subscription
                    if (subscriptionType == currentAccount.getNextSubscription().getSubscriptionType()) {
                        btnCancelSubscription.setVisibility(View.GONE);
                        Drawable drawable = card.getBackground();
                        drawable = DrawableCompat.wrap(drawable);
                        drawable.setTint(getResources().getColor(R.color.raisingPositiveAccentLight, null));
                        card.setBackground(drawable);
                        card.setStrokeColor(getResources().getColor(R.color.raisingDarkGrey, null));
                        card.setStrokeWidth(8);
                        activeSubscription.setVisibility(View.VISIBLE);
                        subscriptionDate.setVisibility(View.VISIBLE);
                        // if current subscription and next subscription are the same
                        if (currentAccount.getActiveSubscription().getSubscriptionType() == currentAccount.getNextSubscription().getSubscriptionType()) {
                            // if current subscription == next subscription the user has automatic extension
                            activeSubscription.setText(getString(R.string.subscription_your_subscriptions));
                            subscriptionDate.setText(getString(R.string.subscription_automatic_extension));

                            btnCancelSubscription.setVisibility(View.VISIBLE);
                            btnCancelSubscription.setOnClickListener(v -> {
                                if (currentAccount.getActiveSubscription().getSubscriptionType() == currentAccount.getNextSubscription().getSubscriptionType()) {
                                    new AlertDialog.Builder(this.getContext())
                                            .setTitle(getString(R.string.subscription_cancel))
                                            .setMessage(getString(R.string.subscription_cancel_text))
                                            .setPositiveButton(getString(R.string.yes_text), (dialog, which) -> {
                                                setNextSubscription(SubscriptionType.NONE);
                                                refreshSubscriptionsLayout();
                                                Log.d(TAG, "refreshSubscriptionsLayout: Subscription" + currentAccount.getActiveSubscription());
                                                showSimpleDialog(
                                                        getString(R.string.subscription_cancel_confirmation_title),
                                                        getString(R.string.subscription_cancel_confirmation_text)
                                                                + currentAccount.getActiveSubscription().getExpirationDateString());
                                            })
                                            .setNegativeButton(getString(R.string.cancel_text), null)
                                            .show();
                                } else {
                                    currentAccount.getActiveSubscription().setSubscriptionType(SubscriptionType.NONE);
                                    refreshSubscriptionsLayout();
                                }
                            });
                        } else {
                            // if current subscription != next subscription the next subscription starts, when the current one expires
                            activeSubscription.setText(getString(R.string.subscription_next));
                            String nextSubscriptionStart = getString(R.string.subscription_activated) + " " + currentAccount.getActiveSubscription().getExpirationDateString();
                            subscriptionDate.setText(nextSubscriptionStart);
                        }
                    }
                }
                subscriptionsLayout.addView(subscriptionLayout);
            }
        });
    }

    private void setNextSubscription(SubscriptionType subscriptionType) {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionType);
        subscription.setPurchaseDate(currentAccount.getActiveSubscription().getExpirationDate());

        // set expiration date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentAccount.getActiveSubscription().getPurchaseDate().getTime());
        calendar.add(Calendar.MONTH, subscriptionType.getDuration());
        subscription.setExpirationDate(calendar);

        Log.d(TAG, "setNextSubscription: Next subscription" + subscription.getSubscriptionType().getTitle()
                + " " + subscription.getPurchaseDate().getTime().toString()
                + " " + subscription.getExpirationDateString());
        currentAccount.setNextSubscription(subscription);
    }

    private void setCurrentSubscription(SubscriptionType subscriptionType) {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionType);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        subscription.setPurchaseDate(calendar);
        // set expiration date
        calendar.add(Calendar.MONTH, subscriptionType.getDuration());
        subscription.setExpirationDate(calendar);

        Log.d(TAG, "setCurrentSubscription: Selected subscription " + subscription.getSubscriptionType().getTitle()
                + " " + subscription.getPurchaseDate().getTime().toString()
                + " " + subscription.getExpirationDateString());
        currentAccount.setActiveSubscription(subscription);
        setNextSubscription(subscriptionType);
    }
}
