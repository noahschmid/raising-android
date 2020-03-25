package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvestorType implements Serializable {
    private long id;
    private String name;

    public InvestorType(long id) { this.id = id; }
}
