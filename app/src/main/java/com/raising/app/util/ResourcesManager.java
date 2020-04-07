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
import com.raising.app.models.Revenue;
import com.raising.app.models.Support;
import com.raising.app.models.TicketSize;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

public class ResourcesManager implements Serializable {
    private static final int REQUESTS_SENT = 11;
    private static int responsesGot = 0;
    private static boolean loadingSuccessful = false;

    private static Resources resources = new Resources();

    private static Context context;
    private static FragmentManager fragmentManager;

    /**
     * Set context and fragment manager and load cached resources if existing
     * @param ctx
     * @param fManager
     */
    public static void init(Context ctx, FragmentManager fManager) {
        fragmentManager = fManager;
        context = ctx;

        try {
            if(InternalStorageHandler.exists("resources")) {
                resources = (Resources) InternalStorageHandler.loadObject("resources");
            }
        } catch (Exception e) {
            SimpleMessageDialog dialog =
                    new SimpleMessageDialog().newInstance(context.getString(R.string.generic_error_title),
                            context.getString(R.string.generic_error_text));
            dialog.show(fragmentManager, "resources_error");
            Log.e("ResourcesManager", "Error while loading cached resources: " + e.getMessage());
        }
    }

    public static Context getContext() { return context; }

    public static FragmentManager getFragmentManager() { return fragmentManager; }

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

    /**
     * Load all resources from backend
     */
    public static void loadAll() {
        if(context == null)
            throw new Error("No context found. Did you call init?");

        loadingSuccessful = true;

        ApiRequestHandler.performArrayGetRequest("public/country", loadCountries,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/continent", loadContinents,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/support", loadSupport,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/investmentphase", loadInvestmentPhases,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/investortype", loadInvestorTypes,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/label", loadLabels,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/industry", loadIndustries,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/revenue", loadRevenues,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/financetype", loadFinanceTypes,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/corporatebody", loadCorporateBodies,
                errorHandler);
        ApiRequestHandler.performArrayGetRequest("public/ticketsize", loadTicketSizes,
                errorHandler);
    }

    public static ArrayList<Country> getCountries() { return resources.getCountries(); }
    public static ArrayList<Continent> getContinents() { return resources.getContinents(); }
    public static ArrayList<Industry> getIndustries() { return resources.getIndustries(); }
    public static ArrayList<InvestmentPhase> getInvestmentPhases() { return resources.getInvestmentPhases(); }
    public static ArrayList<InvestorType> getInvestorTypes() { return resources.getInvestorTypes(); }
    public static ArrayList<Label> getLabels() { return resources.getLabels(); }
    public static ArrayList<Support> getSupports() { return resources.getSupports(); }
    public static ArrayList<Revenue> getRevenues() { return resources.getRevenues(); }
    public static ArrayList<FinanceType> getFinanceTypes() { return resources.getFinanceTypes(); }
    public static ArrayList<CorporateBody> getCorporateBodies() { return resources.getCorporateBodies(); }
    public static ArrayList<TicketSize> getTicketSizes() { return resources.getTicketSizes(); }

    public static String[] getTicketSizeStrings(String currency, String[] units) {
        ArrayList<String> result = new ArrayList();
        resources.getTicketSizes().forEach(size -> result.add(size.toString(currency, units)));
        return result.toArray(new String[0]);
    }

    public static int[] getTicketSizeValues() {
        ArrayList<Integer> result = new ArrayList();
        resources.getTicketSizes().forEach(size -> result.add(size.getTicketSize()));
        return result.stream().mapToInt(Integer::valueOf).toArray();
    }

    public static Country getCountry(long id) { return (Country)findById(id, resources.getCountries()); }
    public static Continent getContinent(long id) { return (Continent)findById(id, resources.getContinents()); }
    public static InvestorType getInvestorType(long id) {
        return (InvestorType)findById(id, resources.getInvestorTypes());
    }

    public static Revenue getRevenue(int minId) {
        for(Revenue rev : resources.getRevenues()) {
            if(rev.getRevenueMinId() == minId)
                return rev;
        }
        return null;
    }

    public static CorporateBody getCorporateBody(int id) {
        for(CorporateBody body : resources.getCorporateBodies()) {
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
            Log.e("ResourcesManager", "Error while fetching resources from server!");
        } else {
            try {
                InternalStorageHandler.saveObject(resources, "resources");
            } catch(IOException e) {
                SimpleMessageDialog dialog =
                        new SimpleMessageDialog().newInstance(context.getString(R.string.generic_error_title),
                                context.getString(R.string.generic_error_text));
                dialog.show(fragmentManager, "resources_error");
                Log.e("ResourcesManager", "Error while caching resources: " + e.getMessage());
            }
        }
    }

    /**
     * Handle error returned by backend server
     */
    static Function<VolleyError, Void> errorHandler = volleyError -> {
        ++responsesGot;
        loadingSuccessful = false;
        handleProcess();
        Log.e("ResourcesManager", "Volley error: " +
                ApiRequestHandler.parseVolleyError(volleyError));
        return null;
    };

    /**
     * Handle backend response and load countries into arraylist
     */
    static Function<JSONArray, Void> loadCountries = response -> {
        try {
            ArrayList<Country> countries = new ArrayList<Country>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Country country = new Country();
                country.setId(jresponse.getLong("id"));
                country.setName(jresponse.getString("name"));
                country.setContinentId(jresponse.getLong("continentId"));

                if(country.getName().length() > 0)
                    countries.add(country);
            }

            if(countries.isEmpty()) {
                loadingSuccessful = false;
                Log.e("ResourcesManager", "Fetched an empty array in getCountries");
            } else {
                resources.setCountries(countries);
            }
        } catch (JSONException e) {
            Log.e("debugMessage", "Error while parsing countries: " + e.getMessage());
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
            ArrayList<Continent> continents = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Continent continent = new Continent();
                continent.setId(jresponse.getLong("id"));
                continent.setName(jresponse.getString("name"));
                if(continent.getName().length() > 0)
                    continents.add(continent);
            }

            if(continents.isEmpty()) {
                Log.e("ResourcesManager", "Fetched an empty array in loadContinents");
                loadingSuccessful = false;
            } else {
                resources.setContinents(continents);
            }
        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.e("ResourcesManager", "Error while parsing continents: " + e.getMessage());
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
            ArrayList<InvestorType> investorTypes = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                InvestorType investorType = new InvestorType();
                investorType.setId(jresponse.getLong("id"));
                investorType.setName(jresponse.getString("name"));
                if(investorType.getName().length() > 0)
                    investorTypes.add(investorType);
            }

            if(investorTypes.isEmpty()) {
                Log.e("ResourcesManager", "Fetched an empty array in loadInvestorTypes");
                loadingSuccessful = false;
            } else {
                resources.setInvestorTypes(investorTypes);
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.e("ResourcesManager", "Error while parsing investor types: " + e.getMessage());
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
            ArrayList<Support> supports = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Support support = new Support();
                support.setId(jresponse.getLong("id"));
                support.setName(jresponse.getString("name"));
                if(support.getName().length() > 0)
                    supports.add(support);
            }

            if(supports.isEmpty()) {
                Log.e("ResourcesManager", "Fetched an empty array in loadSupports");
                loadingSuccessful = false;
            } else {
                resources.setSupports(supports);
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.e("ResourcesManager", "Error while parsing support types: " + e.getMessage());
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
            ArrayList<InvestmentPhase> investmentPhases = new ArrayList<>();
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
                Log.e("ResourcesManager", "Fetched an empty array in loadInvestmentPhases");
            } else {
                resources.setInvestmentPhases(investmentPhases);
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.e("ResourcesManager", "Error while parsing investment phases: " + e.getMessage());
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
            ArrayList<Industry> industries = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Industry industry = new Industry();
                industry.setId(jresponse.getLong("id"));
                industry.setName(jresponse.getString("name"));
                if(industry.getName().length() > 0)
                    industries.add(industry);
            }

            if(industries.isEmpty()) {
                Log.e("ResourcesManager", "Fetched an empty array in loadIndustries");
                loadingSuccessful = false;
            } else {
                resources.setIndustries(industries);
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.e("ResourcesManager", "Error while parsing industries: " + e.getMessage());
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
            ArrayList<Label> labels = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Label label = new Label();
                label.setId(jresponse.getLong("id"));
                label.setName(jresponse.getString("name"));
                if(label.getName().length() > 0)
                    labels.add(label);
            }

            if(labels.isEmpty()) {
                Log.e("ResourcesManager", "Fetched an empty array in loadLabels");
                loadingSuccessful = false;
            } else {
                resources.setLabels(labels);
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.e("ResourcesManager", "Error while parsing labels: " + e.getMessage());
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
            ArrayList<Revenue> revenues = new ArrayList<>();
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
                Log.e("ResourcesManager", "Fetched an empty array in loadRevenues");
                loadingSuccessful = false;
            } else {
                resources.setRevenues(revenues);
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.d("ResourcesManager", "Error while parsing revenues: " + e.getMessage());
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
            ArrayList<FinanceType> financeTypes = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                FinanceType financeType = new FinanceType();
                financeType.setId(jresponse.getInt("id"));
                financeType.setName(jresponse.getString("name"));
                financeTypes.add(financeType);
            }

            if(financeTypes.isEmpty()) {
                Log.e("ResourcesManager", "Fetched an empty array in loadFinanceTypes");
                loadingSuccessful = false;
            } else {
                resources.setFinanceTypes(financeTypes);
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.e("ResourcesManager", "Error while parsing finance types: " + e.getMessage());
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
            ArrayList<CorporateBody> corporateBodies = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                CorporateBody corporateBody = new CorporateBody();
                corporateBody.setId(jresponse.getInt("id"));
                corporateBody.setName(jresponse.getString("name"));
                corporateBodies.add(corporateBody);
            }

            if(corporateBodies.isEmpty()) {
                Log.e("ResourcesManager", "Fetched an empty array in loadCorporateBodies");
                loadingSuccessful = false;
            } else {
                resources.setCorporateBodies(corporateBodies);
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.e("ResourcesManager", "Error while parsing corporate bodies: " + e.getMessage());
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
            ArrayList<TicketSize> ticketSizes = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                TicketSize ticketSize = new TicketSize();
                ticketSize.setId(jresponse.getInt("id"));
                ticketSize.setTicketSize(jresponse.getInt("name"));
                ticketSizes.add(ticketSize);
            }

            if(ticketSizes.isEmpty()) {
                Log.e("ResourcesManager", "Fetched an empty array in loadTicketSizes");
                loadingSuccessful = false;
            } else {
                resources.setTicketSizes(ticketSizes);
            }

        } catch (JSONException e) {
            loadingSuccessful = false;
            Log.e("ResourcesManager", "Error while parsing ticket sizes: " + e.getMessage());
        }

        ++responsesGot;
        handleProcess();
        return null;
    };
}
