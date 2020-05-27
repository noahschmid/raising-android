package com.raising.app.util;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raising.app.R;
import com.raising.app.models.Account;
import com.raising.app.models.ContactData;
import com.raising.app.models.Investor;
import com.raising.app.models.Startup;
import com.raising.app.models.ViewState;
import com.raising.app.viewModels.ViewStateViewModel;

import org.json.JSONObject;

import java.util.function.Function;

/**
 * This class handles loading of accounts and contact data as well as saving new contact data
 * from other accounts
 */

public class AccountService {
    private static ContactData contactData = new ContactData();
    private static Account account = new Account();
    private final static String TAG = "AccountService";

    /**
     * Get private contact details of logged in user
     * @return profile of user, null if not logged in
     */
    public static ContactData getContactData() {
        if(!AuthenticationHandler.isLoggedIn()) {
            Log.e("AuthenticationHandler",
                    "ERROR: fetching contact data without being logged in");
        }
        if(contactData.getEmail() == null)
            loadContactData(AuthenticationHandler.getId());
        return contactData;
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
        if(error.networkResponse != null) {
            if(error.networkResponse.statusCode == 403) {
                ViewStateViewModel viewStateViewModel = ViewModelProviders
                        .of(InternalStorageHandler.getActivity()).get(ViewStateViewModel.class);
                viewStateViewModel.setViewState(ViewState.EXPIRED);
            }
        }
        Log.e(TAG, "Error while fetching profile: " +
                ApiRequestHandler.parseVolleyError(error));
        return null;
    };

    /**
     * Load contact details from internal storage
     * @return true if process was successful, else false
     */
    public static boolean loadContactData(long id) {
        try {
            contactData = (ContactData) InternalStorageHandler
                    .loadObject("contact_" + id);
            account.setEmail(contactData.getEmail());
            contactData.setAccountId(id);
            Log.d("AccountService", "contact data loaded successfully");
            Log.d("AccountService", "email: " + account.getEmail());
        } catch(Exception e) {
            Log.e("AccountService", "Error loading contact data: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Save private contact data to internal storage
     * @return true if process was successful, else false
     */
    public static boolean saveContactData(@Nullable ContactData details) {
        if(details != null) {
            contactData = details;
        }
        try {
            InternalStorageHandler.saveObject(contactData, "contact_" +
                    AuthenticationHandler.getId());
        } catch (Exception e) {
            Log.e("AccountService", "Error while saving contact data: " +
                    e.getMessage());
            return false;
        }
        return true;
    }
}
