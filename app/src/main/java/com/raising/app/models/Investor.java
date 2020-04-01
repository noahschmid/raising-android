package com.raising.app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Investor extends Account implements Serializable {
    private long investorTypeId = -1;
    private List<InvestmentPhase> investmentPhases;

    public Investor() {
        investmentPhases = new ArrayList<>();
    }

    public void clearInvestmentPhases() { investmentPhases.clear(); }

    public void addInvestmentPhase(InvestmentPhase investmentPhase) {
        investmentPhases.add(investmentPhase);
    }
}
