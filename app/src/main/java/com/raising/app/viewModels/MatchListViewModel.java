package com.raising.app.viewModels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raising.app.models.Account;
import com.raising.app.models.MatchListItem;
import com.raising.app.models.ViewState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MatchListViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<MatchListItem>> matchList = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();

    private final String TAG = "MatchListViewModel";

    public MatchListViewModel(@NonNull Application application) {
        super(application);
        matchList.setValue(new ArrayList<>());
        loadMatches();
    }

    public MutableLiveData<ArrayList<MatchListItem>> getMatchList() { return matchList; }

    public void loadMatches() {
        ArrayList<MatchListItem> cachedMatchList = getCachedMatchList();
        if(cachedMatchList != null) {
            viewState.setValue(ViewState.CACHED);
            matchList.setValue(cachedMatchList);
        } else {
            viewState.setValue(ViewState.LOADING);
            matchList.setValue(new ArrayList<>());
        }

        ApiRequestHandler.performArrayGetRequest("matchList",
                response -> {
                    Gson gson = new Gson();
                    ArrayList<MatchListItem> matches = gson.fromJson(response.toString(),
                            new TypeToken<List<MatchListItem>>(){}.getType());
                    matchList.setValue(matches);
                    viewState.setValue(ViewState.RESULT);
                    cacheMatchList();
                    return null;
                },
                err -> {
                    viewState.setValue(ViewState.ERROR);
                    return null;
                });
    }

    /**
     * Load cached match list from internal storage
     * @return
     */
    private ArrayList<MatchListItem> getCachedMatchList() {
        try {
            if(InternalStorageHandler.exists("matches_" + AuthenticationHandler.getId())) {
                return (ArrayList<MatchListItem>) InternalStorageHandler.loadObject(
                        "matches_" + AuthenticationHandler.getId());
            }
        } catch (Exception e) {
            viewState.setValue(ViewState.ERROR);
            Log.e(TAG, "Error while loading cached matches: " + e.getMessage());
        }

        return null;
    }

    /**
     * Save current match list to internal storage
     */
    private void cacheMatchList() {
        if(AuthenticationHandler.isStartup()) {
            try {
                InternalStorageHandler.saveObject(matchList.getValue(),
                        "matches_" + AuthenticationHandler.getId());
            } catch(Exception e) {
                Log.e(TAG, "Error caching matches: " + e.getMessage());
            }
        } else {
            try {
                Gson gson = new Gson();
                InternalStorageHandler.saveObject(matchList.getValue(),
                        "matches_" + AuthenticationHandler.getId());

            } catch(Exception e) {
                Log.e(TAG, "Error caching matches: " + e.getMessage());
            }
        }
    }
}
