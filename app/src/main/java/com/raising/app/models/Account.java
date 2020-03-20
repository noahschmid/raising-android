package com.raising.app.models;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Account implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String description;
    private String pitch;
    private int investmentMin = -1;
    private int investmentMax = -1;
    private long investorTypeId = -1;
    private ArrayList<Long> countries;
    private ArrayList<Long> continents;
    private ArrayList<Long> support;
    private ArrayList<Long> investmentPhases;
    private ArrayList<Long> industries;

    public Account() {
        countries = new ArrayList<>();
        continents = new ArrayList<>();
        support = new ArrayList<>();
        investmentPhases = new ArrayList<>();
        industries = new ArrayList<>();
    }

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
