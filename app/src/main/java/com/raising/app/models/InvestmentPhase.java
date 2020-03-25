package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvestmentPhase implements Serializable {
    private String name;
    private long id;

    public InvestmentPhase(long id) { this.id = id; }
}
