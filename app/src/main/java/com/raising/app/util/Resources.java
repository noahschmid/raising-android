package com.raising.app.util;

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

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Resources implements Serializable {
    private ArrayList<Country> countries = new ArrayList<>();
    private ArrayList<Continent> continents = new ArrayList<>();
    private ArrayList<Support> supports = new ArrayList<>();
    private ArrayList<InvestorType> investorTypes = new ArrayList<>();
    private ArrayList<Label> labels = new ArrayList<>();
    private ArrayList<Industry> industries = new ArrayList<>();
    private ArrayList<InvestmentPhase> investmentPhases = new ArrayList<>();
    private ArrayList<Revenue> revenues = new ArrayList<>();
    private ArrayList<FinanceType> financeTypes = new ArrayList<>();
    private ArrayList<CorporateBody> corporateBodies = new ArrayList<>();
    private ArrayList<TicketSize> ticketSizes = new ArrayList<>();

    public Resources() {}
}
