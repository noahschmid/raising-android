package com.raising.app.models.stakeholder;

public class StakeholderBoardMember {

        private String firstName;
        private String lastName;
        private String profession;
        private String boardPosition;
        private int memberSince;
        private String education;

        public StakeholderBoardMember(String firstName, String lastName, String profession, String boardPosition, int memberSince, String education) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.profession = profession;
            this.boardPosition = boardPosition;
            this.memberSince = memberSince;
            this.education = education;
        }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfession() {
        return profession;
    }

    public String getBoardPosition() {
        return boardPosition;
    }

    public int getMemberSince() {
        return memberSince;
    }

    public String getEducation() {
        return education;
    }
}
