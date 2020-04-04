package com.raising.app.util;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.android.volley.VolleyError;
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
import com.raising.app.models.PrivateProfile;
import com.raising.app.models.Revenue;
import com.raising.app.models.Support;
import com.raising.app.models.TicketSize;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

public class ResourcesManager implements Serializable {
    private static final int REQUESTS_SENT = 11;
    private static int responsesGot = 0;
    private static boolean loadingSuccessful = false;

    private static ArrayList<Country> countries = new ArrayList<>();
    private static ArrayList<Continent> continents = new ArrayList<>();
    private static ArrayList<Support> supports = new ArrayList<>();
    private static ArrayList<InvestorType> investorTypes = new ArrayList<>();
    private static ArrayList<Label> labels = new ArrayList<>();
    private static ArrayList<Industry> industries = new ArrayList<>();
    private static ArrayList<InvestmentPhase> investmentPhases = new ArrayList<>();
    private static ArrayList<Revenue> revenues = new ArrayList<>();
    private static ArrayList<FinanceType> financeTypes = new ArrayList<>();
    private static ArrayList<CorporateBody> corporateBodies = new ArrayList<>();
    private static ArrayList<TicketSize> ticketSizes = new ArrayList<>();

    private static Context context;
    private static FragmentManager fragmentManager;

    public static void init(Context ctx, FragmentManager fManager) {
        fragmentManager = fManager;
        context = ctx;
    }

    /**
     * Get model from list by id
     * @param id the id to search for
     * @param list where to search in
     * @return search result, null if not found
     */
    public static Model findById(int id, ArrayList<? extends Model> list) {
        for(Model item : list) {
            if(item.getId() == id)
                return item;
        }
        return null;
    }

    /**
     * Load all resources from backend
     */
    public static void loadAll() {
        if(context == null)
            throw new Error("No context found. Did you call init?");

        loadingSuccessful = true;

        ApiRequestHandler.performArrayGetRequest("public/country", loadCountries,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/continent", loadContinents,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/support", loadSupport,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/investmentphase", loadInvestmentPhases,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/investortype", loadInvestorTypes,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/label", loadLabels,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/industry", loadIndustries,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/revenue", loadRevenues,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/financetype", loadFinanceTypes,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/corporatebody", loadCorporateBodies,
                errorHandler, context);
        ApiRequestHandler.performArrayGetRequest("public/ticketsize", loadTicketSizes,
                errorHandler, context);
    }

    public static ArrayList<Country> getCountries() { return countries; }
    public static ArrayList<Continent> getContinents() { return continents; }
    public static ArrayList<Industry> getIndustries() { return industries; }
    public static ArrayList<InvestmentPhase> getInvestmentPhases() { return investmentPhases; }
    public static ArrayList<InvestorType> getInvestorTypes() { return investorTypes; }
    public static ArrayList<Label> getLabels() { return labels; }
    public static ArrayList<Support> getSupports() { return supports; }
    public static ArrayList<Revenue> getRevenues() { return revenues; }
    public static ArrayList<FinanceType> getFinanceTypes() { return financeTypes; }
    public static ArrayList<CorporateBody> getCorporateBodies() { return corporateBodies; }
    public static ArrayList<TicketSize> getTicketSizes() { return ticketSizes; }

    public static String[] getTicketSizeStrings(String currency, String[] units) {
        ArrayList<String> result = new ArrayList();
        ticketSizes.forEach(size -> result.add(size.toString(currency, units)));
        return result.toArray(new String[0]);
    }

    public static int[] getTicketSizeValues() {
        ArrayList<Integer> result = new ArrayList();
        ticketSizes.forEach(size -> result.add(size.getTicketSize()));
        return result.stream().mapToInt(Integer::valueOf).toArray();
    }

    public static Country getCountry(int id) { return (Country)findById(id, countries); }
    public static Continent getContinent(int id) { return (Continent)findById(id, continents); }
    public static InvestorType getInvestorType(int id) { return (InvestorType)findById(id, investorTypes); }
    public static Revenue getRevenue(int minId) {
        for(Revenue rev : revenues) {
            if(rev.getRevenueMinId() == minId)
                return rev;
        }
        return null;
    }

    public static CorporateBody getCorporateBody(int id) {
        for(CorporateBody body : corporateBodies) {
            if(body.getId() == id)
                return body;
        }
        return null;
    }

    public static String getRevenueString(int minId) {
        Revenue rev = getRevenue(minId);
        if(rev == null)
            return "";
        return rev.toString(context.getString(R.string.currency),
                context.getResources().getStringArray(R.array.revenue_units));
    }

    /**
     * Check whether all backend requests have returned and if so, check whether they were
     * all successful or not. If not, notify main activity
     */
    private static void handleProcess() {
        if(responsesGot != REQUESTS_SENT)
            return;

        if(!loadingSuccessful) {
            SimpleMessageDialog dialog =
                    new SimpleMessageDialog().newInstance(context.getString(R.string.server_error),
                            context.getString(R.string.server_error_msg_rsc));
            dialog.show(fragmentManager, "server_error");
        }
    }

    /**
     * Handle error returned by backend server
     */
    static Function<VolleyError, Void> errorHandler = volleyError -> {
        ++responsesGot;
        loadingSuccessful = false;
        handleProcess();
        Log.d("debugMessage", "Volley error: " +
                ApiRequestHandler.parseVolleyError(volleyError));
        return null;
    };

    /**
     * Handle backend response and load countries into arraylist
     */
    static Function<JSONArray, Void> loadCountries = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Country country = new Country();
                country.setId(jresponse.getLong("id"));
                country.setName(jresponse.getString("name"));
                country.setContinentId(jresponse.getLong("continentId"));

                if(country.getName().length() > 0)
                    countries.add(country);
            }

            if(countries.isEmpty())
                loadingSuccessful = false;

        } catch (JSONException e) {
            Log.d("debugMessage", "Error while parsing countries: " + e.getMessage());
            loadingSuccessful = false;
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Handle backend response and load continents into arraylist
     */
    static Function<JSONArray, Void> loadContinents = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Continent continent = new Continent();
                continent.setId(jresponse.getLong("id"));
                continent.setName(jresponse.getString("name"));
                if(continent.getName().length() > 0)
                    continents.add(continent);
            }

            if(continents.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadContinents");
                loadingSuccessful = false;
            }
        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing continents: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Handle backend response and load investor types into arraylist
     */
    static Function<JSONArray, Void> loadInvestorTypes = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                InvestorType investorType = new InvestorType();
                investorType.setId(jresponse.getLong("id"));
                investorType.setName(jresponse.getString("name"));
                if(investorType.getName().length() > 0)
                    investorTypes.add(investorType);
            }

            if(investorTypes.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadInvestorTypes");
                loadingSuccessful = false;
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing investor types: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Handle backend response and load support types into arraylist
     */
    static Function<JSONArray, Void> loadSupport = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Support support = new Support();
                support.setId(jresponse.getLong("id"));
                support.setName(jresponse.getString("name"));
                if(support.getName().length() > 0)
                    supports.add(support);
            }

            if(supports.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadSupports");
                loadingSuccessful = false;
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing support types: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Handle backend response and load support types into arraylist
     */
    static Function<JSONArray, Void> loadInvestmentPhases = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                InvestmentPhase investmentPhase = new InvestmentPhase();
                investmentPhase.setId(jresponse.getLong("id"));
                investmentPhase.setName(jresponse.getString("name"));
                if(investmentPhase.getName().length() > 0)
                    investmentPhases.add(investmentPhase);
            }

            if(investmentPhases.isEmpty()) {
                loadingSuccessful = false;
                Log.d("debugMessage", "Fetched an empty array in loadInvestmentPhases");
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing investment phases: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Handle backend response and load industries into arraylist
     */
    static Function<JSONArray, Void> loadIndustries = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Industry industry = new Industry();
                industry.setId(jresponse.getLong("id"));
                industry.setName(jresponse.getString("name"));
                if(industry.getName().length() > 0)
                    industries.add(industry);
            }

            if(industries.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadIndustries");
                loadingSuccessful = false;
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing industries: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Handle backend response and load labels into arraylist
     */
    static Function<JSONArray, Void> loadLabels = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Label label = new Label();
                label.setId(jresponse.getLong("id"));
                label.setName(jresponse.getString("name"));
                if(label.getName().length() > 0)
                    labels.add(label);
            }

            if(labels.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadLabels");
                loadingSuccessful = false;
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing labels: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Handle backend response and load revenues into arraylist
     */
    static Function<JSONArray, Void> loadRevenues = response -> {
        try {
            int lastId = 0;
            int lastValue = 0;
            Revenue revenue;
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                if(i > 0) {
                    revenue = new Revenue();
                    revenue.setRevenueMinId(lastId);
                    revenue.setRevenueMin(lastValue);
                    revenue.setRevenueMaxId(jresponse.getInt("id"));
                    revenue.setRevenueMax(jresponse.getInt("name"));
                    revenues.add(revenue);
                }

                lastId = jresponse.getInt("id");
                lastValue = jresponse.getInt("name");
            }

            if(revenues.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadRevenues");
                loadingSuccessful = false;
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing revenues: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Handle backend response and load financetypes into arraylist
     */
    static Function<JSONArray, Void> loadFinanceTypes = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                FinanceType financeType = new FinanceType();
                financeType.setId(jresponse.getInt("id"));
                financeType.setName(jresponse.getString("name"));
                financeTypes.add(financeType);
            }

            if(financeTypes.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadFinanceTypes");
                loadingSuccessful = false;
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing finance types: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Handle backend response and load corporate bodies into arraylist
     */
    static Function<JSONArray, Void> loadCorporateBodies = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                CorporateBody corporateBody = new CorporateBody();
                corporateBody.setId(jresponse.getInt("id"));
                corporateBody.setName(jresponse.getString("name"));
                corporateBodies.add(corporateBody);
            }

            if(corporateBodies.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadCorporateBodies");
                loadingSuccessful = false;
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing corporate bodies: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };

    /**
     * Load ticket sizes from backend to array list
     */
    public static Function<JSONArray, Void> loadTicketSizes = response -> {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                TicketSize ticketSize = new TicketSize();
                ticketSize.setId(jresponse.getInt("id"));
                ticketSize.setTicketSize(jresponse.getInt("name"));
                ticketSizes.add(ticketSize);
            }

            if(ticketSizes.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadTicketSizes");
                loadingSuccessful = false;
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("debugMessage", "Error while parsing ticket sizes: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };
}
