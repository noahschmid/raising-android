package com.raising.app.models.stakeholder;

public class StakeholderFounder {

    private String firstName;
    private String lastName;
    private String companyPosition;
    private String education;

    public StakeholderFounder(String firstName, String lastName, String companyPosition, String education) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyPosition = companyPosition;
        this.education = education;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompanyPosition() {
        return companyPosition;
    }

    public String getEducation() {
        return education;
    }
}
