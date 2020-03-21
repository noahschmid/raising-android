package com.raising.app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import lombok.Data;

@Data
public class Startup implements Serializable {
    private String name;
    private String website;
    private String description;
    private String pitch;
    private int numberOfFte;
    private String revenue;
    private int breakevenYear;
    private ArrayList<Long> currentMarkets;
    private int foundingYear;
    private String uid;
    private ArrayList<Long> support;
    private ArrayList<Long> investmentPhases;
    private ArrayList<Long> industries;
    private ArrayList<Long> investorTypes;
    private ArrayList<Long> labels;
    private float scope;
    private float ticketSizeMin;
    private float ticketSizeMax;
    private float valuation;
    private Date closingTime;
    private long financialType;

    public Startup() {
        support = new ArrayList<>();
        investmentPhases = new ArrayList<>();
        industries = new ArrayList<>();
        investorTypes = new ArrayList<>();
        currentMarkets = new ArrayList<>();
        labels = new ArrayList<>();
    }

    public void clearSupport() { support.clear(); }
    public void clearInvestmentPhases() { investmentPhases.clear(); }
    public void clearCurrentMarkets() { currentMarkets.clear(); }
    public void clearLabels() { labels.clear(); }
    public void clearInvestorTypes() { investorTypes.clear(); }
    public void clearIndustries() { industries.clear(); }

    public void addLabel(Long id) { labels.add(id); }

    public void addInvestorType(Long id) { investorTypes.add(id); }

    public void addMarket(Long id) {
        currentMarkets.add(id);
    }

    public void addIndustry(Long id) {
        industries.add(id);
    }

    public void addInvestmentPhase(Long id) {
        investmentPhases.add(id);
    }

    public void addSupport(Long id) {
        support.add(id);
    }
}
