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

import com.google.gson.Gson;
import com.raising.app.R;
import com.raising.app.fragments.leads.LeadsInteractionFragment;
import com.raising.app.models.ContactData;
import com.raising.app.models.leads.Interaction;
import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.Lead;

import org.json.JSONObject;

import java.util.Objects;

public class LeadsInteraction {
    private static final String TAG = "LeadsInteraction";

    private Interaction interaction;

    private Button interactionButton;
    private ImageButton declineInteraction;
    private ImageView interactionArrow;
    private View interactionView;
    private LinearLayout layout;
    private Lead lead;

    private FragmentActivity activity;

    public LeadsInteraction(Interaction interaction, LinearLayout layout, @NonNull FragmentActivity activity) {
        this.interaction = interaction;
        this.layout = layout;
        this.activity = activity;
        this.lead = lead;
        View interactionView = activity.getLayoutInflater().inflate(R.layout.item_lead_interaction, null);
        ((TextView)interactionView.findViewById(R.id.interaction_caption))
                .setText(interaction.getInteractionType().getCaption());

        interactionArrow = (ImageView)interactionView.findViewById(R.id.interaction_arrow);
        interactionButton = (Button)interactionView.findViewById(R.id.button_request_interaction);
        declineInteraction = (ImageButton) interactionView.findViewById(R.id.button_decline_interaction);

        prepareInteraction();
        layout.addView(interactionView);
    }

    public void enableButton(boolean enabled) {
        interactionButton.setEnabled(enabled);
    }

    /**
     * Prepare the contact interaction for usage by setting the needed listeners and visibilities
     */
    private void prepareInteraction() {
        interactionArrow.setVisibility(View.GONE);

        interactionButton.setOnClickListener(v -> {
            updateInteraction(true);
            toggleContactButton();
            updateRemoteInteraction(true);
        });

        declineInteraction.setVisibility(View.GONE);
        declineInteraction.setOnClickListener(v -> {
            updateInteraction(false);
            toggleContactButton();
            updateRemoteInteraction(false);
        });

        if(interaction.getInteractionState() == InteractionState.STARTUP_ACCEPTED &&
                !AuthenticationHandler.isStartup() || interaction.getInteractionState() ==
                InteractionState.INVESTOR_ACCEPTED && AuthenticationHandler.isStartup()) {
            interactionButton.setText(R.string.accept_text);
            interactionButton.getBackground().setTint(ContextCompat.getColor(
                    Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositive));
            declineInteraction.setVisibility(View.VISIBLE);
        }

        if(interaction.getInteractionState() == InteractionState.STARTUP_ACCEPTED &&
                AuthenticationHandler.isStartup() || interaction.getInteractionState() ==
                InteractionState.INVESTOR_ACCEPTED && !AuthenticationHandler.isStartup()) {
            interactionButton.setEnabled(false);
            interactionButton.getBackground().setTint(ContextCompat.getColor(
                    Objects.requireNonNull(interactionButton.getContext()), R.color.raisingPositiveAccent));
            interactionButton.setText(activity.getResources().getString(R.string.requested_text));
        }

        if(interaction.getInteractionState() == InteractionState.STARTUP_DECLINED ||
                interaction.getInteractionState() == InteractionState.INVESTOR_DECLINED ) {
            interactionButton.setEnabled(false);
            interactionButton.getBackground().setTint(ContextCompat.getColor(
                    Objects.requireNonNull(interactionButton.getContext()), R.color.raisingNegativeAccent));
            interactionButton.setText(activity.getResources().getString(R.string.declined_text));
            declineInteraction.setVisibility(View.GONE);
        }

        if(interaction.getInteractionState() == InteractionState.HANDSHAKE) {
            interactionArrow.setVisibility(View.VISIBLE);
            interactionButton.setVisibility(View.GONE);
            declineInteraction.setVisibility(View.GONE);
        }
    }

    private void updateRemoteInteraction(boolean accept) {
        Log.d(TAG, "updateRemoteInteraction: " + accept + " " + interaction.getId());
        try {
            JSONObject params = new JSONObject();
            params.put("interactionId", interaction.getId());
            params.put("interaction", interaction.getInteractionType().toString());
            Gson gson = new Gson();

            params.put("data", new JSONObject(gson.toJson(AccountService.getContactData())));
            params.put("accountId", interaction.getPartnerId());

            if(interaction.getId() == -1) {
                Log.d(TAG, "updateRemoteInteraction: " + params.toString());
                ApiRequestHandler.performPostRequest("interaction",
                        v -> {
                            return null;
                        },
                        err -> {
                            Log.e(TAG, "updateRemoteInteraction: " + ApiRequestHandler.parseVolleyError(err) );
                            return null;
                        },
                        params);
            } else {
                String endpoint = accept ? "interaction/accept" : "interaction/reject/" + interaction.getId();

                ApiRequestHandler.performPatchRequest(endpoint,
                        v -> {
                            return null;
                        },
                        err -> {
                            Log.e(TAG, "updateRemoteInteraction: " + ApiRequestHandler.parseVolleyError(err));
                            return null;
                        },
                        accept ? params : new JSONObject());
            }
        } catch (Exception e) {
            Log.e(TAG, "updateRemoteInteraction: " +  e.getMessage());
        }
    }

    /**
     * Update the interaction state based on the previous state
     */
    private void updateInteraction(boolean accept) {
        switch (interaction.getInteractionState()) {
            case EMPTY:
                if(AuthenticationHandler.isStartup()) {
                    interaction.setInteractionState(InteractionState.STARTUP_ACCEPTED);
                } else {
                    interaction.setInteractionState(InteractionState.INVESTOR_ACCEPTED);
                }
                Log.d(TAG, "updateInteraction: " + interaction.getInteractionType().name() + "Interaction State" + interaction.getInteractionState().name());
                break;
            case STARTUP_ACCEPTED:
                if (!(AuthenticationHandler.isStartup()) && accept) {
                    interaction.setInteractionState(InteractionState.HANDSHAKE);
                } else if(!AuthenticationHandler.isStartup()) {
                    interaction.setInteractionState(InteractionState.INVESTOR_DECLINED);
                }
                Log.d(TAG, "updateInteraction: " + interaction.getInteractionType().name() + "Interaction State" + interaction.getInteractionState().name());
                break;
            case INVESTOR_ACCEPTED:
                if (AuthenticationHandler.isStartup() && accept) {
                    interaction.setInteractionState(InteractionState.HANDSHAKE);
                } else if(AuthenticationHandler.isStartup()) {
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
                break;
            case HANDSHAKE:
                interactionButton.setVisibility(View.GONE);
                // confirmInteraction.setVisibility(View.GONE);
                declineInteraction.setVisibility(View.GONE);
                interactionArrow.setVisibility(View.VISIBLE);
                /*
                interactionView.setOnClickListener(v -> {
                    currentFragment.enterInteractionExchange(interaction);
                });*/
                break;
            case STARTUP_ACCEPTED:
                if(AuthenticationHandler.isStartup()) {
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
                if(AuthenticationHandler.isStartup()) {
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
