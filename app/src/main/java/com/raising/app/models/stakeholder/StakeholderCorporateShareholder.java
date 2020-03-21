package com.raising.app.models.stakeholder;

public class StakeholderCorporateShareholder {

    private String name;
    private String corporateBody;
    private String website;
    private int equityShare;

    public StakeholderCorporateShareholder (String name, String corporateBody, String website, int equityShare) {
        this.name = name;
        this.corporateBody = corporateBody;
        this.website = website;
        this.equityShare = equityShare;
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

    public int getEquityShare() {
        return equityShare;
    }
}
