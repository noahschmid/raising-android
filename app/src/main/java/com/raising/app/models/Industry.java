package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Industry implements Serializable, Model {
    private long id;
    private String name;

    public Industry() {}

    public Industry(long id) { this.id = id; }
}
