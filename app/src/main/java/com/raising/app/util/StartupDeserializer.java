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
import com.raising.app.models.Startup;
import com.raising.app.models.Support;

import java.io.IOException;
import java.lang.reflect.Type;

public class StartupDeserializer implements JsonDeserializer<Startup> {
    @Override
    public Startup deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context)
            throws JsonParseException {
        Log.d("StartupDeserializer", json.toString());
        JsonObject jsonObject = json.getAsJsonObject();
        Startup startup = new Startup();

        startup.setId(jsonObject.get("accountId").getAsLong());
        if(!jsonObject.get("name").isJsonNull()) {
            startup.setName(jsonObject.get("name").getAsString());
        }
        startup.setCompanyName(jsonObject.get("companyName").getAsString());
        startup.setPitch(jsonObject.get("pitch").getAsString());
        startup.setDescription(jsonObject.get("description").getAsString());
        startup.setTicketMinId(jsonObject.get("ticketMinId").getAsInt());
        startup.setTicketMaxId(jsonObject.get("ticketMaxId").getAsInt());
        startup.setInvestmentPhaseId(jsonObject.get("investmentPhaseId").getAsInt());
        if(!jsonObject.get("firstName").isJsonNull()) {
            startup.setFirstName(jsonObject.get("firstName").getAsString());
        }
        if(!jsonObject.get("lastName").isJsonNull()) {
            startup.setLastName(jsonObject.get("lastName").getAsString());
        }
        startup.setClosingTime(jsonObject.get("closingTime").getAsString());
        startup.setBreakEvenYear(jsonObject.get("breakEvenYear").getAsInt());
        startup.setScope(jsonObject.get("scope").getAsInt());
        startup.setFinanceTypeId(jsonObject.get("financeTypeId").getAsInt());
        if(!jsonObject.get("turnover").isJsonNull()) {
            startup.setTurnover(jsonObject.get("turnover").getAsInt());
        }
        if(!jsonObject.get("raised").isJsonNull()) {
            startup.setRaised(jsonObject.get("raised").getAsInt());
        }
        startup.setRevenueMaxId(jsonObject.get("revenueMaxId").getAsInt());
        startup.setRevenueMinId(jsonObject.get("revenueMinId").getAsInt());
        startup.setNumberOfFte(jsonObject.get("numberOfFte").getAsInt());
        startup.setFoundingYear(jsonObject.get("foundingYear").getAsInt());
        startup.setUId(jsonObject.get("uid").getAsString());

        if(!jsonObject.get("website").isJsonNull()) {
            startup.setWebsite(jsonObject.get("website").getAsString());
        }
        if(!jsonObject.get("countryId").isJsonNull()) {
            startup.setCountryId(jsonObject.get("countryId").getAsLong());
        }

        if(!jsonObject.get("profilePicture").isJsonNull()) {
            startup.setProfilePicture(new Image(jsonObject.get("profilePicture").getAsString()));
        }
        for(JsonElement el : jsonObject.get("countries").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            startup.addCountry(obj.get("id").getAsLong());
        }

        for(JsonElement el : jsonObject.get("continents").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            startup.addContinent(obj.get("id").getAsLong());
        }

        for(JsonElement el : jsonObject.get("investorTypes").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            startup.addInvestorType(obj.get("id").getAsLong());
        }

        for(JsonElement el : jsonObject.get("industries").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            startup.addIndustry(obj.get("id").getAsLong());
        }

        for(JsonElement el : jsonObject.get("support").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            startup.addSupport(obj.get("id").getAsLong());
        }

        for(JsonElement el : jsonObject.get("labels").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            startup.addLabel(obj.get("id").getAsLong());
        }

        return startup;
    }
}
