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
        SKU_LIST.add(YEARLY_SUBSCRIPTION_ID);
        SKU_LIST.add(SIX_MONTH_SUBSCRIPTION);
        SKU_LIST.add(THREE_MONTH_SUBSCRIPTION);

        billingClient = BillingClient.newBuilder(getContext())
                .setListener((billingResult, list) -> {
            //TODO: add action
        })
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished: Billing Response Code: " + billingResult.getResponseCode());
                    getSkuDetails();
                    //TODO: billing client is ready for action
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                //TODO: restart billing server here
            }
        });

    }

    private void getSkuDetails() {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(SKU_LIST).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    Log.d(TAG, "onViewCreated: SkuDetails" + skuDetailsList);
                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null)  {
                        Log.d(TAG, "getSkuDetails: Billing Response Code: " + billingResult.getResponseCode());
                        skuDetailsArrayList.addAll(skuDetailsList);
                        refreshSubscriptionsLayout();
                    } else {
                        Log.d(TAG, "onSkuDetailsResponse: Bad response: " + billingResult.getDebugMessage());
                    }
                });
    }

    private void refreshSubscriptionsLayout() {
        subscriptionsLayout.removeAllViews();

        Arrays.asList(SubscriptionType.values()).forEach(subscriptionType -> {
            // Log.d(TAG, "fillSubscriptionsList: SkuDetail" + skuDetails.getSku() + " " + skuDetails.getTitle() + " " + skuDetails.getPrice());





            // setup layout for unselected subscriptions
            View subscriptionLayout = getActivity().getLayoutInflater().inflate(R.layout.item_subscription_detail, null);

            // gather all views of a subscription card
            MaterialCardView card = subscriptionLayout.findViewById(R.id.card_subscription);
            TextView subscriptionTitle = subscriptionLayout.findViewById(R.id.subscription_active_subscription);
            TextView subscriptionName = subscriptionLayout.findViewById(R.id.subscription_title);
            TextView subscriptionDate = subscriptionLayout.findViewById(R.id.subscription_expiration);

            // hide views that are not needed for unselected subscriptions
            subscriptionTitle.setVisibility(View.GONE);
            subscriptionDate.setVisibility(View.INVISIBLE);

            subscriptionName.setText(subscriptionType.getTitle());
            card.setOnClickListener(v -> processOnCardClick(subscriptionType));

            // check if user has a subscription
            if (activeSubscriptionExists()) {
                adjustNotSelectedSubscriptions(subscriptionTitle, subscriptionType);

                // adjust card of users current subscription
                if (subscriptionType == currentAccount.getActiveSubscription().getSubscriptionType()) {
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
                if (nextSubscriptionExists() && subscriptionType == currentAccount.getNextSubscription().getSubscriptionType()) {
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

    private void processOnCardClick(SubscriptionType subscriptionType) {
        if (currentNotEqualNextSubscription()) {
            showSimpleDialog(getString(R.string.subscription_error_cannot_add_title), getString(R.string.subscription_error_cannot_add_text));
        } else if (!activeSubscriptionExists()) {
            // purchase subscription
            if(showAlertDialog(getString(R.string.subscription_dialog_subscribe_title), getString(R.string.subscribtion_dialog_subscribe_text))) {
                setCurrentSubscription(subscriptionType);
                //TODO: implement payment
            }
        } else {
            // up-/downgrade your subscription
            if(showAlertDialog(getString(R.string.subscription_dialog_subscribe_title), getString(R.string.subscribtion_dialog_subscribe_text))) {
                setNextSubscription(subscriptionType);
                //TODO: implement payment
            }
        }
        refreshSubscriptionsLayout();
    }

    private void cancelNextSubscription() {
        // setNextSubscription(SubscriptionType.NONE)
        currentAccount.setNextSubscription(null);
        Log.d(TAG, "cancelNextSubscription: NextSubscription" + currentAccount.getNextSubscription());
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

    private boolean currentEqualsNextSubscription() {
        return (activeSubscriptionExists()
                && nextSubscriptionExists()
                && currentAccount.getActiveSubscription().getSubscriptionType() == currentAccount.getNextSubscription().getSubscriptionType());
    }

    private boolean currentNotEqualNextSubscription() {
        return (activeSubscriptionExists()
                && nextSubscriptionExists()
                && currentAccount.getActiveSubscription().getSubscriptionType() != currentAccount.getNextSubscription().getSubscriptionType());
    }

    private void adjustNotSelectedSubscriptions(TextView subscriptionTitle, SubscriptionType subscriptionType) {
        // adjust cards of shorter subscription types
        if (subscriptionType.getDuration() < currentAccount.getActiveSubscription().getSubscriptionType().getDuration()) {
            // subscriptions cheaper than the one currently active, if current subscription != next subscription user cannot up-/downgrade
            if (currentEqualsNextSubscription()) {
                subscriptionTitle.setVisibility(View.VISIBLE);
                subscriptionTitle.setText(getString(R.string.subscription_downgrade));
            }
        }

        // adjust cards of longer subscription types
        if (subscriptionType.getDuration() > currentAccount.getActiveSubscription().getSubscriptionType().getDuration()) {
            // subscriptions more expensive than current subscription, if current subscription != next subscription user cannot up-/downgrade
            if (currentEqualsNextSubscription()) {
                subscriptionTitle.setVisibility(View.VISIBLE);
                subscriptionTitle.setText(getString(R.string.subscription_upgrade));
            }
        }
    }

    private boolean activeSubscriptionExists() {
        return (currentAccount.getActiveSubscription() != null);
    }

    private boolean nextSubscriptionExists() {
        return (currentAccount.getNextSubscription() != null);
    }
}
