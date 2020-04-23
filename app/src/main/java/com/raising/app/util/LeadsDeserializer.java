package com.raising.app.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raising.app.models.ContactData;
import com.raising.app.models.leads.Interaction;
import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.InteractionType;
import com.raising.app.models.leads.Lead;
import com.raising.app.models.leads.LeadState;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LeadsDeserializer implements JsonDeserializer<Lead> {
    private static String TAG = "LeadsDeserializer";
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public Lead deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) {
            Lead lead = new Lead();
            JsonObject jsonObject = json.getAsJsonObject();
            Gson gson = new Gson();

            lead.setId(jsonObject.get("id").getAsLong());
            lead.setAccountId(jsonObject.get("accountId").getAsLong());
            lead.setHandshakeState(InteractionState.valueOf(jsonObject.get("state").getAsString()));
            lead.setInvestmentPhaseId(jsonObject.get("investmentPhaseId").getAsLong());
            lead.setInvestorTypeId(jsonObject.get("investorTypeId").getAsLong());
            if(jsonObject.get("lastName") != null) {
                lead.setLastName(jsonObject.get("lastName").getAsString());
            }
            if(jsonObject.get("firstName") != null) {
                lead.setFirstName(jsonObject.get("firstName").getAsString());
            }
            if(jsonObject.get("companyName") != null) {
                lead.setCompanyName(jsonObject.get("companyName").getAsString());
            }
            lead.setStartup(jsonObject.get("startup").getAsBoolean());
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

                if(state == InteractionState.HANDSHAKE || state == InteractionState.INVESTOR_DECLINED ||
                        state == InteractionState.STARTUP_DECLINED) {
                    leadState = LeadState.CLOSED;
                }

                if ((state == InteractionState.INVESTOR_ACCEPTED && AuthenticationHandler.isStartup() ||
                    state == InteractionState.STARTUP_ACCEPTED && !AuthenticationHandler.isStartup()) &&
                        leadState != LeadState.CLOSED) {
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

            if(lead.getHandshakeState() == InteractionState.INVESTOR_DECLINED ||
            lead.getHandshakeState() == InteractionState.STARTUP_DECLINED) {
                leadState = LeadState.CLOSED;
            }

            try {
                String timestampString = jsonObject.get("lastchanged").getAsString().substring(0, 10);
                Log.d(TAG, "deserialize: " + timestampString);
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(timestampString);
                lead.setTimestamp(new Timestamp(date.getTime()));
            } catch (Exception e) {
                lead.setTimestamp(new Timestamp(new Date().getTime()));
                Log.e(TAG, "deserialize: error parsing timestamp: " + e.getMessage() );
            }

            lead.setState(leadState);

            return lead;
        }
}
