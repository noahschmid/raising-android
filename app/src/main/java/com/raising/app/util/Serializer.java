package com.raising.app.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.raising.app.models.Investor;
import com.raising.app.models.Startup;

import java.lang.reflect.Type;

public class Serializer {
    public static JsonSerializer<Investor> InvestorUpdateSerializer = new JsonSerializer<Investor>() {
        @Override
        public JsonElement serialize(Investor src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject jsonInvestor = new JsonObject();

            jsonInvestor.addProperty("email", src.getEmail());
            jsonInvestor.addProperty("ticketMinId", src.getTicketMinId());
            jsonInvestor.addProperty("ticketMaxId", src.getTicketMaxId());
            jsonInvestor.addProperty("investorTypeId", src.getInvestorTypeId());
            jsonInvestor.addProperty("company", src.getCompany());
            jsonInvestor.addProperty("pitch", src.getPitch());
            jsonInvestor.addProperty("name", src.getName());
            jsonInvestor.addProperty("firstName", src.getFirstName());
            jsonInvestor.addProperty("lastName", src.getLastName());
            jsonInvestor.addProperty("description", src.getDescription());

            return jsonInvestor;
        }
    };

    public static JsonSerializer<Startup> StartupUpdateSerializer = new JsonSerializer<Startup>() {
        @Override
        public JsonElement serialize(Startup src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject jsonStartup = new JsonObject();

            jsonStartup.addProperty("email", src.getEmail());
            jsonStartup.addProperty("ticketMinId", src.getTicketMinId());
            jsonStartup.addProperty("ticketMaxId", src.getTicketMaxId());
            jsonStartup.addProperty("investmentPhaseId", src.getInvestmentPhaseId());
            jsonStartup.addProperty("company", src.getCompany());
            jsonStartup.addProperty("pitch", src.getPitch());
            jsonStartup.addProperty("name", src.getName());
            jsonStartup.addProperty("firstName", src.getFirstName());
            jsonStartup.addProperty("lastName", src.getLastName());
            jsonStartup.addProperty("raised", src.getRaised());
            jsonStartup.addProperty("revenueMaxId", src.getRevenueMaxId());
            jsonStartup.addProperty("revenueMinId", src.getRevenueMinId());
            jsonStartup.addProperty("numberOfFte", src.getNumberOfFte());
            jsonStartup.addProperty("foundingYear", src.getFoundingYear());
            jsonStartup.addProperty("closingTime", src.getClosingTime());
            jsonStartup.addProperty("breakevenYear", src.getBreakEvenYear());
            jsonStartup.addProperty("uId", src.getUId());
            jsonStartup.addProperty("website", src.getWebsite());
            jsonStartup.addProperty("scope", src.getScope());
            jsonStartup.addProperty("financeTypeId", src.getFinanceTypeId());
            jsonStartup.addProperty("turnover", src.getTurnover());
            jsonStartup.addProperty("description", src.getDescription());

            return jsonStartup;
        }
    };
}
