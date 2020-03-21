package com.raising.app.models;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Investor implements Serializable {
    private float ticketSizeMin = -1;
    private float ticketSizeMax = -1;
    private long investorTypeId = -1;
    private ArrayList<Long> countries;
    private ArrayList<Long> continents;
    private ArrayList<Long> support;
    private ArrayList<Long> investmentPhases;
    private ArrayList<Long> industries;

    public Investor() {
        countries = new ArrayList<>();
        continents = new ArrayList<>();
        support = new ArrayList<>();
        investmentPhases = new ArrayList<>();
        industries = new ArrayList<>();
    }

    public void clearInvestmentPhases() { investmentPhases.clear(); }
    public void clearCountries() { countries.clear(); }
    public void clearContinents() { continents.clear(); }
    public void clearIndustries() { industries.clear(); }
    public void clearSupport() { support.clear(); }

    public void addCountry(Long id) {
        countries.add(id);
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

    public void addContinent(Long id) {
        continents.add(id);
    }
}
