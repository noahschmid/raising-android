package com.raising.app.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raising.app.R;
import com.raising.app.models.Continent;
import com.raising.app.models.CorporateBody;
import com.raising.app.models.Country;
import com.raising.app.models.FinanceType;
import com.raising.app.models.Industry;
import com.raising.app.models.InvestmentPhase;
import com.raising.app.models.InvestorType;
import com.raising.app.models.Label;
import com.raising.app.models.Revenue;
import com.raising.app.models.Support;
import com.raising.app.models.TicketSize;
import com.raising.app.models.ViewState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.Resources;
import com.raising.app.util.ResourcesDeserializer;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * View model managing all public resources
 */

public class ResourcesViewModel extends AndroidViewModel{
    private MutableLiveData<Resources> resources = new MutableLiveData<Resources>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<ViewState>();

    private final String TAG = "ResourcesViewModel";

    public ResourcesViewModel(@NonNull Application application) {
        super(application);
        viewState.setValue(ViewState.EMPTY);
    }

    /**
     * Load resources from backend
     */
    public void loadResources() {
        Resources cachedResources = getCachedResources();

        if(cachedResources != null) {
            resources.postValue(cachedResources);
            viewState.postValue(ViewState.CACHED);
        } else {
            viewState.postValue(ViewState.LOADING);
        }

        ApiRequestHandler.performGetRequest("public/", result -> {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(Resources.class, new ResourcesDeserializer());
                    Gson gson = gsonBuilder.create();
                    resources.postValue(gson.fromJson(result.toString(), Resources.class));
                    viewState.postValue(ViewState.RESULT);
                    Log.d(TAG, "loadResources: ViewState" + getViewState().getValue().toString());
                    Log.d(TAG, "loadResources: resources successfuly loaded");
                    return null;
                },
                error -> {
                    if(viewState.getValue() != ViewState.CACHED) {
                        if(error.networkResponse != null) {
                            if(error.networkResponse.statusCode == 403) {
                                viewState.postValue(ViewState.EXPIRED);
                            } else {
                                viewState.postValue(ViewState.ERROR);
                            }
                        } else {
                            viewState.postValue(ViewState.ERROR);
                        }
                        Log.d(TAG, "loadResources: ViewState" + getViewState().getValue().toString());
                    }
                    return null;
                });
    }

    /**
     * Load resources from internal storage if exists
     * @return Resources instance
     */
    private Resources getCachedResources() {
        try {
            if(InternalStorageHandler.exists("resources")) {
                return (Resources) InternalStorageHandler.loadObject("resources");
            }
        } catch (Exception e) {
            viewState.postValue(ViewState.ERROR);
            Log.d(TAG, "getCachedResources: ViewState" + getViewState().getValue().toString());
            Log.e(TAG, "Error while loading cached resources: " + e.getMessage());
        }

        return null;
    }

    /**
     * Save current account to internal storage
     */
    private void cacheResources() {
        try {
            InternalStorageHandler.saveObject(resources.getValue(),
                    "resources");
            Log.d(TAG, "cacheResources: loading cached resources successful");
        } catch(Exception e) {
            Log.e(TAG, "error caching resources: " + e.getMessage());
        }
    }

    public MutableLiveData<Resources> getResources() { return resources; }

    public MutableLiveData<ViewState> getViewState() { return viewState; }
}
