package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvestmentPhase implements Serializable, Model {
    private String name;
    private long id;

    public InvestmentPhase() {}
    public InvestmentPhase(long id) { this.id = id; }
}
