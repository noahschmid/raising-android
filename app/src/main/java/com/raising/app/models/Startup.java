package com.raising.app.models;

import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.models.stakeholder.Shareholder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Startup extends Account implements Serializable {
    private long investmentPhaseId = -1;
    private int boosts = 0;
    private String website;
    private int breakEvenYear;
    private int numberOfFte;
    private int turnover;
    private int preMoneyValuation;
    private String closingTime;
    private int revenueMaxId = -1;
    private int revenueMinId = -1;
    private int scope;
    private String uId;
    private int foundingYear;
    private long financeTypeId = -1;
    private int raised;

    private List<InvestorType> investorTypes;
    private List<Label> labels;
    private List<BoardMember> boardMembers;
    private Contact contact;
    private List<Founder> founders;
    private List<Shareholder> privateShareholders;
    private List<Shareholder> corporateShareholders;
    private int countryId;

    public Startup() {
        investorTypes = new ArrayList<>();
        labels = new ArrayList<>();
        boardMembers = new ArrayList<>();
        founders = new ArrayList<>();
        privateShareholders = new ArrayList<>();
        corporateShareholders = new ArrayList<>();
    }

    public void clearLabels() { labels.clear(); }
    public void clearInvestorTypes() { investorTypes.clear(); }
    public void clearBoardMembers() { boardMembers.clear(); }
    public void clearFounders() { founders.clear(); }
    public void clearPrivateShareholders() { privateShareholders.clear(); }
    public void clearCorporateShareholders() { corporateShareholders.clear(); }

    public void addLabel(Label label) { labels.add(label); }
    public void addInvestorType(InvestorType type) { investorTypes.add(type); }
    public void addBoardMember(BoardMember boardMember) { boardMembers.add(boardMember); }
    public void addFounder(Founder founder) { founders.add(founder); }
    public void addPrivateShareholder(Shareholder privateShareholder) {
        privateShareholders.add(privateShareholder);
    }
    public void addCorporateShareholder(Shareholder corporateShareholder) {
        corporateShareholders.add(corporateShareholder);
    }
}
