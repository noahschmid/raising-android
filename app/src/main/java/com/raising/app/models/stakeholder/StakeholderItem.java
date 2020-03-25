package com.raising.app.models.stakeholder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StakeholderItem {
    private String title;

    public StakeholderItem() {
        this.title = "";
    }

    public StakeholderItem(String title) {
        this.title = title;
    }
}
