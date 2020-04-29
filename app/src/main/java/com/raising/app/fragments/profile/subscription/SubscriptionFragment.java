package com.raising.app.fragments.profile.subscription;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Account;
import com.raising.app.models.Subscription;
import com.raising.app.models.SubscriptionType;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SubscriptionFragment extends RaisingFragment {
    private final String TAG = "SubscriptionsFragment";

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

        subscriptionsLayout = view.findViewById(R.id.subscriptions_layout);
        btnCancelSubscription = view.findViewById(R.id.button_cancel_subscription);
        btnCancelSubscription.setVisibility(View.GONE);
        refreshSubscriptionsLayout();
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
                TextView subscriptionExpiration = subscriptionLayout.findViewById(R.id.subscription_expiration);

                // hide views that are not needed for unselected subscriptions
                activeSubscription.setVisibility(View.GONE);
                subscriptionExpiration.setVisibility(View.INVISIBLE);

                subscriptionTitle.setText(subscriptionType.getTitle());
                card.setOnClickListener(v -> {
                    if (currentAccount.getActiveSubscription() == null) {
                        setCurrentSubscription(subscriptionType);
                    } else {
                        setNextSubscription(subscriptionType);
                    }
                    refreshSubscriptionsLayout();
                });

                // adjust subscription cards according to current subscription
                if (currentAccount.getActiveSubscription() != null) {
                    // setup layout for current subscription
                    if (currentAccount.getActiveSubscription().getSubscriptionType() == subscriptionType) {
                        card.setStrokeColor(getResources().getColor(R.color.raisingPositiveAccent, null));
                        card.setStrokeWidth(8);
                        activeSubscription.setVisibility(View.VISIBLE);
                        subscriptionExpiration.setVisibility(View.VISIBLE);
                        subscriptionExpiration.setText(currentAccount.getActiveSubscription().getExpirationDateString());
                        btnCancelSubscription.setVisibility(View.VISIBLE);
                        btnCancelSubscription.setOnClickListener(v -> {
                            if (currentAccount.getActiveSubscription().isSubscriptionActive()) {
                                new AlertDialog.Builder(this.getContext())
                                        .setTitle(getString(R.string.subscription_cancel))
                                        .setMessage(getString(R.string.subscription_cancel_text))
                                        .setPositiveButton(getString(R.string.yes_text), (dialog, which) -> {
                                            currentAccount.getActiveSubscription().setSubscriptionActive(false);
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
                    }

                    if (subscriptionType.getDuration() < currentAccount.getActiveSubscription().getSubscriptionType().getDuration()) {
                        // subscriptions cheaper than the one currently active
                        activeSubscription.setVisibility(View.VISIBLE);
                        activeSubscription.setText(getString(R.string.subscription_downgrade));
                    }

                    if (subscriptionType.getDuration() > currentAccount.getActiveSubscription().getSubscriptionType().getDuration()) {
                        // subscriptions more expensive than current subscription
                        activeSubscription.setVisibility(View.VISIBLE);
                        activeSubscription.setText(getString(R.string.subscription_upgrade));
                    }
                }
                subscriptionsLayout.addView(subscriptionLayout);
            }
        });
    }

    private void setNextSubscription(SubscriptionType subscriptionType) {
        currentAccount.getActiveSubscription().setSubscriptionActive(false);
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionType);
        subscription.setPurchaseDate(currentAccount.getActiveSubscription().getExpirationDate());

        // set expiration date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentAccount.getActiveSubscription().getPurchaseDate().getTime());
        calendar.add(Calendar.MONTH, subscriptionType.getDuration());
        subscription.setExpirationDate(calendar);

        Log.d(TAG, "setNextSubscription: Next subscription" + subscription.getSubscriptionType().getTitle() + " " + subscription.getPurchaseDate().getTime().toString());
        // currentAccount.setNextSubscription(subscription);
    }

    private void setCurrentSubscription(SubscriptionType subscriptionType) {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionType);
        subscription.setSubscriptionActive(true);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        subscription.setPurchaseDate(calendar);
        // set expiration date
        calendar.add(Calendar.MONTH, subscriptionType.getDuration());
        subscription.setExpirationDate(calendar);

        Log.d(TAG, "setCurrentSubscription: Selected subscription " + subscription.getSubscriptionType().getTitle() + " " + subscription.getPurchaseDate().getTime().toString());
        currentAccount.setActiveSubscription(subscription);
    }
}
