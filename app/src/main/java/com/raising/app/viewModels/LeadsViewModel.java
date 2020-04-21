package com.raising.app.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.Lead;
import com.raising.app.models.ViewState;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;

import java.util.ArrayList;

public class LeadsViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Lead>> leads = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();

    private final String TAG = "LeadsViewModel";

    public LeadsViewModel(@NonNull Application application) {
        super(application);
        leads.setValue(new ArrayList<>());
        viewState.setValue(ViewState.EMPTY);
        // loadLeads();
    }

    public LiveData<ArrayList<Lead>> getLeads() {
        return leads;
    }

    public ArrayList<Lead> getOpenRequests() {
        ArrayList<Lead> openRequests = new ArrayList<>();
        leads.getValue().forEach(lead -> {
            if((AuthenticationHandler.isStartup() && lead.getHandshakeState() == InteractionState.INVESTOR_ACCEPTED)
                || (!(AuthenticationHandler.isStartup()) && lead.getHandshakeState() == InteractionState.STARTUP_ACCEPTED)) {
                openRequests.add(lead);
            }
        });
        return openRequests;
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    public void loadLeads() {
        viewState.setValue(ViewState.LOADING);
        //TODO: implement method
    }

    /**
     * Load cached lead list from internal storage
     * @return Array list containing cached leads
     */
    private ArrayList<Lead> getCachedLeads() {
        viewState.setValue(ViewState.LOADING);
        try {
            if(InternalStorageHandler.exists("leads_" + AuthenticationHandler.getId())) {
                Log.d(TAG, "getCachedLeads: loaded cached leads");
                viewState.setValue(ViewState.CACHED);
                return (ArrayList<Lead>) InternalStorageHandler.loadObject(
                        "leads_" + AuthenticationHandler.getId());
            }
        } catch (Exception e) {
            viewState.setValue(ViewState.ERROR);
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
            viewState.setValue(ViewState.ERROR);
            Log.e(TAG, "Error caching leads: " + e.getMessage());
        }
    }
}
