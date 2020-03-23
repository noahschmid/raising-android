package com.raising.app.models.stakeholder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StakeholderRecyclerListItem {
    private String title;

    public StakeholderRecyclerListItem() {
        this.title = "";
    }

    public StakeholderRecyclerListItem(String title) {
        this.title = title;
    }
}
