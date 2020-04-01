package com.raising.app.models.stakeholder;

import java.io.Serializable;

import lombok.Data;

@Data
public class Shareholder extends StakeholderItem implements Serializable {
    private boolean isPrivateShareholder;
    private String firstName;
    private String lastName;
    private String corpName;
    private int corporateBodyId;
    private long investortypeId;
    private String website;
    private String equityShare;
    private long countryId;

    public Shareholder() {
        super();
    }

    public Shareholder(
            boolean privateShareholder, String firstName, String lastName, long countryId,
            String name, int corporateBody, String website, String equityShare, long investorTypeId) {
        super(privateShareholder ? (firstName + " " + lastName) : name);
        this.isPrivateShareholder = privateShareholder;
        this.firstName = firstName;
        this.lastName = lastName;
        this.countryId = countryId;
        this.corpName = name;
        this.corporateBodyId = corporateBody;
        this.website = website;
        this.equityShare = equityShare;
        this.investortypeId = investorTypeId;
    }

    public void updateTitle() {
        setTitle(isPrivateShareholder ? (firstName + " " + lastName + ", " + equityShare + "%") : (corpName + ", " + equityShare + "%"));
    }
}
