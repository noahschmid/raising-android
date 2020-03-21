package com.raising.app.models.stakeholder;

public class StakeholderPrivateShareholder {

    private String firstName;
    private String lastName;
    private String country;
    private int equityShare;

    public StakeholderPrivateShareholder(String firstName, String lastName, String country, int equityShare) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.equityShare = equityShare;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCountry() {
        return country;
    }

    public int getEquityShare() {
        return equityShare;
    }
}
