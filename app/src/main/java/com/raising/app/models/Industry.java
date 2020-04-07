package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Industry implements Serializable, Model {
    private long id;
    private String name;

    public Industry() {}

    public Industry(long id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Industry)) {
            return false;
        }

        Industry c = (Industry) o;
        return c.getId() == this.getId();
    }
}
