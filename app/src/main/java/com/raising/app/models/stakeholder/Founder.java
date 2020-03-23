package com.raising.app.models.stakeholder;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Founder extends StakeholderRecyclerListItem {
    private String firstName;
    private String lastName;
    private String companyPosition;
    private String education;

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
