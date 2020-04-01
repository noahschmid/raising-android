package com.raising.app.models.stakeholder;

import java.io.Serializable;

import lombok.Data;

@Data
public class Founder extends StakeholderItem implements Serializable {
    private String firstName;
    private String lastName;
    private String position;
    private String education;
    private int countryId;

    public Founder() {
        super();
    }

    public Founder(String firstName, String lastName, String companyPosition,
                   String education, int country) {
        super(firstName + " " + lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = companyPosition;
        this.education = education;
        this.countryId = country;
    }

    public void updateTitle() {
        setTitle(firstName + " " + lastName + ", " + position);
    }
}
