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
import com.google.gson.reflect.TypeToken;
import com.raising.app.models.Account;
import com.raising.app.models.Match;
import com.raising.app.models.Match;
import com.raising.app.models.ViewState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.MatchesDeserializer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchesViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Match>> matches = new MutableLiveData<>();
    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();

    private final String TAG = "MatchesViewModel";

    public MatchesViewModel(@NonNull Application application) {
        super(application);
        matches.setValue(new ArrayList<>());
        viewState.setValue(ViewState.EMPTY);
    }

    public LiveData<ArrayList<Match>> getMatches() { return matches; }

    public LiveData<ViewState> getViewState() { return viewState; }

    public void removeMatch(Long id) {
        for(int i = 0; i < matches.getValue().size(); ++i) {
            if(id == matches.getValue().get(i).getId()) {
                matches.getValue().remove(i);
            }
        }
    }

    public void runMatching() {
        ApiRequestHandler.performPostRequest("match/run",
                response -> {
                    loadMatches();
                    return null;
                },
                error -> {
                    Log.e(TAG, "runMatching: " + ApiRequestHandler.parseVolleyError(error));

                    Log.e(TAG, "runMatching: " + error.networkResponse.statusCode );
                    if(error.networkResponse != null) {
                        if(error.networkResponse.statusCode == 403) {
                            viewState.postValue(ViewState.EXPIRED);
                        } else {
                            viewState.postValue(ViewState.ERROR);
                        }
                    } else {
                        viewState.postValue(ViewState.ERROR);
                    }
                    return null;
                }, new JSONObject());
    }

    public void loadMatches() {
        ArrayList<Match> cachedMatchList = getCachedMatches();
        if(cachedMatchList != null) {
            viewState.postValue(ViewState.CACHED);
            matches.postValue(cachedMatchList);
        } else {
            viewState.postValue(ViewState.LOADING);
            matches.postValue(new ArrayList<>());
        }

        ApiRequestHandler.performArrayGetRequest("match",
                response -> {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(Match.class, new MatchesDeserializer());
                    Gson gson = gsonBuilder.create();
                    ArrayList<Match> matchList = gson.fromJson(response.toString(),
                            new TypeToken<List<Match>>(){}.getType());
                    Collections.sort(matchList, new Comparator<Match>() {
                        @Override
                        public int compare(Match lhs, Match rhs) {
                            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                            return lhs.getMatchingPercent() > rhs.getMatchingPercent() ?
                                    -1 : (lhs.getMatchingPercent() < rhs.getMatchingPercent() ) ? 1 : 0;
                        }
                    });
                    matches.postValue(matchList);
                    Log.d(TAG, "loadMatches: fetched " + matchList.size() + " new matches");
                    viewState.postValue(ViewState.RESULT);
                    cacheMatches();
                    return null;
                },
                err -> {
                    if(err.networkResponse != null) {
                        if(err.networkResponse.statusCode == 403) {
                            viewState.postValue(ViewState.EXPIRED);
                        } else {
                            viewState.postValue(ViewState.ERROR);
                        }
                    } else {
                        viewState.postValue(ViewState.ERROR);
                    }
                    return null;
                });
    }

    /**
     * Load cached match list from internal storage
     * @return
     */
    private ArrayList<Match> getCachedMatches() {
        try {
            if(InternalStorageHandler.exists("matches_" + AuthenticationHandler.getId())) {
                Log.d(TAG, "getCachedMatches: loaded cached matches");
                return (ArrayList<Match>) InternalStorageHandler.loadObject(
                        "matches_" + AuthenticationHandler.getId());
            }
        } catch (Exception e) {
            viewState.postValue(ViewState.ERROR);
            Log.e(TAG, "Error while loading cached matches: " + e.getMessage());
        }

        return null;
    }

    /**
     * Save current matches to internal storage
     */
    private void cacheMatches() {
        if(AuthenticationHandler.isStartup()) {
            try {
                InternalStorageHandler.saveObject(matches.getValue(),
                        "matches_" + AuthenticationHandler.getId());
            } catch(Exception e) {
                Log.e(TAG, "Error caching matches: " + e.getMessage());
            }
        } else {
            try {
                Gson gson = new Gson();
                InternalStorageHandler.saveObject(matches.getValue(),
                        "matches_" + AuthenticationHandler.getId());

            } catch(Exception e) {
                Log.e(TAG, "Error caching matches: " + e.getMessage());
            }
        }
    }
}
