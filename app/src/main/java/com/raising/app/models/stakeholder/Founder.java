package com.raising.app.models.stakeholder;

import java.io.Serializable;

import lombok.Data;

@Data
public class Founder extends StakeholderItem implements Serializable {
    private String firstName;
    private String lastName;
    private String companyPosition;
    private String education;

    public Founder() {
        super();
    }

    public Founder(String firstName, String lastName, String companyPosition,
                   String education) {
        super(firstName + " " + lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyPosition = companyPosition;
        this.education = education;
    }

    public void updateTitle() {
        setTitle(firstName + " " + lastName);
    }
}
