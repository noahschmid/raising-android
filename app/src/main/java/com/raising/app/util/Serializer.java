package com.raising.app.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.raising.app.models.Investor;
import com.raising.app.models.Startup;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Serializer {
    public static JsonSerializer<Investor> InvestorUpdateSerializer = new JsonSerializer<Investor>() {
        @Override
        public JsonElement serialize(Investor src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject jsonInvestor = new JsonObject();

            jsonInvestor.addProperty("ticketMinId", src.getTicketMinId());
            jsonInvestor.addProperty("ticketMaxId", src.getTicketMaxId());
            jsonInvestor.addProperty("investorTypeId", src.getInvestorTypeId());
            jsonInvestor.addProperty("companyName", src.getCompanyName());
            jsonInvestor.addProperty("pitch", src.getPitch());
            jsonInvestor.addProperty("name", src.getName());
            jsonInvestor.addProperty("website", src.getWebsite());
            jsonInvestor.addProperty("firstName", src.getFirstName());
            jsonInvestor.addProperty("profilePictureId", src.getProfilePictureId());
            jsonInvestor.addProperty("lastName", src.getLastName());
            if(src.getEmail() != null && !src.getEmail().equals(AuthenticationHandler.getEmail())) {
                jsonInvestor.addProperty("email", src.getEmail());
            }
            jsonInvestor.addProperty("description", src.getDescription());
            jsonInvestor.addProperty("countryId", src.getCountryId());

            jsonInvestor.add("countries", toJsonArray(src.getCountries()));
            jsonInvestor.add("continents", toJsonArray(src.getContinents()));

            jsonInvestor.add("investmentPhases", toJsonArray(src.getInvestmentPhases()));
            jsonInvestor.add("support", toJsonArray(src.getSupport()));
            jsonInvestor.add("industries", toJsonArray(src.getIndustries()));

            jsonInvestor.add("gallery", toJsonArray(src.getGalleryIds()));

            return jsonInvestor;
        }
    };

    public static JsonSerializer<Investor> InvestorRegisterSerializer = new JsonSerializer<Investor>() {
        @Override
        public JsonElement serialize(Investor src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject jsonInvestor = new JsonObject();

            jsonInvestor.addProperty("email", src.getEmail());
            jsonInvestor.addProperty("ticketMinId", src.getTicketMinId());
            jsonInvestor.addProperty("ticketMaxId", src.getTicketMaxId());
            jsonInvestor.addProperty("investorTypeId", src.getInvestorTypeId());
            jsonInvestor.addProperty("companyName", src.getCompanyName());
            jsonInvestor.addProperty("pitch", src.getPitch());
            jsonInvestor.addProperty("website", src.getWebsite());
            jsonInvestor.addProperty("password", src.getPassword());
            jsonInvestor.addProperty("name", src.getName());
            jsonInvestor.addProperty("firstName", src.getFirstName());
            jsonInvestor.addProperty("lastName", src.getLastName());
            jsonInvestor.addProperty("description", src.getDescription());

            jsonInvestor.add("countries", toJsonArray(src.getCountries()));

            jsonInvestor.add("continents", toJsonArray(src.getContinents()));
            jsonInvestor.add("support", toJsonArray(src.getSupport()));
            jsonInvestor.add("industries", toJsonArray(src.getIndustries()));
            jsonInvestor.add("investmentPhases", toJsonArray(src.getInvestmentPhases()));
            jsonInvestor.addProperty("website", src.getWebsite());
            jsonInvestor.addProperty("countryId", src.getCountryId());
            jsonInvestor.addProperty("profilePictureId", src.getProfilePictureId());
            jsonInvestor.add("gallery", toJsonArray(src.getGalleryIds()));

            return jsonInvestor;
        }
    };

    public static JsonSerializer<Startup> StartupUpdateSerializer = new JsonSerializer<Startup>() {
        @Override
        public JsonElement serialize(Startup src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject jsonStartup = new JsonObject();

            jsonStartup.addProperty("ticketMinId", src.getTicketMinId());
            jsonStartup.addProperty("ticketMaxId", src.getTicketMaxId());
            jsonStartup.addProperty("preMoneyValuation", src.getPreMoneyValuation());
            jsonStartup.addProperty("investmentPhaseId", src.getInvestmentPhaseId());
            jsonStartup.addProperty("companyName", src.getCompanyName());
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
            jsonStartup.addProperty("breakEvenYear", src.getBreakEvenYear());
            jsonStartup.addProperty("uid", src.getUId());
            jsonStartup.addProperty("website", src.getWebsite());
            jsonStartup.addProperty("scope", src.getScope());
            jsonStartup.addProperty("financeTypeId", src.getFinanceTypeId());
            jsonStartup.addProperty("turnover", src.getTurnover());
            jsonStartup.addProperty("description", src.getDescription());
            jsonStartup.addProperty("countryId", src.getCountryId());
            jsonStartup.addProperty("profilePictureId", src.getProfilePictureId());

            if(src.getEmail() != null && !src.getEmail().equals(AuthenticationHandler.getEmail())) {
                jsonStartup.addProperty("email", src.getEmail());
            }

            jsonStartup.add("countries", toJsonArray(src.getCountries()));
            jsonStartup.add("continents", toJsonArray(src.getContinents()));
            jsonStartup.add("labels", toJsonArray(src.getLabels()));
            jsonStartup.add("support", toJsonArray(src.getSupport()));
            jsonStartup.add("industries", toJsonArray(src.getIndustries()));
            jsonStartup.add("investorTypes", toJsonArray(src.getInvestorTypes()));

            jsonStartup.add("gallery", toJsonArray(src.getGalleryIds()));

            return jsonStartup;
        }
    };

    public static JsonSerializer<Startup> StartupRegisterSerializer = new JsonSerializer<Startup>() {
        @Override
        public JsonElement serialize(Startup src, Type typeOfSrc,
                                     JsonSerializationContext context){
            JsonObject jsonStartup = new JsonObject();

            jsonStartup.addProperty("email", src.getEmail());
            jsonStartup.addProperty("ticketMinId", src.getTicketMinId());
            jsonStartup.addProperty("ticketMaxId", src.getTicketMaxId());
            jsonStartup.addProperty("preMoneyValuation", src.getPreMoneyValuation());
            jsonStartup.addProperty("investmentPhaseId", src.getInvestmentPhaseId());
            jsonStartup.addProperty("companyName", src.getCompanyName());
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
            jsonStartup.addProperty("breakEvenYear", src.getBreakEvenYear());
            jsonStartup.addProperty("uid", src.getUId());
            jsonStartup.addProperty("website", src.getWebsite());
            jsonStartup.addProperty("scope", src.getScope());
            jsonStartup.addProperty("financeTypeId", src.getFinanceTypeId());
            jsonStartup.addProperty("turnover", src.getTurnover());
            jsonStartup.addProperty("description", src.getDescription());
            jsonStartup.addProperty("password", src.getPassword());
            jsonStartup.addProperty("countryId", src.getCountryId());
            jsonStartup.add("countries", toJsonArray(src.getCountries()));
            jsonStartup.add("continents", toJsonArray(src.getContinents()));
            jsonStartup.add("labels", toJsonArray(src.getLabels()));
            jsonStartup.add("support", toJsonArray(src.getSupport()));
            jsonStartup.add("industries", toJsonArray(src.getIndustries()));
            jsonStartup.add("investorTypes", toJsonArray(src.getInvestorTypes()));
            jsonStartup.addProperty("profilePictureId", src.getProfilePictureId());

            jsonStartup.add("gallery", toJsonArray(src.getGalleryIds()));

            // -- FOUNDERS --
            JsonArray founders = new JsonArray();
            src.getFounders().forEach(founder -> {
                JsonObject founderObject = new JsonObject();
                founderObject.addProperty("firstName", founder.getFirstName());
                founderObject.addProperty("countryId", founder.getCountryId());
                founderObject.addProperty("education", founder.getEducation());
                founderObject.addProperty("lastName", founder.getLastName());
                founderObject.addProperty("position", founder.getPosition());
                founders.add(founderObject);
            });

            jsonStartup.add("founders", founders);

            // -- BOARDMEMBERS --
            JsonArray boardMembers = new JsonArray();
            src.getBoardMembers().forEach(boardMember -> {
                JsonObject boardMemberObject = new JsonObject();
                boardMemberObject.addProperty("firstName", boardMember.getFirstName());
                boardMemberObject.addProperty("countryId", boardMember.getCountryId());
                boardMemberObject.addProperty("education", boardMember.getEducation());
                boardMemberObject.addProperty("lastName", boardMember.getLastName());
                boardMemberObject.addProperty("position", boardMember.getBoardPosition());
                boardMemberObject.addProperty("memberSince", boardMember.getMemberSince());
                boardMemberObject.addProperty("profession", boardMember.getProfession());

                boardMembers.add(boardMemberObject);
            });

            jsonStartup.add("boardmembers", boardMembers);

            // -- PRIVATE SHAREHOLDERS --
            JsonArray privateShareholders = new JsonArray();
            src.getPrivateShareholders().forEach(shareholder -> {
                JsonObject shareholderObject = new JsonObject();
                shareholderObject.addProperty("firstName", shareholder.getFirstName());
                shareholderObject.addProperty("countryId", shareholder.getCountryId());
                shareholderObject.addProperty("equityShare", shareholder.getEquityShare());
                shareholderObject.addProperty("lastName", shareholder.getLastName());
                shareholderObject.addProperty("investorTypeId", shareholder.getInvestorTypeId());
                privateShareholders.add(shareholderObject);
            });

            jsonStartup.add("privateShareholders", privateShareholders);

            // -- CORPORATE SHAREHOLDERS --
            JsonArray corporateShareholders = new JsonArray();
            src.getCorporateShareholders().forEach(shareholder -> {
                JsonObject shareholderObject = new JsonObject();
                shareholderObject.addProperty("corpName", shareholder.getCorpName());
                shareholderObject.addProperty("countryId", shareholder.getCountryId());
                shareholderObject.addProperty("equityShare", shareholder.getEquityShare());
                shareholderObject.addProperty("website", shareholder.getWebsite());
                shareholderObject.addProperty("corporateBodyId", shareholder.getCorporateBodyId());

                corporateShareholders.add(shareholderObject);
            });

            jsonStartup.add("corporateShareholders", corporateShareholders);

            return jsonStartup;
        }
    };


    private static JsonArray toJsonArray(List<Long> input) {
        if(input == null)
            return new JsonArray();
        JsonArray array = new JsonArray();
        input.forEach(id -> {
            array.add(id);
        });

        return array;
    }
}
