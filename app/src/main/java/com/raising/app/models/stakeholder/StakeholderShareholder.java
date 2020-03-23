package com.raising.app.models.stakeholder;

public class StakeholderShareholder {

    private boolean isPrivateShareholder;
    private String firstName;
    private String lastName;
    private String country;
    private String name;
    private String corporateBody;
    private String website;
    private String equityShare;

    public StakeholderShareholder(
            boolean privateShareholder, String firstName, String lastName, String country,
            String name, String corporateBody, String website, String equityShare) {
        this.isPrivateShareholder = privateShareholder;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.name = name;
        this.corporateBody = corporateBody;
        this.website = website;
        this.equityShare = equityShare;
    }

    public boolean isPrivateShareholder() {
        return isPrivateShareholder;
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

    public String getName() {
        return name;
    }

    public String getCorporateBody() {
        return corporateBody;
    }

    public String getWebsite() {
        return website;
    }

    public String getEquityShare() {
        return equityShare;
    }
}
