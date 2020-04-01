package com.raising.app.models.stakeholder;

import java.io.Serializable;

import lombok.Data;

@Data
public class BoardMember extends StakeholderItem implements Serializable {
        private String firstName;
        private String lastName;
        private String profession;
        private String boardPosition;
        private String memberSince;
        private String education;
        private int countryId;

        public BoardMember() {
            super();
        }

        public BoardMember(String firstName, String lastName, String profession,
                           String boardPosition, String memberSince, String education,
                           int countryId) {
            super(firstName + " " + lastName);
            this.firstName = firstName;
            this.lastName = lastName;
            this.profession = profession;
            this.boardPosition = boardPosition;
            this.memberSince = memberSince;
            this.education = education;
            this.countryId = countryId;
        }

        public void updateTitle() {
            setTitle(firstName + " " + lastName + ", " + boardPosition);
        }
}