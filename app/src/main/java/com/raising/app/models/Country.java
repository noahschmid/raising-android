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

    public Country(Country country) {
        this.id = country.getId();
        this.continentId = country.getContinentId();
        this.parentId = country.getParentId();
        this.checked = false;
        this.name = country.getName();
    }

    public void setContinentId(long id) {
        continentId = id;
        parentId = id;
    }
}
