package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class CorporateBody implements Serializable, Model {
    private long id;
    private String name;
}
