package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Image implements Serializable {
    private long id;
    private long accountId;
    private String image;

    public Image(String image) { this.image = image; }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Image)) {
            return false;
        }

        Image c = (Image) o;
        return c.getId() == this.getId();
    }
}
