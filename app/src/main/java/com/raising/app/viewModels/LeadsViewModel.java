package com.raising.app.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.Lead;
import com.raising.app.models.ViewState;
import com.raising.app.models.leads.LeadState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.LeadsDeserializer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeadsViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Lead>> leads = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();

    private final String TAG = "LeadsViewModel";

    public LeadsViewModel(@NonNull Application application) {
        super(application);
        leads.setValue(new ArrayList<>());
        viewState.setValue(ViewState.EMPTY);
    }

    public LiveData<ArrayList<Lead>> getLeads() {
        return leads;
    }

    public ArrayList<Lead> getOpenRequests() {
        ArrayList<Lead> openRequests = new ArrayList<>();
        leads.getValue().forEach(lead -> {
            if(lead.getState() == LeadState.OPEN_REQUEST) {
                openRequests.add(lead);
            }
        });
        return openRequests;
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    public void loadLeads() {
        ArrayList<Lead> cachedLeads = getCachedLeads();

        if(cachedLeads != null) {
            leads.postValue(cachedLeads);
            viewState.postValue(ViewState.CACHED);
            Log.d(TAG, "loadLeads: ViewState " + getViewState().getValue().toString());
        } else {
            viewState.postValue(ViewState.LOADING);
            Log.d(TAG, "loadLeads: ViewState " + getViewState().getValue().toString());
        }

        ApiRequestHandler.performArrayGetRequest("interaction",
                result -> {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(Lead.class, new LeadsDeserializer());
                    Gson gson = gsonBuilder.create();
                    leads.postValue(gson.fromJson(result.toString(), new TypeToken<List<Lead>>() {}.getType()));
                    viewState.postValue(ViewState.RESULT);
                    cacheLeads();
                    return null;
                },
                error -> {
                    if(error.networkResponse != null) {
                        if(error.networkResponse.statusCode == 403) {
                            viewState.postValue(ViewState.EXPIRED);
                        } else {
                            viewState.postValue(ViewState.ERROR);
                        }
                    } else {
                        viewState.postValue(ViewState.ERROR);
                    }
                    Log.e(TAG, "loadLeads: " + ApiRequestHandler.parseVolleyError(error));
                    return null;
                });
    }

    /**
     * Load cached lead list from internal storage
     * @return Array list containing cached leads
     */
    private ArrayList<Lead> getCachedLeads() {
        try {
            if(InternalStorageHandler.exists("leads_" + AuthenticationHandler.getId())) {
                Log.d(TAG, "getCachedLeads: loaded cached leads");
                return (ArrayList<Lead>) InternalStorageHandler.loadObject(
                        "leads_" + AuthenticationHandler.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "getCachedLeads: Error while loading cached leads: " + e.getMessage());
        }
        return null;
    }

    /**
     * Save current leads to internal storage
     */
    private void cacheLeads() {
        try {
            InternalStorageHandler.saveObject(leads.getValue(),
                    "leads_" + AuthenticationHandler.getId());
        } catch(Exception e) {
            viewState.postValue(ViewState.ERROR);
            Log.e(TAG, "Error caching leads: " + e.getMessage());
        }
    }
}
