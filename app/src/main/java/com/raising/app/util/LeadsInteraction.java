package com.raising.app.util;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.raising.app.R;
import com.raising.app.fragments.leads.LeadsContactFragment;
import com.raising.app.models.leads.Interaction;
import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.Lead;

import java.util.Objects;

public class LeadsInteraction {
    private Interaction interaction;

    private LeadsContactFragment currentFragment;

    private Button interactionButton;
    private CardView interactionCard;
    private ImageButton confirmInteraction;
    private ImageButton declineInteraction;
    private ImageView interactionArrow;

    public LeadsInteraction(Interaction interaction, LeadsContactFragment fragment) {
        this.interaction = interaction;
        currentFragment = fragment;

        // store all views for later usage
        switch (interaction.getInteractionType()) {
            case COFFEE:
                interactionButton = currentFragment.getCoffeeButton();
                interactionCard = currentFragment.getContactCoffee();
                // confirmInteraction = currentFragment.getConfirmCoffee();
                declineInteraction = currentFragment.getDeclineCoffee();
                interactionArrow = currentFragment.getArrowCoffee();
                break;
            case BUSINESSPLAN:
                interactionButton = currentFragment.getBusinessplanButton();
                interactionCard = currentFragment.getContactBusinessPlan();
                // confirmInteraction = currentFragment.getConfirmBusinessplan();
                declineInteraction = currentFragment.getDeclineBusinessplan();
                interactionArrow = currentFragment.getArrowBusinessplan();
                break;
            case PHONE_CALL:
                interactionButton = currentFragment.getPhoneButton();
                interactionCard = currentFragment.getContactPhone();
                // confirmInteraction = currentFragment.getConfirmPhone();
                declineInteraction = currentFragment.getDeclinePhone();
                interactionArrow = currentFragment.getArrowPhone();
                break;
            case EMAIL:
                interactionButton = currentFragment.getEmailButton();
                interactionCard = currentFragment.getContactEmail();
                // confirmInteraction = currentFragment.getConfirmEmail();
                declineInteraction = currentFragment.getDeclineEmail();
                interactionArrow = currentFragment.getArrowEmail();
                break;
            case VIDEO_CALL:
                interactionButton = currentFragment.getVideoButton();
                interactionCard = currentFragment.getContactVideo();
                // confirmInteraction = currentFragment.getConfirmVideo();
                declineInteraction = currentFragment.getDeclineVideo();
                interactionArrow = currentFragment.getArrowVideo();
                break;
        }
        prepareInteraction();
    }

    /**
     * Prepare the contact interaction for usage by setting the needed listeners and visibilities
     */
    private void prepareInteraction() {
        interactionArrow.setVisibility(View.GONE);

        interactionButton.setOnClickListener(v -> {
            updateInteraction(false);
            toggleContactButton();
            updateRemoteInteraction();
        });

        declineInteraction.setVisibility(View.GONE);
        declineInteraction.setOnClickListener(v -> {
            updateInteraction(true);
            toggleContactButton();
            updateRemoteInteraction();
        });
    }

    private void updateRemoteInteraction() {
        //TODO: update interaction on server
    }

    /**
     * Update the interaction state based on the previous state
     */
    private void updateInteraction(boolean decline) {
        switch (interaction.getInteractionState()) {
            case STARTUP_ACCEPTED:
                if (!(AuthenticationHandler.isStartup()) && !decline) {
                    interaction.setInteractionState(InteractionState.HANDSHAKE);
                } else if(!AuthenticationHandler.isStartup()) {
                    interaction.setInteractionState(InteractionState.INVESTOR_DECLINED);
                }
                break;
            case INVESTOR_ACCEPTED:
                if (AuthenticationHandler.isStartup() && !decline) {
                    interaction.setInteractionState(InteractionState.HANDSHAKE);
                } else if(AuthenticationHandler.isStartup()) {
                    interaction.setInteractionState(InteractionState.STARTUP_DECLINED);
                }
                break;
        }
    }

    /**
     * Update the interaction button based on the current interaction state
     */
    private void toggleContactButton() {
        Drawable drawable = interactionButton.getBackground();
        drawable = DrawableCompat.wrap(drawable);
        switch (interaction.getInteractionState()) {
            case INVESTOR_DECLINED:
            case STARTUP_DECLINED:
                interactionButton.setEnabled(false);
                drawable.setTint(ContextCompat.getColor(
                        Objects.requireNonNull(interactionButton.getContext()), R.color.raisingNegativeAccent));
                interactionButton.setText(Resources.getSystem().getString(R.string.declined_text));
                break;
            case HANDSHAKE:
                interactionButton.setVisibility(View.GONE);
                // confirmInteraction.setVisibility(View.GONE);
                declineInteraction.setVisibility(View.GONE);
                interactionArrow.setVisibility(View.VISIBLE);
                interactionCard.setOnClickListener(v -> {
                    currentFragment.enterInteractionExchange(interaction);
                });
                break;
            case STARTUP_ACCEPTED:
                if(AuthenticationHandler.isStartup()) {
                    interactionButton.setEnabled(false);
                    drawable.setTint(ContextCompat.getColor(
                            Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositiveAccent));
                    interactionButton.setText(Resources.getSystem().getString(R.string.requested_text));
                } else {
                    drawable.setTint(ContextCompat.getColor(
                            Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositive));
                    interactionButton.setText(Resources.getSystem().getString(R.string.confirm_text));
                    // confirmInteraction.setVisibility(View.VISIBLE);
                    declineInteraction.setVisibility(View.VISIBLE);
                }
                break;
            case INVESTOR_ACCEPTED:
                if(AuthenticationHandler.isStartup()) {
                    drawable.setTint(ContextCompat.getColor(
                            Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositive));
                    interactionButton.setText(Resources.getSystem().getString(R.string.confirm_text));
                    // confirmInteraction.setVisibility(View.VISIBLE);
                    declineInteraction.setVisibility(View.VISIBLE);
                } else {
                    interactionButton.setEnabled(false);
                    drawable.setTint(ContextCompat.getColor(
                            Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositiveAccent));
                    interactionButton.setText(Resources.getSystem().getString(R.string.requested_text));
                }
                break;
        }
    }
}
