package com.raising.app.util;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Image;
import com.raising.app.models.Industry;
import com.raising.app.models.InvestmentPhase;
import com.raising.app.models.Investor;
import com.raising.app.models.Support;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class InvestorDeserializer implements JsonDeserializer<Investor> {
    @Override
    public Investor deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context)
            throws JsonParseException {
        Log.d("InvestorDeserializer", json.toString());
        JsonObject jsonObject = json.getAsJsonObject();
        Investor investor = new Investor();

        investor.setId(jsonObject.get("accountId").getAsLong());
        if(jsonObject.get("firstName") != null) {
            investor.setFirstName(jsonObject.get("firstName").getAsString());
        }
        if(jsonObject.get("lastName") != null) {
            investor.setLastName(jsonObject.get("lastName").getAsString());
        }
        investor.setCompanyName(jsonObject.get("companyName").getAsString());
        investor.setPitch(jsonObject.get("pitch").getAsString());
        investor.setDescription(jsonObject.get("description").getAsString());
        investor.setTicketMinId(jsonObject.get("ticketMinId").getAsInt());
        investor.setTicketMaxId(jsonObject.get("ticketMaxId").getAsInt());
        investor.setInvestorTypeId(jsonObject.get("investorTypeId").getAsInt());

        if(jsonObject.get("profilePictureId") != null) {
            investor.setProfilePictureId(jsonObject.get("profilePictureId").getAsInt());
        }

        for(JsonElement el : jsonObject.get("gallery").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            investor.addToGallery(new Image(obj.get("image").getAsString()));
        }

        for(JsonElement el : jsonObject.get("countries").getAsJsonArray()) {
            investor.addCountry(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("continents").getAsJsonArray()) {
            investor.addContinent(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("investmentPhases").getAsJsonArray()) {
            investor.addInvestmentPhase(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("industries").getAsJsonArray()) {
            investor.addIndustry(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("support").getAsJsonArray()) {
            investor.addSupport(el.getAsLong());
        }

        return investor;
    }
}
