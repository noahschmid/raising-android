package com.raising.app.models.stakeholder;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StakeholderItem implements Serializable {
    private String title;
    private long id = -1l;

    public StakeholderItem() {
        this.title = "";
    }

    public StakeholderItem(String title) {
        this.title = title;
    }
}
