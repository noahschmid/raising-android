package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Label implements Serializable, Model {
    private long id;
    private String name;

    public Label() {}
    public Label(long id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Label)) {
            return false;
        }

        Label c = (Label) o;
        return c.getId() == this.getId();
    }
}
