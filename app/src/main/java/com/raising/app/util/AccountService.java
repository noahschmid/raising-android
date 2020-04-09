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
     * Get account of logged in user
     * @return
     */
    public static Account getAccount() {
        if(!AuthenticationHandler.isLoggedIn()) {
            Log.e("AuthenticationHandler", "ERROR: fetching account " +
                    "whithout being logged in!");
        }
        if(AuthenticationHandler.isStartup()) {
            return (Startup)account;
        }
        return (Investor)account;
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
            SimpleMessageDialog dialog =
                    new SimpleMessageDialog().newInstance(ResourcesManager.getContext().getString(R.string.not_logged_in_title),
                            ResourcesManager.getContext().getString(R.string.not_logged_in_text));
            dialog.show(ResourcesManager.getFragmentManager(), "errorDialog");
            return;
        }

        Function<JSONObject, Void> middleware = response -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Startup.class, new StartupDeserializer());
            Gson gson = gsonBuilder.create();
            Startup startup = gson.fromJson(response.toString(), Startup.class);
            callback.apply(startup);
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
            SimpleMessageDialog dialog =
                    new SimpleMessageDialog().newInstance(ResourcesManager.getContext().getString(R.string.not_logged_in_title),
                            ResourcesManager.getContext().getString(R.string.not_logged_in_text));
            dialog.show(ResourcesManager.getFragmentManager(), "errorDialog");
            return;
        }

        Function<JSONObject, Void> middleware = response -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            InvestorDeserializer deserializer = new InvestorDeserializer();
            gsonBuilder.registerTypeAdapter(Investor.class, deserializer);
            Gson gson = gsonBuilder.create();
            Log.d("AccountJSON", response.toString());
            Investor investor = gson.fromJson(response.toString(), Investor.class);
            callback.apply(investor);
            return null;
        };

        ApiRequestHandler.performGetRequest("investor/" + id,
                middleware, errorCallback);
    }

    /**
     * Error callback function
     */
    private static Function<VolleyError, Void> errorCallback = error -> {
        SimpleMessageDialog dialog =
                new SimpleMessageDialog().newInstance(
                        ResourcesManager.getContext().getString(R.string.generic_error_title),
                        ResourcesManager.getContext().getString(R.string.generic_error_text));
        dialog.show(ResourcesManager.getFragmentManager(), "errorDialog");
        Log.e("ProfileService", "Error while fetching profile: " + ApiRequestHandler.parseVolleyError(error));
        return null;
    };

    /**
     * Load the current users account
     */
    public static void loadAccount() {
        if(InternalStorageHandler.exists("account_" +
                AuthenticationHandler.getId())) {
            try {
                if(AuthenticationHandler.isStartup()) {
                    account = (Startup) InternalStorageHandler.loadObject("account_" +
                            AuthenticationHandler.getId());
                } else {
                    account = (Investor) InternalStorageHandler.loadObject("account_" +
                            AuthenticationHandler.getId());
                }

                account.setEmail(null);
                account.setPassword(null);
            } catch(Exception e) {
                Log.e("AccountService", "Error loading cached account details: " +
                        e.getMessage());
            }
        }

        if(AuthenticationHandler.isStartup()) {
            getStartupAccount(AuthenticationHandler.getId(), startup -> {
                account = new Startup();
                account = startup;
                account.setEmail(null);
                account.setPassword(null);
                account.setId(AuthenticationHandler.getId());

                try {
                    InternalStorageHandler.saveObject((Startup)account, "account_" +
                            AuthenticationHandler.getId());
                } catch(Exception e) {
                    Log.e("AccountService", "Error caching account: " + e.getMessage());
                }
                return null;
            });
        } else {
            AccountService.getInvestorAccount(AuthenticationHandler.getId(), investor -> {
                account = new Investor();
                account = investor;
                account.setEmail(null);
                account.setPassword(null);
                account.setId(AuthenticationHandler.getId());

                try {
                    Gson gson = new Gson();
                    InternalStorageHandler.saveObject((Investor)account, "account_" +
                            AuthenticationHandler.getId());

                    Log.d("AccountService", "saved: " + gson.toJson((Investor)account));
                } catch(Exception e) {
                    Log.e("AccountService", "Error caching account: " + e.getMessage());
                }

                return null;
            });
        }

        if(account instanceof Investor) {
            Log.d("AccountService", "is investor");
        } else if(account instanceof Startup) {
            Log.d("AccountService", "is startup");
        } else {
            Log.d("AccountService", "is nothing");
        }
    }

    public static void updateProfilePicture(Image image) {
        JSONObject params = new JSONObject();
        final Image lastImage = account.getProfilePicture();
        account.setProfilePicture(image);
        Log.d("AccountService", image.getImage());
        try {
            params.put("media", image.getImage());
            ApiRequestHandler.performPostRequest("account/profilepicture",
                    v -> {
                Log.d("AccountService", "Successfully updated profile picture");
                        return null;
                    }, err -> {
                        account.setProfilePicture(lastImage);
                        NotificationHandler.displayGenericError();
                        Log.e("AccountService", "Error while updating profile picture: "
                        + ApiRequestHandler.parseVolleyError(err));
                        return null;
                    }, params);
        } catch (Exception e) {
            Log.e("AccountService", "Error while updating profile picture: " +
                    e.getMessage());
        }
    }

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

    /**
     * Update account of logged in user
     */
    public static void updateAccount(Account update, Function<JSONObject, Void> callback) {
        String endpoint = null;
        String accountString = null;

        update.setEmail(contactDetails.getEmail());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Startup.class,
                Serializer.StartupUpdateSerializer);
        gsonBuilder.registerTypeAdapter(Investor.class,
                Serializer.InvestorUpdateSerializer);
        Gson gson = gsonBuilder.create();

        if(AuthenticationHandler.isStartup()) {
            endpoint = "startup/" + AuthenticationHandler.getId();
            accountString = gson.toJson((Startup)update);
        } else {
            endpoint = "investor/" + AuthenticationHandler.getId();
            accountString = gson.toJson((Investor)update);
        }

        try {
            ApiRequestHandler.performPatchRequest(endpoint,
                    v -> {
                        loadAccount();
                        callback.apply(v);
                    return null;
                    }, ApiRequestHandler.errorHandler,
                    new JSONObject(accountString));
        } catch (JSONException e) {
            Log.e("AccountService", "Error while performing patch request: " +
                    e.getMessage());
        }
        Log.d("AccountService", "patch request: " + accountString);
    }


    /**
     *
     * @param original
     * @param update
     * @param endpoint
     * @throws JSONException
     */
    public static void updateList(List<Long> original,
                                   List<Long> update,
                                   String endpoint) throws JSONException {
        boolean found;

        JSONArray delete = new JSONArray();
        JSONArray add = new JSONArray();

        for(Long o : original) {
            found = false;
            for(Long u : update) {
                if(u.equals(o)) {
                    update.remove(u);
                    found = true;
                    break;
                }
            }

            if(!found) {
                JSONObject tmp = new JSONObject();
                tmp.put("id", o);
                delete.put(tmp);
            }
        }

        for(Long u : update) {
            JSONObject tmp = new JSONObject();
            tmp.put("id", u);
            add.put(tmp);
        }

        if(delete.length() > 0) {
            ApiRequestHandler.performPostRequest(endpoint + "/delete", v -> {
                Log.d("AccountService", "List delete successful");
                return null;
            }, ApiRequestHandler.errorHandler, delete);
        }

        if(add.length() > 0) {
            ApiRequestHandler.performPostRequest(endpoint, v -> {
                Log.d("AccountService", "List add successful");
                return null;
            }, ApiRequestHandler.errorHandler, delete);
        }

        Log.d("AccountService", "delete: " +  delete.toString());
        Log.d("AccountService", "add: " + add.toString());
    }
}
