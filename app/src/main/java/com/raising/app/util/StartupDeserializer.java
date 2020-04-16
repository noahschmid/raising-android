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
import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.models.stakeholder.Shareholder;

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
        if(jsonObject.get("name") != null) {
            startup.setName(jsonObject.get("name").getAsString());
        }
        startup.setCompanyName(jsonObject.get("companyName").getAsString());
        startup.setPitch(jsonObject.get("pitch").getAsString());
        startup.setPreMoneyValuation(jsonObject.get("preMoneyValuation").getAsInt());
        startup.setDescription(jsonObject.get("description").getAsString());
        startup.setTicketMinId(jsonObject.get("ticketMinId").getAsInt());
        startup.setTicketMaxId(jsonObject.get("ticketMaxId").getAsInt());
        startup.setInvestmentPhaseId(jsonObject.get("investmentPhaseId").getAsInt());
        if(jsonObject.get("firstName") != null) {
            startup.setFirstName(jsonObject.get("firstName").getAsString());
        }
        if(jsonObject.get("lastName") != null) {
            startup.setLastName(jsonObject.get("lastName").getAsString());
        }
        startup.setClosingTime(jsonObject.get("closingTime").getAsString());
        startup.setBreakEvenYear(jsonObject.get("breakEvenYear").getAsInt());
        startup.setScope(jsonObject.get("scope").getAsInt());
        startup.setFinanceTypeId(jsonObject.get("financeTypeId").getAsInt());
        if(jsonObject.get("turnover") != null) {
            startup.setTurnover(jsonObject.get("turnover").getAsInt());
        }
        if(jsonObject.get("raised") != null) {
            startup.setRaised(jsonObject.get("raised").getAsInt());
        }
        startup.setRevenueMaxId(jsonObject.get("revenueMaxId").getAsInt());
        startup.setRevenueMinId(jsonObject.get("revenueMinId").getAsInt());
        startup.setNumberOfFte(jsonObject.get("numberOfFte").getAsInt());
        startup.setFoundingYear(jsonObject.get("foundingYear").getAsInt());
        startup.setUId(jsonObject.get("uid").getAsString());
        startup.setProfilePictureId(jsonObject.get("profilePictureId").getAsInt());

        if(jsonObject.get("website") != null) {
            startup.setWebsite(jsonObject.get("website").getAsString());
        }

        if(jsonObject.get("countryId") != null) {
            startup.setCountryId(jsonObject.get("countryId").getAsLong());
        }

        for(JsonElement el : jsonObject.get("countries").getAsJsonArray()) {
            startup.addCountry(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("continents").getAsJsonArray()) {
            startup.addContinent(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("investorTypes").getAsJsonArray()) {
            startup.addInvestorType(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("industries").getAsJsonArray()) {
            startup.addIndustry(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("support").getAsJsonArray()) {
            startup.addSupport(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("labels").getAsJsonArray()) {
            startup.addLabel(el.getAsLong());
        }

        for(JsonElement el : jsonObject.get("founders").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            Founder founder = new Founder();
            founder.setFirstName(obj.get("firstName").getAsString());
            founder.setLastName(obj.get("lastName").getAsString());
            founder.setCountryId(obj.get("countryId").getAsInt());
            founder.setEducation(obj.get("education").getAsString());
            founder.setPosition(obj.get("position").getAsString());

            founder.setTitle(founder.getFirstName() + " " +
                    founder.getLastName() + ", " + founder.getPosition());
            founder.setId(obj.get("id").getAsLong());
            startup.addFounder(founder);
        }

        for(JsonElement el : jsonObject.get("boardmembers").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            BoardMember boardMember = new BoardMember();
            boardMember.setBoardPosition(obj.get("position").getAsString());
            boardMember.setCountryId(obj.get("countryId").getAsInt());
            boardMember.setEducation(obj.get("education").getAsString());
            boardMember.setFirstName(obj.get("firstName").getAsString());
            boardMember.setLastName(obj.get("lastName").getAsString());
            boardMember.setProfession(obj.get("profession").getAsString());
            boardMember.setMemberSince(obj.get("memberSince").getAsString());
            boardMember.setTitle(boardMember.getFirstName() + " " +
                    boardMember.getLastName() + ", " + boardMember.getBoardPosition());
            boardMember.setId(obj.get("id").getAsLong());
            startup.addBoardMember(boardMember);
        }

        for(JsonElement el : jsonObject.get("privateShareholders").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            Shareholder privateShareholder = new Shareholder();
            privateShareholder.setPrivateShareholder(true);
            privateShareholder.setFirstName(obj.get("firstName").getAsString());
            privateShareholder.setLastName(obj.get("lastName").getAsString());
            privateShareholder.setInvestorTypeId(obj.get("investorTypeId").getAsInt());
            privateShareholder.setCountryId(obj.get("countryId").getAsInt());
            privateShareholder.setEquityShare(obj.get("equityShare").getAsFloat());
            privateShareholder.setTitle(privateShareholder.getFirstName() + " " +
                    privateShareholder.getLastName() + ", " + privateShareholder.getEquityShare() + "%");
            privateShareholder.setId(obj.get("id").getAsLong());
            startup.addPrivateShareholder(privateShareholder);
        }

        for(JsonElement el : jsonObject.get("corporateShareholders").getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            Shareholder corporateShareholder = new Shareholder();
            corporateShareholder.setPrivateShareholder(false);
            corporateShareholder.setCorporateBodyId(obj.get("corporateBodyId").getAsInt());
            corporateShareholder.setCountryId(obj.get("countryId").getAsInt());
            corporateShareholder.setEquityShare(obj.get("equityShare").getAsFloat());
            corporateShareholder.setCorpName(obj.get("corpName").getAsString());
            corporateShareholder.setWebsite(obj.get("website").getAsString());
            corporateShareholder.setId(obj.get("id").getAsLong());
            corporateShareholder.setTitle(corporateShareholder.getCorpName() + ", " +
                    corporateShareholder.getEquityShare() + "%");
            startup.addCorporateShareholder(corporateShareholder);
        }

        Log.d("StartupDeserializer", "boardmember size: " + startup.getBoardMembers().size());

        return startup;
    }
}
