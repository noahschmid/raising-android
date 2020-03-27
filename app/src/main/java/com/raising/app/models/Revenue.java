package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Revenue implements Serializable, Model {
    private long id;
    private String name;
}
