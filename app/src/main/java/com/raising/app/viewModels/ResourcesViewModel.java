package com.raising.app.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.Resources;

import java.util.ArrayList;

public class ResourcesViewModel extends AndroidViewModel{
    private MutableLiveData<Resources> resources = new MutableLiveData<Resources>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<ViewState>();

    private final String TAG = "ResourcesViewModel";

    public ResourcesViewModel(@NonNull Application application) {
        super(application);
        Resources cachedResources = getCachedResources();

        if(cachedResources != null) {
            resources.setValue(cachedResources);
            viewState.setValue(ViewState.CACHED);
        } else {
            viewState.setValue(ViewState.LOADING);
        }

        ApiRequestHandler.performArrayGetRequest("public", result -> {
            return null;
                },
                error -> {
            if(viewState.getValue() != ViewState.CACHED) {
                viewState.setValue(ViewState.ERROR);
            }
            return null;
                });
    }

    private Resources getCachedResources() {
        try {
            if(InternalStorageHandler.exists("resources")) {
                return (Resources) InternalStorageHandler.loadObject("resources");
            }
        } catch (Exception e) {
            viewState.setValue(ViewState.ERROR);
            Log.e(TAG, "Error while loading cached resources: " + e.getMessage());
        }

        return null;
    }

    public MutableLiveData<Resources> getResources() { return resources; }

    public MutableLiveData<ViewState> getViewState() { return viewState; }
}
