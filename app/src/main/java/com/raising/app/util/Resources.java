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

/**
 * This class manages lists containing public resources (for example countries, investor types etc.)
 * and tasks associated with it (for example formatting)
 */

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
    public Model findById(long id, ArrayList<? extends Model> list) {
        for(Model item : list) {
            if(item.getId() == id)
                return item;
        }
        return null;
    }

    /**
     * Transform a boring looking integer value to a pretty formatted string in the form of
     * 1000 -> 1K, 1000000 -> 1M
     * @param amount the number to "prettify"
     * @return good looking string
     */
    public String formatMoneyAmount(int amount) {
        int i = 0;
        String[] units = InternalStorageHandler.getContext().getResources()
                .getStringArray(R.array.revenue_units);
        String unit = "", currency = InternalStorageHandler.getContext().getString(R.string.currency);

        while(Math.log10(amount) >= 3 && i < units.length) {
            amount /= 1000;
            unit = units[i];
            ++i;
        }

        return  currency + " " + amount + unit;
    }

    // - STANDARD GETTERS FOR ALL LISTS -
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

    public Country getCountry(long id) { return (Country)findById(id, getCountries()); }

    public Continent getContinent(long id) { return (Continent)findById(id, getContinents()); }

    public InvestorType getInvestorType(long id) {
        return (InvestorType)findById(id, getInvestorTypes());
    }

    /**
     * Get formatted strings for all ticket sizes
     * @param currency localized currency string
     * @param units localized unit string (for example k, M, B for thousand, million, billion)
     * @return Array of strings containing ticket sizes
     */
    public String[] getTicketSizeStrings(String currency, String[] units) {
        ArrayList<String> result = new ArrayList();
        getTicketSizes().forEach(size -> result.add(size.toString(currency, units)));
        return result.toArray(new String[0]);
    }

    /**
     * Get values of ticket sizes instead of string
     * @return Array of integers containing ticket sizes
     */
    public int[] getTicketSizeValues() {
        ArrayList<Integer> result = new ArrayList();
        getTicketSizes().forEach(size -> result.add(size.getTicketSize()));
        return result.stream().mapToInt(Integer::valueOf).toArray();
    }

    /**
     * Get pretty printed string of revenue range
     * @param minId the id of the minimum revenue
     * @return string in the form of CHF 1k - 5k
     */
    public String getRevenueString(int minId) {
        Revenue rev = getRevenue(minId);
        if(rev == null)
            return "";
        return rev.toString(InternalStorageHandler.getContext().getString(R.string.currency),
                InternalStorageHandler.getContext().getResources().getStringArray(R.array.revenue_units));
    }

    /**
     * Get revenue range from the id of the lower end revenue
     * @param minId id of lower end revenue
     * @return Revenue instance
     */
    public Revenue getRevenue(int minId) {
        for(Revenue rev : getRevenues()) {
            if(rev.getRevenueMinId() == minId)
                return rev;
        }
        return null;
    }

    /**
     * Get corporate body by id
     * @param id id of corporate body
     * @return CorporateBody instance
     */
    public CorporateBody getCorporateBody(int id) {
        for(CorporateBody body : getCorporateBodies()) {
            if(body.getId() == id)
                return body;
        }
        return null;
    }
}
