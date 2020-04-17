package com.raising.app.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raising.app.R;
import com.raising.app.models.Account;
import com.raising.app.models.ContactDetails;
import com.raising.app.models.Image;
import com.raising.app.models.Investor;
import com.raising.app.models.Model;
import com.raising.app.models.Startup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class AccountService {
    private static ContactDetails contactDetails = new ContactDetails();
    private static Account account = new Account();
    private final static String TAG = "AccountService";

    /**
     * Get private contact details of logged in user
     * @return profile of user, null if not logged in
     */
    public static ContactDetails getContactDetails() {
        if(!AuthenticationHandler.isLoggedIn()) {
            Log.e("AuthenticationHandler",
                    "ERROR: fetching contact details without being logged in");
        }
        return contactDetails;
    }

    /**
     * Check whether currently logged in user is startup
     * @return boolean indicating whether user is startup
     */
    public static boolean isStartup() {
        return AuthenticationHandler.isStartup();
    }

    /**
     * Get profile of startup
     * @param id
     * @param callback
     */
    public static void getStartupAccount(long id, Function<Startup, Void> callback) {
        if(!AuthenticationHandler.isLoggedIn()) {
            Log.e(TAG, "getStartupAccount: You are not logged in!");
            return;
        }

        Function<JSONObject, Void> middleware = response -> {
            try {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Startup.class, new StartupDeserializer());
                Gson gson = gsonBuilder.create();
                Startup startup = gson.fromJson(response.toString(), Startup.class);

                callback.apply(startup);
            } catch (Exception e) {
                Log.e(TAG, "getStartupAccount: " + e.getMessage());
            }
            return null;
        };

        ApiRequestHandler.performGetRequest("startup/" + id,
                middleware, errorCallback);
    }

    /**
     * Get profile of investor
     * @param id
     * @param callback
     */
    public static void getInvestorAccount(long id, Function<Investor, Void> callback) {
        if(!AuthenticationHandler.isLoggedIn()) {
            Log.e(TAG, "getInvestorAccount: You are not logged in!");
            return;
        }

        Function<JSONObject, Void> middleware = response -> {
            try {
                GsonBuilder gsonBuilder = new GsonBuilder();
                InvestorDeserializer deserializer = new InvestorDeserializer();
                gsonBuilder.registerTypeAdapter(Investor.class, deserializer);
                Gson gson = gsonBuilder.create();
                Log.d("AccountJSON", response.toString());
                Investor investor = gson.fromJson(response.toString(), Investor.class);
                callback.apply(investor);
            } catch(Exception e) {
                Log.e(TAG, "getInvestorAccount: " + e.getMessage() );
            }
            return null;
        };

        ApiRequestHandler.performGetRequest("investor/" + id,
                middleware, errorCallback);
    }

    /**
     * Error callback function
     */
    private static Function<VolleyError, Void> errorCallback = error -> {
        Log.e(TAG, "Error while fetching profile: " +
                ApiRequestHandler.parseVolleyError(error));
        return null;
    };

    /**
     * Load contact details from internal storage
     * @return true if process was successful, else false
     */
    public static boolean loadContactDetails() {
        try {
            contactDetails = (ContactDetails) InternalStorageHandler
                    .loadObject("contact_" +
                            AuthenticationHandler.getId());
            account.setEmail(contactDetails.getEmail());
            Log.d("AccountService", "contact details loaded successfully");
            Log.d("AccountService", "email: " + account.getEmail());
        } catch(Exception e) {
            Log.e("AccountService", "Error loading contact details: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Save private contact details to internal storage
     * @return true if process was successful, else false
     */
    public static boolean saveContactDetails(@Nullable ContactDetails details) {
        if(details != null) {
            contactDetails = details;
        }
        try {
            InternalStorageHandler.saveObject(contactDetails, "contact_" +
                    AuthenticationHandler.getId());
        } catch (Exception e) {
            Log.e("AccountService", "Error while saving contact details: " +
                    e.getMessage());
            return false;
        }
        return true;
    }
}
