package com.raising.app.viewModels;

import android.app.Application;
import android.app.Person;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
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

public class SettingsViewModel extends AndroidViewModel {
    private final String TAG = "SettingsViewModel";

    private MutableLiveData<PersonalSettings> personalSettings = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        personalSettings.setValue(new PersonalSettings());
        viewState.setValue(ViewState.EMPTY);
    }

    public LiveData<PersonalSettings> getPersonalSettings() {
        return personalSettings;
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    public void loadSettings() {
        Log.d(TAG, "loadSettings: Loading Settings");
        viewState.setValue(ViewState.LOADING);
        Log.d(TAG, "loadSettings: ViewState " + viewState.getValue().toString());
        PersonalSettings cachedSettings = getCachedSettings();
        if (cachedSettings != null) {
            personalSettings.setValue(cachedSettings);
            viewState.setValue(ViewState.CACHED);
            Log.d(TAG, "loadSettings: ViewState " + viewState.getValue().toString());
        } else {
            // TODO: once server supports GET replace with GET request
            addInitialSettings();
            loadSettings();
        }

        //TODO: get personal settings from server
    }

    /**
     * Update all user settings locally, in cache and on the server
     *
     * @param settings The new object of personal settings
     */
    public void updatePersonalSettings(PersonalSettings settings) {
        viewState.setValue(ViewState.LOADING);
        personalSettings.setValue(settings);
        cacheSettings(personalSettings.getValue());

        // device specifications
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "updatePersonalSettings: DeviceToken: " + deviceToken);

        /*
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

            // object.put("language", personalSettings.getValue().getLanguage());
            // object.put("numberOfMatches", personalSettings.getValue().getNumberOfMatches());

            Log.d(TAG, "updatePersonalSettings: JSONObject" + object.toString());
        } catch (JSONException e) {
            Log.e(TAG, "updatePersonalSettings: JSONException" + e.getMessage());
        }

        ApiRequestHandler.performPatchRequest("device",
                response -> {
                    viewState.setValue(ViewState.RESULT);
                    return null;
                }, volleyError -> {
                    viewState.setValue(ViewState.ERROR);
                    Log.e(TAG, "updatePersonalSettings: " + ApiRequestHandler.parseVolleyError(volleyError));
                    return null;
                }, object);
        */

    }

    /**
     * Cache all the users personal settings in internal storage
     *
     * @param settings The settings that are to be cached
     */
    private void cacheSettings(PersonalSettings settings) {
        try {
            InternalStorageHandler.saveObject(settings,
                    "settings_" + AuthenticationHandler.getId());
        } catch (Exception e) {
            viewState.setValue(ViewState.ERROR);
            Log.e(TAG, "Error caching settings: " + e.getMessage());
        }
    }


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

        initialSettings.setLanguage("English");
        initialSettings.setNumberOfMatches(5);

        ArrayList<NotificationSettings> notificationSettings = new ArrayList<>();
        notificationSettings.add(NotificationSettings.MATCHLIST);
        notificationSettings.add(NotificationSettings.LEAD);
        notificationSettings.add(NotificationSettings.REQUEST);
        notificationSettings.add(NotificationSettings.CONNECTION);
        initialSettings.setNotificationSettings(notificationSettings);

        //TODO: remove and replace with updatePersonalSettings(initialSettings); once backend supports all requests via PATCH

        personalSettings.setValue(initialSettings);
        cacheSettings(personalSettings.getValue());

        /*
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

            // object.put("language", personalSettings.getValue().getLanguage());
            // object.put("numberOfMatches", personalSettings.getValue().getNumberOfMatches());

            Log.d(TAG, "addInitialSettings: JSONObject" + object.toString());
        } catch (JSONException e) {
            Log.e(TAG, "addInitialSettings: JSONException" + e.getMessage());
        }

        ApiRequestHandler.performPostRequest("device",
                response -> {
                    viewState.setValue(ViewState.RESULT);
                    Log.d(TAG, "addInitialSettings: ViewState " + viewState.getValue().toString() );
                    return null;
                }, volleyError -> {
                    viewState.setValue(ViewState.ERROR);
                    Log.d(TAG, "addInitialSettings: ViewState " + viewState.getValue().toString());
                    Log.e(TAG, "addInitialSettings: " + ApiRequestHandler.parseVolleyError(volleyError));
                    return null;
                }, object);

         */
    }
}
