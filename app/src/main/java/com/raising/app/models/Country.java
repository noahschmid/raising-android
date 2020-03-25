package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Country implements Serializable {
    private String name;
    private long id;
    private long continentId;

    public Country(long id) { this.id = id; }
}
