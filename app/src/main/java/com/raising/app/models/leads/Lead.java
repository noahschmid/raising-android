package com.raising.app.models.leads;

import com.raising.app.util.AuthenticationHandler;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Lead implements Serializable {
    private long id;
    private boolean isStartup;
    private String title;
    private String companyName;
    private String firstName;
    private String lastName;
    private String attribute;
    private int matchingPercent;
    private long profilePictureId;
    private long accountId;
    private long investmentPhaseId;
    private long investorTypeId;
    private Timestamp timestamp;
    private Timestamp accountLastChanged;

    private LeadState state;
    private InteractionState handshakeState;

    private ArrayList<Interaction> interactions = new ArrayList<>();

    public String getHandshakePercentString() {
        return matchingPercent + " %";
    }

    /**
     * @param type the type of interaction
     * @return found interaction
     */
    public Interaction getInteraction(InteractionType type) {
        for(Interaction interaction : interactions) {
            if(type == interaction.getInteractionType())
                return interaction;
        }

        return new Interaction(-1, type, InteractionState.EMPTY, getAccountId());
    }

    /**
     * Get all interactions of given lead
     * @return array list of interactions
     */
    public List<Interaction> getInteractions() {
        List<Interaction> interactions = new ArrayList<>();
        for(InteractionType type : InteractionType.values()) {
            interactions.add(getInteraction(type));
        }

        return interactions;
    }

    /**
     * Start a new interaction for given lead
     * @param type the type of interaction
     */
    public void startInteraction(InteractionType type) {
        if(AuthenticationHandler.isStartup()) {
            interactions.add(new Interaction(-1, type, InteractionState.STARTUP_ACCEPTED, getAccountId()));
        } else {
            interactions.add(new Interaction(-1, type, InteractionState.INVESTOR_ACCEPTED, getAccountId()));
        }
    }
}
