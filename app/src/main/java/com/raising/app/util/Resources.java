package com.raising.app.util;

import com.raising.app.R;
import com.raising.app.models.Continent;
import com.raising.app.models.CorporateBody;
import com.raising.app.models.Country;
import com.raising.app.models.FinanceType;
import com.raising.app.models.Industry;
import com.raising.app.models.InvestmentPhase;
import com.raising.app.models.InvestorType;
import com.raising.app.models.Label;
import com.raising.app.models.Model;
import com.raising.app.models.Revenue;
import com.raising.app.models.Support;
import com.raising.app.models.TicketSize;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Resources implements Serializable {
    private ArrayList<Country> countries = new ArrayList<>();
    private ArrayList<Continent> continents = new ArrayList<>();
    private ArrayList<Support> supports = new ArrayList<>();
    private ArrayList<InvestorType> investorTypes = new ArrayList<>();
    private ArrayList<Label> labels = new ArrayList<>();
    private ArrayList<Industry> industries = new ArrayList<>();
    private ArrayList<InvestmentPhase> investmentPhases = new ArrayList<>();
    private ArrayList<Revenue> revenues = new ArrayList<>();
    private ArrayList<FinanceType> financeTypes = new ArrayList<>();
    private ArrayList<CorporateBody> corporateBodies = new ArrayList<>();
    private ArrayList<TicketSize> ticketSizes = new ArrayList<>();

    public Resources() {}

    /**
     * Get model from list by id
     * @param id the id to search for
     * @param list where to search in
     * @return search result, null if not found
     */
    public static Model findById(long id, ArrayList<? extends Model> list) {
        for(Model item : list) {
            if(item.getId() == id)
                return item;
        }
        return null;
    }

    public TicketSize getTicketSize(long id) {
        return (TicketSize) findById(id, getTicketSizes());
    }

    public FinanceType getFinanceType(long id) {
        return (FinanceType) findById(id, getFinanceTypes());
    }

    public Industry getIndustry(long id) {
        return (Industry) findById(id, getIndustries());
    }

    public Support getSupport(long id) {
        return (Support) findById(id, getSupports());
    }

    public InvestmentPhase getInvestmentPhase(long id) {
        return (InvestmentPhase)findById(id, getInvestmentPhases());
    }

    public String[] getTicketSizeStrings(String currency, String[] units) {
        ArrayList<String> result = new ArrayList();
        getTicketSizes().forEach(size -> result.add(size.toString(currency, units)));
        return result.toArray(new String[0]);
    }

    public int[] getTicketSizeValues() {
        ArrayList<Integer> result = new ArrayList();
        getTicketSizes().forEach(size -> result.add(size.getTicketSize()));
        return result.stream().mapToInt(Integer::valueOf).toArray();
    }

    public Country getCountry(long id) { return (Country)findById(id, getCountries()); }
    public Continent getContinent(long id) { return (Continent)findById(id, getContinents()); }
    public InvestorType getInvestorType(long id) {
        return (InvestorType)findById(id, getInvestorTypes());
    }

    public Revenue getRevenue(int minId) {
        for(Revenue rev : getRevenues()) {
            if(rev.getRevenueMinId() == minId)
                return rev;
        }
        return null;
    }

    public CorporateBody getCorporateBody(int id) {
        for(CorporateBody body : getCorporateBodies()) {
            if(body.getId() == id)
                return body;
        }
        return null;
    }
}
