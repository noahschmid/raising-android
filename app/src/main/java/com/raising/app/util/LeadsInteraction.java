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

import com.raising.app.R;
import com.raising.app.fragments.leads.LeadsInteractionFragment;
import com.raising.app.models.ContactData;
import com.raising.app.models.leads.Interaction;
import com.raising.app.models.leads.InteractionState;

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

    private FragmentActivity activity;

    public LeadsInteraction(Interaction interaction, LinearLayout layout, @NonNull FragmentActivity activity) {
        this.interaction = interaction;
        this.layout = layout;
        this.activity = activity;
        View interactionView = activity.getLayoutInflater().inflate(R.layout.item_lead_interaction, null);
        ((TextView)interactionView.findViewById(R.id.interaction_caption))
                .setText(interaction.getInteractionType().getCaption());

        interactionArrow = (ImageView)interactionView.findViewById(R.id.interaction_arrow);
        interactionButton = (Button)interactionView.findViewById(R.id.button_request_interaction);
        declineInteraction = (ImageButton) interactionView.findViewById(R.id.button_decline_interaction);

        prepareInteraction();
        layout.addView(interactionView);
    }

    /**
     * Prepare the contact interaction for usage by setting the needed listeners and visibilities
     */
    private void prepareInteraction() {
        interactionArrow.setVisibility(View.GONE);

        interactionButton.setOnClickListener(v -> {
            Log.d(TAG, "prepareInteraction: ");
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
            case EMPTY:
                if(AuthenticationHandler.isStartup()) {
                    interaction.setInteractionState(InteractionState.STARTUP_ACCEPTED);
                } else {
                    interaction.setInteractionState(InteractionState.INVESTOR_ACCEPTED);
                }
                Log.d(TAG, "updateInteraction: " + interaction.getInteractionType().name() + "Interaction State" + interaction.getInteractionState().name());
                break;
            case STARTUP_ACCEPTED:
                if (!(AuthenticationHandler.isStartup()) && !decline) {
                    interaction.setInteractionState(InteractionState.HANDSHAKE);
                } else if(!AuthenticationHandler.isStartup()) {
                    interaction.setInteractionState(InteractionState.INVESTOR_DECLINED);
                }
                Log.d(TAG, "updateInteraction: " + interaction.getInteractionType().name() + "Interaction State" + interaction.getInteractionState().name());
                break;
            case INVESTOR_ACCEPTED:
                if (AuthenticationHandler.isStartup() && !decline) {
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

        try {
            JSONObject params = new JSONObject();
            params.put("interactionId", interaction.getId());
            params.put("interaction", interaction.getInteractionType().toString());
            params.put("data", new ContactData());
            ApiRequestHandler.performPostRequest("interaction/accept",
                    v -> {
                        return null;
                    },
                    err -> {
                        Log.e(TAG, "toggleContactButton: " + ApiRequestHandler.parseVolleyError(err) );
                        return null;
                    },
                    params);
        } catch (Exception e) {
            Log.e(TAG, "toggleContactButton: " +  e.getMessage());
        }

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
