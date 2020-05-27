package com.raising.app.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.raising.app.models.Continent;
import com.raising.app.models.CorporateBody;
import com.raising.app.models.Country;
import com.raising.app.models.FinanceType;
import com.raising.app.models.Industry;
import com.raising.app.models.InvestmentPhase;
import com.raising.app.models.InvestorType;
import com.raising.app.models.Label;
import com.raising.app.models.Revenue;
import com.raising.app.models.Support;
import com.raising.app.models.TicketSize;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This class handles deserialization of resources when returned as json data from backend server
 */

public class ResourcesDeserializer implements JsonDeserializer<Resources> {
    @Override
    public Resources deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context) {
        Resources resources = new Resources();
        JsonObject jsonObject = json.getAsJsonObject();
        Gson gson = new Gson();


        JsonArray ticketSizes = jsonObject.get("ticketSizes").getAsJsonArray();
        ArrayList<TicketSize> tickets = new ArrayList<>();
        if(ticketSizes != null) {
            for (JsonElement el : ticketSizes) {
                JsonObject obj = el.getAsJsonObject();
                tickets.add(new TicketSize(obj.get("id").getAsLong(), obj.get("name").getAsInt()));
            }
            resources.setTicketSizes(tickets);
        }

        resources.getTicketSizes().forEach(ticketSize -> Log.d("ResourcesDeserializer",
                "deserialize: " + ticketSize.getName()));
        Log.d("ResourcesDeserializer", "deserialize: " + resources.getTicketSizes().toString());

        JsonArray countries = jsonObject.get("countries").getAsJsonArray();
        if(countries != null) {
            resources.setCountries(gson.fromJson(countries.toString(),
                    new TypeToken<ArrayList<Country>>(){}.getType()));
        }

        JsonArray continents = jsonObject.get("continents").getAsJsonArray();
        if(continents != null) {
            resources.setContinents(gson.fromJson(continents.toString(),
                    new TypeToken<ArrayList<Continent>>(){}.getType()));
        }

        JsonArray investorTypes = jsonObject.get("investorTypes").getAsJsonArray();
        if(investorTypes != null) {
            resources.setInvestorTypes(gson.fromJson(investorTypes.toString(),
                    new TypeToken<ArrayList<InvestorType>>(){}.getType()));
        }

        JsonArray investmentPhases = jsonObject.get("investmentPhases").getAsJsonArray();
        if(investmentPhases != null) {
            resources.setInvestmentPhases(gson.fromJson(investmentPhases.toString(),
                    new TypeToken<ArrayList<InvestmentPhase>>(){}.getType()));
        }

        JsonArray corporateBodies = jsonObject.get("corporateBodies").getAsJsonArray();
        if(corporateBodies != null) {
            resources.setCorporateBodies(gson.fromJson(corporateBodies.toString(),
                    new TypeToken<ArrayList<CorporateBody>>(){}.getType()));
        }

        JsonArray financeTypes = jsonObject.get("financeTypes").getAsJsonArray();
        if(financeTypes != null) {
            resources.setFinanceTypes(gson.fromJson(financeTypes.toString(),
                    new TypeToken<ArrayList<FinanceType>>(){}.getType()));
        }

        JsonArray revenues = jsonObject.get("revenues").getAsJsonArray();
        Revenue revenue;
        int lastId = 0, lastValue = 0;
        if(revenues != null) {
            for(int i = 0; i < revenues.size(); ++i) {
                JsonObject el = revenues.get(i).getAsJsonObject();
                if(i > 0) {
                    revenue = new Revenue();
                    revenue.setRevenueMinId(lastId);
                    revenue.setRevenueMin(lastValue);
                    revenue.setRevenueMaxId(el.get("id").getAsInt());
                    revenue.setRevenueMax(el.get("name").getAsInt());
                    resources.getRevenues().add(revenue);
                }

                lastId = el.get("id").getAsInt();
                lastValue = el.get("name").getAsInt();
            }
        }

        JsonArray labels = jsonObject.get("labels").getAsJsonArray();
        if(labels != null) {
            resources.setLabels(gson.fromJson(labels.toString(),
                    new TypeToken<ArrayList<Label>>(){}.getType()));
        }

        JsonArray industries = jsonObject.get("industries").getAsJsonArray();
        if(industries != null) {
            resources.setIndustries(gson.fromJson(industries.toString(),
                    new TypeToken<ArrayList<Industry>>(){}.getType()));
        }

        JsonArray supports = jsonObject.get("support").getAsJsonArray();
        if(supports != null) {
            resources.setSupports(gson.fromJson(supports.toString(),
                    new TypeToken<ArrayList<Support>>(){}.getType()));
        }

        return resources;
    }
}
