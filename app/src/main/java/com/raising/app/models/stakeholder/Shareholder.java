package com.raising.app.models.stakeholder;

import java.io.Serializable;

import lombok.Data;

@Data
public class Shareholder extends StakeholderItem implements Serializable {
    private boolean isPrivateShareholder;
    private String firstName;
    private String lastName;
    private String country;
    private String name;
    private String corporateBody;
    private String website;
    private String equityShare;
    private long countryId;

    public Shareholder() {
        super();
    }

    public Shareholder(
            boolean privateShareholder, String firstName, String lastName, String country,
            String name, String corporateBody, String website, String equityShare) {
        super(privateShareholder ? (firstName + " " + lastName) : name);
        this.isPrivateShareholder = privateShareholder;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.name = name;
        this.corporateBody = corporateBody;
        this.website = website;
        this.equityShare = equityShare;
    }

    public void updateTitle() {
        setTitle(isPrivateShareholder ? (firstName + " " + lastName) : name);
    }
}
