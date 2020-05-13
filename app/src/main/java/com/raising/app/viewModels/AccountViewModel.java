package com.raising.app.viewModels;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.raising.app.models.Account;
import com.raising.app.models.Image;
import com.raising.app.models.Investor;
import com.raising.app.models.Match;
import com.raising.app.models.Startup;
import com.raising.app.models.ViewState;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.ImageUploader;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.Serializer;
import com.raising.app.util.ToastHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    private MutableLiveData<Account> currentAccount = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<ViewState>();

    private final String TAG = "AccountViewModel";

    public AccountViewModel(@NonNull Application application) {
        super(application);
        viewState.setValue(ViewState.EMPTY);
    }

    public void updateCompleted() {
        viewState.postValue(ViewState.RESULT);
    }

    public void setAccount(Account account) {
        currentAccount.postValue(account);
    }

    /**
     * Get account of logged in user from backend
     */
    public void loadAccount() {
        if (!AuthenticationHandler.isLoggedIn()) {
            Log.d(TAG, "loadAccountE1: ViewState" + getViewState().getValue().toString());
            Log.e("AccountViewModel", "Trying to fetch account without being logged in!");
            return;
        }

        Account account = getCachedAccount();
        if (account != null) {
            currentAccount.postValue(account);
            viewState.postValue(ViewState.CACHED);
            Log.d(TAG, "loadAccountC1: ViewState" + getViewState().getValue().toString());
        } else {
            viewState.postValue(ViewState.LOADING);
            Log.d(TAG, "loadAccountL1: ViewState" + getViewState().getValue().toString());
        }

        if (AuthenticationHandler.isStartup()) {
            AccountService.getStartupAccount(AuthenticationHandler.getId(),
                    startup -> {
                        currentAccount.postValue(startup);
                        viewState.postValue(ViewState.RESULT);
                        Log.d(TAG, "loadAccountR1: ViewState" + getViewState().getValue().toString());
                        cacheAccount();
                        return null;
                    });
        } else {
            AccountService.getInvestorAccount(AuthenticationHandler.getId(),
                    investor -> {
                        currentAccount.postValue(investor);
                        viewState.postValue(ViewState.RESULT);
                        Log.d(TAG, "loadAccountR2: ViewState" + getViewState().getValue().toString());
                        cacheAccount();
                        return null;
                    });
        }
    }

    /**
     * Get the current view state
     *
     * @return
     */
    public LiveData<ViewState> getViewState() {
        return viewState;
    }


    /**
     * Retrieve the currently stored account of the logged in user
     *
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
     *
     * @return
     */
    private Account getCachedAccount() {
        Account account = null;
        if (InternalStorageHandler.exists("account_" +
                AuthenticationHandler.getId())) {
            try {
                if (AuthenticationHandler.isStartup()) {
                    account = (Startup) InternalStorageHandler.loadObject("account_" +
                            AuthenticationHandler.getId());
                    Log.d(TAG, "getCachedAccount: " + account);
                } else {
                    account = (Investor) InternalStorageHandler.loadObject("account_" +
                            AuthenticationHandler.getId());
                    Log.d(TAG, "getCachedAccount: " + account);
                }
                account.setEmail(AuthenticationHandler.getEmail());
            } catch (Exception e) {
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
        if (AuthenticationHandler.isStartup()) {
            try {
                InternalStorageHandler.saveObject((Startup) currentAccount.getValue(),
                        "account_" + AuthenticationHandler.getId());
            } catch (Exception e) {
                Log.e("AccountViewModel", "Error caching account: " + e.getMessage());
            }
        } else {
            try {
                Gson gson = new Gson();
                InternalStorageHandler.saveObject((Investor) currentAccount.getValue(),
                        "account_" + AuthenticationHandler.getId());

            } catch (Exception e) {
                Log.e("AccountViewModel", "Error caching account: " + e.getMessage());
            }
        }
    }


    /**
     * Send patch request to backend to update account
     *
     * @param update the new account instance
     */
    public void update(Account update) {
        String endpoint = null;
        String accountString = null;

        if (!(update instanceof Investor) && !(update instanceof Startup)) {
            Log.e("AccountViewModel", "update: invalid account instance");
            return;
        }

        viewState.postValue(ViewState.LOADING);
        Log.d(TAG, "update: ViewState" + getViewState().getValue().toString());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Startup.class,
                Serializer.StartupUpdateSerializer);
        gsonBuilder.registerTypeAdapter(Investor.class,
                Serializer.InvestorUpdateSerializer);
        Gson gson = gsonBuilder.create();

        if (AuthenticationHandler.isStartup()) {
            endpoint = "startup/" + AuthenticationHandler.getId();
            accountString = gson.toJson((Startup) update);
        } else {
            endpoint = "investor/" + AuthenticationHandler.getId();
            accountString = gson.toJson((Investor) update);
        }

        try {
            ApiRequestHandler.performPatchRequest(endpoint,
                    v -> {
                        if (update instanceof Startup) {
                            currentAccount.postValue((Startup) update);
                        } else if (update instanceof Investor) {
                            currentAccount.postValue((Investor) update);
                        } else {
                            viewState.postValue(ViewState.ERROR);
                            Log.d(TAG, "update: ViewState" + getViewState().getValue().toString());
                            return null;
                        }
                        viewState.postValue(ViewState.UPDATED);
                        Log.d(TAG, "update: ViewState" + getViewState().getValue().toString());
                        return null;
                    }, error -> {
                        viewState.postValue(ViewState.ERROR);
                        return null;
                    }, new JSONObject(accountString));
        } catch (JSONException e) {
            viewState.postValue(ViewState.ERROR);
            Log.d(TAG, "update: ViewState" + getViewState().getValue().toString());
            Log.e("AccountViewModel", "Error while performing patch request: " +
                    e.getMessage());
        }
        Log.d(TAG, "patch request: " + accountString);
    }

    /**
     * Update the profile picture for the current account
     *
     * @param image
     */
    public void updateProfilePicture(Image image) {
        try {
            String method = "POST";
            String endpoint = "media/profilepicture";

            if(currentAccount.getValue().getProfilePictureId() != -1) {
                method = "PATCH";
                endpoint += "/" + currentAccount.getValue().getProfilePictureId();
            }
                new ImageUploader(image.getImage(),
                        currentAccount.getValue().getProfilePictureId(), response -> {
                    try {
                        if(response.has("profileResponse")) {
                            JSONObject profileResponse = response.getJSONObject("profileResponse");
                            currentAccount.getValue().setProfilePicture(image);
                            if(profileResponse.has("id")) {
                                currentAccount.getValue().setProfilePictureId(profileResponse.getLong("id"));
                                update(currentAccount.getValue());
                            }
                        } else {
                            viewState.postValue(ViewState.UPDATED);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "updateProfilePicture: " + e.getMessage());
                        viewState.postValue(ViewState.ERROR);
                    }
                    Log.d(TAG, "Successfully updated profile picture");

                    return null;
                }, error -> {
                    viewState.postValue(ViewState.ERROR);
                    Log.e(TAG, "updateProfilePicture: " + error.toString() );
                    return null;
                }).execute();
            } catch (Exception e) {
                viewState.postValue(ViewState.ERROR);
                Log.e(TAG, "Error while updating profile picture: " +
                        e.getMessage());
            }
    }

    /**
     * Update the gallery photos
     * @param gallery
     */
    public void updateGallery(List<Image> gallery) {
        List<Bitmap> pictures = new ArrayList<>();
        gallery.forEach(img -> {
            if(img.getId() == -1) {
                pictures.add(img.getImage());
            }
        });

        new ImageUploader(null, pictures,
                response -> {
            try {
                if(response.has("galleryResponse")) {
                    if(!response.isNull("galleryResponse")) {
                        JSONArray array = response.getJSONArray("galleryResponse");
                        for(int i = 0; i < array.length(); ++i) {
                            currentAccount.getValue().getGalleryIds().add(array.getLong(i));
                        }
                    }
                }

                viewState.postValue(ViewState.UPDATED);
            } catch (JSONException e) {
                Log.e(TAG, "updateGallery: " + e.getMessage());
                viewState.postValue(ViewState.ERROR);
            }
            Log.d(TAG, "Successfully updated gallery");
            return null;
        }, error -> {
            viewState.postValue(ViewState.ERROR);
            Log.e(TAG, "updateGallery: " + error.toString() );
            return null;
        }).execute();
    }
}