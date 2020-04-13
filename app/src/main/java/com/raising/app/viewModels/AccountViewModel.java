package com.raising.app.viewModels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raising.app.models.Account;
import com.raising.app.models.Image;
import com.raising.app.models.Investor;
import com.raising.app.models.Startup;
import com.raising.app.models.ViewState;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.Serializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AccountViewModel extends AndroidViewModel {
    private MutableLiveData<Account> currentAccount = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<ViewState>();

    private final String TAG = "accountViewModel";

    public AccountViewModel(@NonNull Application application) {
        super(application);
    }

    public void updateCompleted() {
        viewState.setValue(ViewState.RESULT);
    }

    /**
     * Get account of logged in user from backend
     */
    public void loadAccount() {
        if(!AuthenticationHandler.isLoggedIn()) {
            viewState.setValue(ViewState.ERROR);
            Log.e("AccountViewModel", "Trying to fetch account without being logged in!");
            return;
        }

        Account account = getCachedAccount();
        if(account != null) {
            currentAccount.setValue(account);
            viewState.setValue(ViewState.CACHED);
        } else {
            viewState.setValue(ViewState.LOADING);
        }

        if(AuthenticationHandler.isStartup()) {
            AccountService.getStartupAccount(AuthenticationHandler.getId(),
                    startup -> {
                        currentAccount.setValue(startup);
                        viewState.setValue(ViewState.RESULT);
                        cacheAccount();
                        return null;
                    });
        } else {
            AccountService.getInvestorAccount(AuthenticationHandler.getId(),
                    investor -> {
                        currentAccount.setValue(investor);
                        viewState.setValue(ViewState.RESULT);
                        cacheAccount();
                        return null;
                    });
        }
    }

    /**
     * Get the current view state
     * @return
     */
    public LiveData<ViewState> getViewState() {
        return viewState;
    }


    /**
     * Retrieve the currently stored account of the logged in user
     * @return The account currently stored
     */
    public LiveData<Account> getAccount() {
        return currentAccount;
    }

    /**
     * Clear account data
     */
    public void clearAccount() {
        currentAccount = new MutableLiveData<>();
    }


    /**
     * Get account saved in internal storage
     * @return
     */
    private Account getCachedAccount() {
        Account account = null;
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

                account.setEmail(AuthenticationHandler.getEmail());
            } catch(Exception e) {
                Log.e("AccountViewModel", "Error loading cached account: " +
                        e.getMessage());
                account = null;
            }
        }

        return account;
    }


    /**
     * Save current account to internal storage
     */
    private void cacheAccount() {
        if(AuthenticationHandler.isStartup()) {
            try {
                InternalStorageHandler.saveObject((Startup)currentAccount.getValue(),
                        "account_" + AuthenticationHandler.getId());
            } catch(Exception e) {
                Log.e("AccountViewModel", "Error caching account: " + e.getMessage());
            }
        } else {
            try {
                Gson gson = new Gson();
                InternalStorageHandler.saveObject((Investor)currentAccount.getValue(),
                        "account_" + AuthenticationHandler.getId());

            } catch(Exception e) {
                Log.e("AccountViewModel", "Error caching account: " + e.getMessage());
            }
        }
    }


    /**
     * Send patch request to backend to update account
     * @param update the new account instance
     */
    public void update(Account update) {
        String endpoint = null;
        String accountString = null;

        if(!(update instanceof Investor) && !(update instanceof Startup)) {
            Log.e("RegistrationHandler", "update: invalid account instance");
            return;
        }

        viewState.setValue(ViewState.LOADING);

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
                        if(update instanceof Startup) {
                            currentAccount.setValue((Startup)update);
                        } else if(update instanceof Investor) {
                            currentAccount.setValue((Investor)update);
                        } else {
                            viewState.setValue(ViewState.ERROR);
                            return null;
                        }

                        viewState.setValue(ViewState.UPDATED);
                        return null;
                    }, ApiRequestHandler.errorHandler,
                    new JSONObject(accountString));
        } catch (JSONException e) {
            viewState.setValue(ViewState.ERROR);
            Log.e("AccountViewModel", "Error while performing patch request: " +
                    e.getMessage());
        }
        Log.d("AccountViewModel", "patch request: " + accountString);
    }

    /**
     * Update the profile picture for the current account
     * @param image
     */
    public void updateProfilePicture(Image image) {
        HashMap<String, String> params = new HashMap<>();
        final Image lastImage = currentAccount.getValue().getProfilePicture();
        currentAccount.getValue().setProfilePicture(image);

        try {
            params.put("media", image.getImage());
            ApiRequestHandler.performPostRequest("account/profilepicture",
                    v -> {
                        Log.d(TAG, "Successfully updated profile picture");
                        return null;
                    }, err -> {
                        currentAccount.getValue().setProfilePicture(lastImage);
                        Log.e(TAG, "Error while updating profile picture: "
                                + ApiRequestHandler.parseVolleyError(err));
                        return null;
                    }, new JSONObject(params));
        } catch (Exception e) {
            Log.e(TAG, "Error while updating profile picture: " +
                    e.getMessage());
        }
    }
}
