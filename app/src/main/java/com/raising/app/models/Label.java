package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Label implements Serializable, Model {
    private long id;
    private String name;

    public Label() {}
    public Label(long id) { this.id = id; }
}
