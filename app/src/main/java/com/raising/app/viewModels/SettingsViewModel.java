package com.raising.app.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.iid.FirebaseInstanceId;
import com.raising.app.models.NotificationSettings;
import com.raising.app.models.PersonalSettings;
import com.raising.app.models.ViewState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Callable;

public class SettingsViewModel extends AndroidViewModel {
    private final String TAG = "SettingsViewModel";

    private MutableLiveData<PersonalSettings> personalSettings = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        personalSettings.setValue(new PersonalSettings());
        Objects.requireNonNull(personalSettings.getValue()).setNotificationSettings(new ArrayList<>());
        viewState.setValue(ViewState.EMPTY);
    }

    public LiveData<PersonalSettings> getPersonalSettings() {
        return personalSettings;
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    /**
     * Load settings from backend
     */
    public void loadSettings() {
        updateDeviceToken();
        viewState.setValue(ViewState.LOADING);
        PersonalSettings cachedSettings = getCachedSettings();
        personalSettings.setValue(cachedSettings);
        if (cachedSettings != null) {
            Log.d(TAG, "loadSettings: Loaded Cached Settings " + cachedSettings);
            personalSettings.setValue(cachedSettings);
            viewState.setValue(ViewState.CACHED);
        }
        getUserSettings();
    }

    /**
     * Update the device token using a patch request to the server
     */
    private void updateDeviceToken() {
        Log.d(TAG, "updateDeviceToken: ");
        viewState.postValue(ViewState.LOADING);
        JSONObject object = new JSONObject();
        // device specifications
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "sendDeviceToken: DeviceToken " + deviceToken);

        try {
            object.put("token", deviceToken);
            object.put("device", "ANDROID");
        } catch (JSONException e) {
            Log.e(TAG, "sendDeviceToken: JSONException " + e.getMessage());
        }

        Log.d(TAG, "updateDeviceToken: " + object);
        ApiRequestHandler.performPatchRequest("settings",
                response -> {
                    viewState.postValue(ViewState.RESULT);
                    Log.d(TAG, "sendDeviceToken: Token updated " + deviceToken);
                    return null;
                }, error -> {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 403) {
                            viewState.postValue(ViewState.EXPIRED);
                        } else {
                            viewState.postValue(ViewState.ERROR);
                        }
                    } else {
                        viewState.postValue(ViewState.ERROR);
                    }
                    Log.e(TAG, "sendDeviceToken: Update failed " + error.getMessage());
                    return null;
                }, object);
    }

    /**
     * Get currently saved user settings from backend
     */
    private void getUserSettings() {
        ApiRequestHandler.performGetRequest("settings",
                response -> {
                    viewState.postValue(ViewState.RESULT);
                    try {
                        personalSettings.setValue(new PersonalSettings());
                        personalSettings.getValue().setNotificationSettings(new ArrayList<>());
                        personalSettings.getValue().setNumberOfMatches(response.getInt("numberOfMatches"));
                        JSONArray notificationSettings = response.getJSONArray("notificationTypes");
                        Log.d(TAG, "getUserSettings: " + notificationSettings);
                        for (int i = 0; i < notificationSettings.length(); i++) {
                            personalSettings.getValue().getNotificationSettings().add(Enum.valueOf(NotificationSettings.class, notificationSettings.getString(i)));
                        }
                        Log.d(TAG, "getUserSettings: Personal Settings" + personalSettings.getValue());
                        cacheSettings(personalSettings.getValue());
                    } catch (Exception e) {
                        Log.e(TAG, "getUserSettings: Error getting JSON" + e.getMessage());
                    }
                    return null;
                }, volleyError -> {
                    if (viewState.getValue() != ViewState.CACHED) {
                        viewState.postValue(ViewState.ERROR);
                    }
                    Log.d(TAG, "getUserSettings: Error fetching settings" + volleyError.toString());
                    return null;
                });
    }

    /**
     * Update all user settings locally, in cache and on the server
     * @param settings The new object of personal settings
     */
    public void updatePersonalSettings(PersonalSettings settings) {
        viewState.postValue(ViewState.LOADING);
        personalSettings.postValue(settings);
        cacheSettings(personalSettings.getValue());

        // device specifications
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "updatePersonalSettings: DeviceToken: " + deviceToken);

        JSONObject object = new JSONObject();
        ArrayList<String> notificationSettingsStrings = new ArrayList<>();
        personalSettings.getValue().getNotificationSettings().forEach(notificationSettings -> {
            notificationSettingsStrings.add(notificationSettings.name());
        });
        try {
            object.put("notificationTypes", new JSONArray(notificationSettingsStrings.toArray()));
            object.put("token", deviceToken);
            object.put("device", "ANDROID");

            //TODO: add following fields, once backend supports these values

            object.put("numberOfMatches", personalSettings.getValue().getNumberOfMatches());

            Log.d(TAG, "updatePersonalSettings: JSONObject" + object.toString());
        } catch (JSONException e) {
            Log.e(TAG, "updatePersonalSettings: JSONException" + e.getMessage());
        }

        ApiRequestHandler.performPatchRequest("settings",
                response -> {
                    viewState.postValue(ViewState.RESULT);
                    return null;
                }, volleyError -> {
                    viewState.postValue(ViewState.ERROR);
                    Log.e(TAG, "updatePersonalSettings: " + ApiRequestHandler.parseVolleyError(volleyError));
                    return null;
                }, object);
    }

    /**
     * Cache all the users personal settings in internal storage
     * @param settings The settings that are to be cached
     */
    private void cacheSettings(PersonalSettings settings) {
        try {
            InternalStorageHandler.saveObject(settings,
                    "settings_" + AuthenticationHandler.getId());
        } catch (Exception e) {
            viewState.postValue(ViewState.ERROR);
            Log.e(TAG, "Error caching settings: " + e.getMessage());
        }
    }

    /**
     * Get cached settings from internal storage
     * @return PersonalSettings instance loaded from internal storage
     */
    private PersonalSettings getCachedSettings() {
        try {
            if (InternalStorageHandler.exists("settings_" + AuthenticationHandler.getId())) {
                Log.d(TAG, "getCachedSettings: loaded cached settings");
                return (PersonalSettings) InternalStorageHandler.loadObject(
                        "settings_" + AuthenticationHandler.getId());
            } else {

                Log.d(TAG, "getCachedSettings: No cached settings available");
            }
        } catch (Exception e) {
            viewState.setValue(ViewState.ERROR);
            Log.e(TAG, "getCachedSettings: Error while getting cached settings" + e.getMessage());
            addInitialSettings();
        }
        return null;
    }

    /**
     * Add initial settings for new users
     */
    public void addInitialSettings() {
        viewState.setValue(ViewState.LOADING);
        Log.d(TAG, "addInitialSettings: ViewState " + viewState.getValue().toString());
        PersonalSettings initialSettings = new PersonalSettings();

        initialSettings.setNumberOfMatches(5);

        ArrayList<NotificationSettings> notificationSettings = new ArrayList<>();
        notificationSettings.add(NotificationSettings.MATCHLIST);
        notificationSettings.add(NotificationSettings.LEAD);
        notificationSettings.add(NotificationSettings.REQUEST);
        notificationSettings.add(NotificationSettings.CONNECTION);
        initialSettings.setNotificationSettings(notificationSettings);

        //TODO: remove and replace with updatePersonalSettings(initialSettings); once backend supports all requests via PATCH

        personalSettings.postValue(initialSettings);
        cacheSettings(personalSettings.getValue());

        // device specifications
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "addInitialSettings: DeviceToken: " + deviceToken);

        JSONObject object = new JSONObject();
        ArrayList<String> notificationSettingsStrings = new ArrayList<>();
        personalSettings.getValue().getNotificationSettings().forEach(settings -> {
            notificationSettingsStrings.add(settings.name());
        });
        try {
            object.put("notificationTypes", new JSONArray(notificationSettingsStrings.toArray()));
            object.put("token", deviceToken);
            object.put("device", "ANDROID");

            //TODO: add following fields, once backend supports these values

            object.put("numberOfMatches", personalSettings.getValue().getNumberOfMatches());

            Log.d(TAG, "addInitialSettings: JSONObject" + object.toString());
        } catch (JSONException e) {
            Log.e(TAG, "addInitialSettings: JSONException" + e.getMessage());
        }

        ApiRequestHandler.performPatchRequest("settings",
                response -> {
                    viewState.postValue(ViewState.RESULT);
                    Log.d(TAG, "addInitialSettings: ViewState " + viewState.getValue().toString());
                    return null;
                }, volleyError -> {
                    viewState.postValue(ViewState.ERROR);
                    Log.d(TAG, "addInitialSettings: ViewState " + viewState.getValue().toString());
                    Log.e(TAG, "addInitialSettings: " + ApiRequestHandler.parseVolleyError(volleyError));
                    return null;
                }, object);
    }

    /**
     * Call the backend to inform, that user has logged out and the device token should be deleted.
     * This prevents, that a user who is not logged in, does not receive any push notifications.
     */
    public void onLogoutResetToken(Callable<Void> callable) {
        ApiRequestHandler.performPatchRequest("settings/deletetoken",
                response -> {
                    Log.d(TAG, "onLogoutResetToken: Token Reset");
                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }, volleyError -> {
                    Log.e(TAG, "onLogoutResetToken: " + volleyError.getMessage());
                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }, new JSONObject());
    }
}
