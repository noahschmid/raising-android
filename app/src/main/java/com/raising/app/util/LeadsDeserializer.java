package com.raising.app.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raising.app.models.ContactData;
import com.raising.app.models.Interaction;
import com.raising.app.models.InteractionState;
import com.raising.app.models.InteractionType;
import com.raising.app.models.Lead;
import com.raising.app.models.LeadState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LeadsDeserializer implements JsonDeserializer<Lead> {
        @Override
        public Lead deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) {
            Lead lead = new Lead();
            JsonObject jsonObject = json.getAsJsonObject();
            Gson gson = new Gson();

            lead.setId(jsonObject.get("id").getAsLong());
            lead.setAccountId(jsonObject.get("id").getAsLong());
            lead.setHandshakeState(InteractionState.valueOf(jsonObject.get("state").getAsString()));
            lead.setInvestmentPhaseId(jsonObject.get("investmentPhaseId").getAsLong());
            lead.setInvestorTypeId(jsonObject.get("investorTypeId").getAsLong());
            lead.setLastName(jsonObject.get("lastName").getAsString());
            lead.setFirstName(jsonObject.get("firstName").getAsString());
            lead.setCompanyName(jsonObject.get("companyName").getAsString());
            lead.setStartup(jsonObject.get("isStartup").getAsBoolean());
            lead.setMatchingPercent(jsonObject.get("matchingPercent").getAsInt());

            LeadState leadState = LeadState.PENDING;

            JsonArray jsonInteractions = jsonObject.get("interactions").getAsJsonArray();
            ArrayList<Interaction> interactions = new ArrayList<>();
            for(JsonElement el : jsonInteractions) {
                JsonObject obj = el.getAsJsonObject();
                Interaction interaction = new Interaction();
                InteractionState state = InteractionState.EMPTY;

                switch (obj.get("startupState").getAsString()) {
                    case "ACCEPTED":
                        state = InteractionState.STARTUP_ACCEPTED;
                        break;
                    case "DECLINED":
                        state = InteractionState.STARTUP_DECLINED;
                        break;
                }

                switch (obj.get("investorState").getAsString()) {
                    case "ACCEPTED":
                        if(state == InteractionState.STARTUP_ACCEPTED) {
                            state = InteractionState.HANDSHAKE;
                        } else {
                            state = InteractionState.INVESTOR_ACCEPTED;
                        }
                        break;
                    case "DECLINED":
                        state = InteractionState.INVESTOR_DECLINED;
                        break;
                }

                if(state == InteractionState.HANDSHAKE) {
                    leadState = LeadState.CLOSED;
                }

                if (state == InteractionState.INVESTOR_ACCEPTED && AuthenticationHandler.isStartup() ||
                    state == InteractionState.STARTUP_ACCEPTED && !AuthenticationHandler.isStartup()) {
                    leadState = LeadState.YOUR_TURN;
                }

                interaction.setInteractionState(state);
                interaction.setInteractionType(InteractionType.valueOf(obj.get("interaction").getAsString()));

                if(obj.get("data") != null) {
                    JsonObject data = obj.get("data").getAsJsonObject();
                    ContactData contactData = gson.fromJson(data.toString(), ContactData.class);
                    ContactDataHandler.processNewData(contactData);
                }

                interactions.add(interaction);
            }

            lead.setInteractions(interactions);
            lead.setState(leadState);

            return lead;
        }
}
