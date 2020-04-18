package com.raising.app.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raising.app.models.Lead;
import com.raising.app.models.ViewState;

import java.util.ArrayList;

public class HandshakesViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Lead>> leads = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();

    private final String TAG = "HandshakesViewModel";


    public HandshakesViewModel(@NonNull Application application) {
        super(application);
        leads.setValue(new ArrayList<>());
        viewState.setValue(ViewState.EMPTY);
        loadLeads();
    }

    public LiveData<ArrayList<Lead>> getLeads() {
        return leads;
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    private void loadLeads() {
        viewState.setValue(ViewState.LOADING);
    }
}
