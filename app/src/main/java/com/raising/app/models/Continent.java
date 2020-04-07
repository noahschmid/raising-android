package com.raising.app.models;

import com.raising.app.util.customPicker.PickerItem;

import java.io.Serializable;

import lombok.Data;

@Data
public class Continent implements Serializable, Model, PickerItem {
    private String name;
    private long id;
    private boolean checked = false;
    private long parentId;

    public Continent() {}
    public Continent(long id) { this.id = id; this.parentId = id; }
    public void setId(long id) { this.id = id; this.parentId = id; }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Continent)) {
            return false;
        }

        Continent c = (Continent) o;
        return c.getId() == this.getId();
    }
}
