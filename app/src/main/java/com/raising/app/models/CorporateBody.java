package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class CorporateBody implements Serializable, Model {
    private long id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof CorporateBody)) {
            return false;
        }

        CorporateBody c = (CorporateBody) o;
        return c.getId() == this.getId();
    }
}
