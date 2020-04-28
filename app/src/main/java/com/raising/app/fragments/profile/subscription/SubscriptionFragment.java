package com.raising.app.fragments.profile.subscription;

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
import java.util.Date;

public class SubscriptionFragment extends RaisingFragment {
    private final String TAG = "SubscriptionsFragment";

    private LinearLayout subscriptionsLayout;

    private Account account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customizeAppBar(getString(R.string.toolbar_profile_subscription), true);
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        account = currentAccount;

        subscriptionsLayout = view.findViewById(R.id.subscriptions_layout);
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
                Button btnSubscribe = subscriptionLayout.findViewById(R.id.button_subscribe);
                Button btnCancelSubscription = subscriptionLayout.findViewById(R.id.button_cancel_subscription);

                // hide views that are not needed for unselected subscriptions
                activeSubscription.setVisibility(View.GONE);
                subscriptionExpiration.setVisibility(View.GONE);
                btnCancelSubscription.setVisibility(View.GONE);

                subscriptionTitle.setText(subscriptionType.getTitle());
                btnSubscribe.setOnClickListener(v -> {
                    Subscription subscription = new Subscription();
                    subscription.setSubscriptionType(subscriptionType);
                    subscription.setPurchaseDate(new Date());
                    Log.d(TAG, "onViewCreated: Selected subscription " + subscription.getSubscriptionType().getTitle());
                    currentAccount.setActiveSubscription(subscription);
                    refreshSubscriptionsLayout();
                });
                // check for currently selected form of subscription
                if(currentAccount.getActiveSubscription() != null && currentAccount.getActiveSubscription().getSubscriptionType() == subscriptionType) {
                    card.setStrokeColor(getResources().getColor(R.color.raisingPositiveAccent, null));
                    card.setStrokeWidth(5);
                    activeSubscription.setVisibility(View.VISIBLE);
                    subscriptionExpiration.setVisibility(View.VISIBLE);

                    // subscriptionExpiration.setText(currentAccount.getActiveSubscription().getExpirationDate().toString());
                    btnCancelSubscription.setVisibility(View.VISIBLE);
                    btnCancelSubscription.setOnClickListener(v -> {
                        Subscription subscription = new Subscription();
                        subscription.setSubscriptionType(SubscriptionType.NONE);
                        currentAccount.setActiveSubscription(subscription);
                        refreshSubscriptionsLayout();
                    });
                }
                subscriptionsLayout.addView(subscriptionLayout);
            }
        });
    }
}
