package com.raising.app.models.stakeholder;

import android.os.Parcelable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class BoardMember extends StakeholderRecyclerListItem {
        private String firstName;
        private String lastName;
        private String profession;
        private String boardPosition;
        private String memberSince;
        private String education;

        public BoardMember() {
            super();
        }

        public BoardMember(String firstName, String lastName, String profession,
                           String boardPosition, String memberSince, String education) {
            super(firstName + " " + lastName);
            this.firstName = firstName;
            this.lastName = lastName;
            this.profession = profession;
            this.boardPosition = boardPosition;
            this.memberSince = memberSince;
            this.education = education;
        }

        public void updateTitle() {
            setTitle(firstName + " " + lastName);
        }
}
