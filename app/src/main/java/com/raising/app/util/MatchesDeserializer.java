package com.raising.app.util;

import android.service.autofill.FieldClassification;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raising.app.models.Match;
import com.raising.app.models.MatchListItem;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MatchesDeserializer implements JsonDeserializer<Match> {
    @Override
    public Match deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) {
        Match match = new Match();
        JsonObject jsonObject = json.getAsJsonObject();
        match.setStartup(jsonObject.get("startup").getAsBoolean());
        match.setMatchingPercent(jsonObject.get("matchingPercent").getAsInt());
        match.setId(jsonObject.get("id").getAsLong());
        match.setAccountId(jsonObject.get("accountId").getAsLong());
        match.setProfilePictureId(jsonObject.get("profilePictureId").getAsLong());
        match.setDescription(jsonObject.get("description").getAsString());
        if(jsonObject.has("accountLastChanged")) {
            match.setAccountLastChanged(Serializer.parseTimestamp(jsonObject.get("accountLastChanged").getAsString()));
        }
        if(match.isStartup()) {
            match.setInvestmentPhaseId(jsonObject.get("investmentPhaseId").getAsLong());
            match.setCompanyName(jsonObject.get("companyName").getAsString());
        } else {
            if(jsonObject.get("firstName") != null) {
                match.setFirstName(jsonObject.get("firstName").getAsString());
            }
            if(jsonObject.get("lastName") != null) {
                match.setLastName(jsonObject.get("lastName").getAsString());
            }
            match.setInvestorTypeId(jsonObject.get("investorTypeId").getAsLong());
        }

        return match;
    }
}
