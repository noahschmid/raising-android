package com.raising.app.util;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.raising.app.R;
import com.raising.app.fragments.leads.LeadsInteractionFragment;
import com.raising.app.models.ContactData;
import com.raising.app.models.leads.Interaction;
import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.Lead;

import org.json.JSONObject;

import java.util.Objects;

/**
 * This class handles a single interaction between two users (leads).
 */

public class LeadsInteraction {
    private static final String TAG = "LeadsInteraction";

    private Interaction interaction;

    private Button interactionButton;
    private ImageView declineInteraction;
    private ImageView interactionArrow;
    private ImageView interactionIcon;
    private MaterialCardView interactionCard;
    private View interactionView;
    private LinearLayout layout;
    private Lead lead;

    private FragmentActivity activity;

    public LeadsInteraction(Interaction interaction, LinearLayout layout,
                            @NonNull FragmentActivity activity, Lead lead) {
        this.interaction = interaction;
        this.layout = layout;
        this.activity = activity;
        this.lead = lead;
        interactionView = activity.getLayoutInflater().inflate(R.layout.item_lead_interaction, null);
        ((TextView) interactionView.findViewById(R.id.interaction_caption))
                .setText(interaction.getInteractionType().getCaption());

        interactionArrow = interactionView.findViewById(R.id.interaction_arrow);
        interactionButton = interactionView.findViewById(R.id.button_request_interaction);
        declineInteraction = interactionView.findViewById(R.id.button_decline_interaction);
        interactionIcon = interactionView.findViewById(R.id.leads_interaction_icon);
        interactionCard = interactionView.findViewById(R.id.leads_interaction_card);

        prepareInteraction();
        layout.addView(interactionView);
    }


    /**
     * Enable or disable the interaction button
     * @param enabled whether button shall be enabled
     */
    public void enableButton(boolean enabled) {
        interactionButton.setEnabled(enabled);
    }

    /**
     * Prepare the contact interaction for usage by setting the needed listeners and visibilities
     */
    private void prepareInteraction() {
        setInteractionIcon();
        interactionArrow.setVisibility(View.GONE);

        interactionButton.setOnClickListener(v -> {
            updateRemoteInteraction(true);
        });

        declineInteraction.setVisibility(View.GONE);
        declineInteraction.setOnClickListener(v -> {
            updateRemoteInteraction(false);
        });

        if (interaction.getInteractionState() == InteractionState.STARTUP_ACCEPTED &&
                !AuthenticationHandler.isStartup() || interaction.getInteractionState() ==
                InteractionState.INVESTOR_ACCEPTED && AuthenticationHandler.isStartup()) {
            interactionButton.setText(R.string.accept_text);
            interactionButton.getBackground().setTint(ContextCompat.getColor(
                    Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositive));
            declineInteraction.setVisibility(View.VISIBLE);
        }

        if (interaction.getInteractionState() == InteractionState.STARTUP_ACCEPTED &&
                AuthenticationHandler.isStartup() || interaction.getInteractionState() ==
                InteractionState.INVESTOR_ACCEPTED && !AuthenticationHandler.isStartup()) {
            interactionButton.setEnabled(false);
            interactionButton.getBackground().setTint(ContextCompat.getColor(
                    Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositiveAccent));
            interactionButton.setText(activity.getResources().getString(R.string.requested_text));
        }

        if (interaction.getInteractionState() == InteractionState.STARTUP_DECLINED ||
                interaction.getInteractionState() == InteractionState.INVESTOR_DECLINED) {
            interactionButton.setEnabled(false);
            interactionButton.getBackground().setTint(ContextCompat.getColor(
                    Objects.requireNonNull(interactionButton.getContext()), R.color.raisingNegativeAccent));
            interactionButton.setText(activity.getResources().getString(R.string.declined_text));
            declineInteraction.setVisibility(View.GONE);
        }

        if (interaction.getInteractionState() == InteractionState.HANDSHAKE) {
            interactionArrow.setVisibility(View.VISIBLE);
            interactionButton.setVisibility(View.GONE);
            declineInteraction.setVisibility(View.GONE);
            interactionCard.setOnClickListener(v -> {
                interaction.getInteractionType().executeAction(lead);
            });
        }
    }

    /**
     * Set the correct icon for the interaction
     */
    private void setInteractionIcon() {
        switch (interaction.getInteractionType()) {
            case COFFEE:
                interactionIcon.setImageDrawable(ContextCompat.getDrawable(interactionView.getContext(), R.drawable.ic_raising_contact_coffee));
                break;
            case BUSINESS_PLAN:
                interactionIcon.setImageDrawable(ContextCompat.getDrawable(interactionView.getContext(), R.drawable.ic_raising_contact_bp));
                break;
            case PHONE_CALL:
                interactionIcon.setImageDrawable(ContextCompat.getDrawable(interactionView.getContext(), R.drawable.ic_raising_contact_phone));
                break;
            case EMAIL:
                interactionIcon.setImageDrawable(ContextCompat.getDrawable(interactionView.getContext(), R.drawable.ic_raising_contact_mail));
                break;
            case VIDEO_CONFERENCE:
                interactionIcon.setImageDrawable(ContextCompat.getDrawable(interactionView.getContext(), R.drawable.ic_raising_contact_video));
                break;
        }
    }

    /**
     * Send backend requests when an interaction gets accepted/declined
     * @param accept whether the interaction was accepted or not
     */
    private void updateRemoteInteraction(boolean accept) {
        Log.d(TAG, "updateRemoteInteraction: " + accept + " " + interaction.getId());
        try {
            JSONObject params = new JSONObject();
            params.put("interaction", interaction.getInteractionType().toString());
            Gson gson = new Gson();

            ContactData cData = AccountService.getContactData();
            cData.setAccountId(interaction.getPartnerId());

            params.put("data", new JSONObject(gson.toJson(cData)));
            params.put("relationshipId", interaction.getRelationshipId());

            Log.d(TAG, "updateRemoteInteraction: " + params.toString());

            if (interaction.getId() == -1) {
                Log.d(TAG, "updateRemoteInteraction: " + params.toString());
                ApiRequestHandler.performPostRequest("interaction",
                        v -> {
                            updateInteraction(true);
                            toggleContactButton();
                            return null;
                        },
                        err -> {
                            Log.e(TAG, "updateRemoteInteraction: " + ApiRequestHandler.parseVolleyError(err));
                            return null;
                        },
                        params);
            } else {
                String endpoint = accept ? "interaction/" + interaction.getId() + "/accept" :
                        "interaction/" + interaction.getId() + "/decline";

                ApiRequestHandler.performPatchRequest(endpoint,
                        v -> {
                            try {
                                if (v != null) {
                                    ContactData contactData = gson.fromJson(v.toString(), ContactData.class);
                                    contactData.setAccountId(lead.getAccountId());
                                    ContactDataHandler.processNewData(contactData);
                                }

                                updateInteraction(accept);
                                toggleContactButton();
                            } catch (Exception e) {
                                Log.e(TAG, "updateRemoteInteraction: " + e.getMessage());
                            }
                            return null;
                        },
                        err -> {
                            Log.e(TAG, "updateRemoteInteraction: " + ApiRequestHandler.parseVolleyError(err));
                            return null;
                        },
                        accept ? params : new JSONObject());
            }
        } catch (Exception e) {
            Log.e(TAG, "updateRemoteInteraction: " + e.getMessage());
        }
    }

    /**
     * Update the interaction state based on the previous state
     */
    private void updateInteraction(boolean accept) {
        switch (interaction.getInteractionState()) {
            case EMPTY:
                if (AuthenticationHandler.isStartup()) {
                    interaction.setInteractionState(InteractionState.STARTUP_ACCEPTED);
                } else {
                    interaction.setInteractionState(InteractionState.INVESTOR_ACCEPTED);
                }
                Log.d(TAG, "updateInteraction: " + interaction.getInteractionType().name() + "Interaction State" + interaction.getInteractionState().name());
                break;
            case STARTUP_ACCEPTED:
                if (!(AuthenticationHandler.isStartup()) && accept) {
                    interaction.setInteractionState(InteractionState.HANDSHAKE);
                } else if (!AuthenticationHandler.isStartup()) {
                    interaction.setInteractionState(InteractionState.INVESTOR_DECLINED);
                }
                Log.d(TAG, "updateInteraction: " + interaction.getInteractionType().name() + "Interaction State" + interaction.getInteractionState().name());
                break;
            case INVESTOR_ACCEPTED:
                if (AuthenticationHandler.isStartup() && accept) {
                    interaction.setInteractionState(InteractionState.HANDSHAKE);
                } else if (AuthenticationHandler.isStartup()) {
                    interaction.setInteractionState(InteractionState.STARTUP_DECLINED);
                }
                Log.d(TAG, "updateInteraction: " + interaction.getInteractionType().name() + "Interaction State" + interaction.getInteractionState().name());
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
                interactionButton.setText(activity.getResources().getString(R.string.declined_text));
                declineInteraction.setVisibility(View.GONE);
                break;
            case HANDSHAKE:
                interactionButton.setVisibility(View.GONE);
                // confirmInteraction.setVisibility(View.GONE);
                declineInteraction.setVisibility(View.GONE);
                interactionArrow.setVisibility(View.VISIBLE);
                interactionCard.setOnClickListener(v -> {
                    interaction.getInteractionType().executeAction(lead);
                });
                break;
            case STARTUP_ACCEPTED:
                if (AuthenticationHandler.isStartup()) {
                    interactionButton.setEnabled(false);
                    drawable.setTint(ContextCompat.getColor(
                            Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositiveAccent));
                    interactionButton.setText(activity.getResources().getString(R.string.requested_text));
                } else {
                    drawable.setTint(ContextCompat.getColor(
                            Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositive));
                    interactionButton.setText(activity.getResources().getString(R.string.confirm_text));
                    // confirmInteraction.setVisibility(View.VISIBLE);
                    declineInteraction.setVisibility(View.VISIBLE);
                }
                break;
            case INVESTOR_ACCEPTED:
                if (AuthenticationHandler.isStartup()) {
                    drawable.setTint(ContextCompat.getColor(
                            Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositive));
                    interactionButton.setText(activity.getResources().getString(R.string.confirm_text));
                    // confirmInteraction.setVisibility(View.VISIBLE);
                    declineInteraction.setVisibility(View.VISIBLE);
                } else {
                    interactionButton.setEnabled(false);
                    drawable.setTint(ContextCompat.getColor(
                            Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositiveAccent));
                    interactionButton.setText(activity.getResources().getString(R.string.requested_text));
                }
                break;
        }
    }
}
