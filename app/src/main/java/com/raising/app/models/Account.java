package com.raising.app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Account implements Serializable, Model {
    protected long id = -1L;
    protected String company;
    protected String name;
    private String firstName;
    private String lastName;
    private String password;
    private String roles = "ROLE_USER";
    private String email;
    private String pitch;
    private String description;
    private int ticketMinId = -1;
    private int ticketMaxId = -1;

    private List<Country> countries = new ArrayList<>();
    private List<Continent> continents = new ArrayList<>();
    private List<Support> support = new ArrayList<>();
    private List<Industry> industries = new ArrayList<>();

    public void clearSupport() { support.clear(); }
    public void clearIndustries() { industries.clear(); }
    public void clearCountries() { countries.clear(); }
    public void clearContinents() { continents.clear(); }

    public void addCountry(Country country) {
        countries.add(country);
    }
    public void addContinent(Continent continent) {
        continents.add(continent);
    }
    public void addIndustry(Industry industry) {
        industries.add(industry);
    }
    public void addSupport(Support spprt) {
        support.add(spprt);
    }
}
