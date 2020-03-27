package com.raising.app.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.raising.app.R;
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Industry;
import com.raising.app.models.InvestmentPhase;
import com.raising.app.models.InvestorType;
import com.raising.app.models.Label;
import com.raising.app.models.Revenue;
import com.raising.app.models.Support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.Function;

public class ResourcesManager {
    private static final int REQUESTS_SENT = 8;
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

    private static Context ctx;

    public static void init(Context context) {
        ctx = context;
    }

    /**
     * Load all resources from backend
     */
    public static void loadAll() {
        if(ctx == null)
            throw new Error("No context found. Did you call init?");

        loadingSuccessful = true;

        ApiRequestHandler.performArrayGetRequest("public/country", loadCountries,
                errorHandler, ctx);
        ApiRequestHandler.performArrayGetRequest("public/continent", loadContinents,
                errorHandler, ctx);
        ApiRequestHandler.performArrayGetRequest("public/support", loadSupport,
                errorHandler, ctx);
        ApiRequestHandler.performArrayGetRequest("public/investmentphase", loadInvestmentPhases,
                errorHandler, ctx);
        ApiRequestHandler.performArrayGetRequest("public/investortype", loadInvestorTypes,
                errorHandler, ctx);
        ApiRequestHandler.performArrayGetRequest("public/label", loadLabels,
                errorHandler, ctx);
        ApiRequestHandler.performArrayGetRequest("public/industry", loadIndustries,
                errorHandler, ctx);
        ApiRequestHandler.performArrayGetRequest("public/revenue", loadRevenues,
                errorHandler, ctx);
    }

    public static ArrayList<Country> getCountries() { return countries; }
    public static ArrayList<Continent> getContinents() { return continents; }
    public static ArrayList<Industry> getIndustries() { return industries; }
    public static ArrayList<InvestmentPhase> getInvestmentPhases() { return investmentPhases; }
    public static ArrayList<InvestorType> getInvestorTypes() { return investorTypes; }
    public static ArrayList<Label> getLabels() { return labels; }
    public static ArrayList<Support> getSupports() { return supports; }
    public static ArrayList<Revenue> getRevenues() { return revenues; }

    /**
     * Check whether all backend requests have returned and if so, check whether they were
     * all successful or not. If not, notify main activity
     */
    private static void handleProcess() {
        if(responsesGot != REQUESTS_SENT)
            return;
        if(!loadingSuccessful) {
            // TODO: notification that resources didn't load
            throw new Error("Couldn't fetch all data from backend!");
        }
    }

    /**
     * Handle error returned by backend server
     */
    static Function<VolleyError, Void> errorHandler = volleyError -> {
        ++responsesGot;
        Log.d("debugMessage", "Volley error: " +
                ApiRequestHandler.parseVolleyError(volleyError));
        handleProcess();
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

                Log.d("debugMessage", "name: " + industry.getName() + " id: " + industry.getId());
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
            for (int i = 0; i < response.length(); i++) {
                JSONObject jresponse = response.getJSONObject(i);
                Revenue revenue = new Revenue();
                revenue.setId(jresponse.getLong("id"));
                revenue.setName(jresponse.getString("name"));
                if(revenue.getName().length() > 0)
                    revenues.add(revenue);
            }

            if(revenues.isEmpty()) {
                Log.d("debugMessage", "Fetched an empty array in loadRevenues");
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
}
