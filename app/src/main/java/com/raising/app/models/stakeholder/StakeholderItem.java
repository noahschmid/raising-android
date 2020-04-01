package com.raising.app.models.stakeholder;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StakeholderItem implements Serializable {
    private String title;

    public StakeholderItem() {
        this.title = "";
    }

    public StakeholderItem(String title) {
        this.title = title;
    }
}
