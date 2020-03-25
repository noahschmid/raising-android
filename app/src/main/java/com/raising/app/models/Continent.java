package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Continent implements Serializable {
    private String name;
    private long id;

    public Continent(long id) { this.id = id; }
}
