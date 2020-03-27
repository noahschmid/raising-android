package com.raising.app.models;

import com.raising.app.util.customPicker.PickerItem;

import java.io.Serializable;

import lombok.Data;

@Data
public class Country implements Serializable, Model, PickerItem {
    private String name;
    private long id;
    private long continentId;
    private boolean checked = false;
    private long parentId;

    public Country() {}

    public Country(long id) { this.id = id; }

    public void setContinentId(long id) {
        continentId = id;
        parentId = id;
    }
}
