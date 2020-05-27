package com.raising.app.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raising.app.models.ViewState;
import com.raising.app.util.InternalStorageHandler;

import java.util.ArrayList;

/**
 * This view model handles the global view state of the application. As long as a view model is in
 * the state LOADING, the app should be in the state LOADING, so that a loading view can be displayed.
 * If you need to show a loading screen outside of a view model, you can set loading = true using
 * startLoading()
 */

public class ViewStateViewModel extends AndroidViewModel {
    private MutableLiveData<ViewState> viewState = new MutableLiveData<ViewState>();
    private ArrayList<LiveData<ViewState>> viewModelStates = new ArrayList<>();
    private boolean loading = false;
    private static String TAG = "ViewStateViewModel";

    public ViewStateViewModel(@NonNull Application application) {
        super(application);
        viewState.setValue(ViewState.EMPTY);
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    public void setViewState(ViewState state) { this.viewState.postValue(state); }

    /**
     * Start custom loading (acts as yet another view model loading)
     */
    public void startLoading() {
        this.loading = true;
        viewState.postValue(ViewState.LOADING);
        Log.d(TAG, "startLoading");
    }

    /**
     * Stop custom loading
     */
    public void stopLoading() {
        this.loading = false;
        if(!stateExists(ViewState.LOADING)) {
            viewState.postValue(ViewState.RESULT);
        }

        Log.d(TAG, "stopLoading: " + viewState.getValue());
    }

    /**
     * Add a new view state to the list that gets observed. When the view state changes to LOADING,
     * change the global view state to LOADING. Only when no view model is in the view state LOADING
     * can the global view state be changed to RESULT.
     * @param viewModelState LiveData of the view state of the corresponding viewmodel
     * @param owner view model owner
     */
    public void addViewModel(LiveData<ViewState> viewModelState, LifecycleOwner owner) {
        viewModelStates.add(viewModelState);
        viewModelState.observe(owner, state -> {
            switch (state) {
                case RESULT:
                case CACHED:
                case UPDATED:
                    if(!stateExists(ViewState.LOADING)) {
                        viewState.postValue(ViewState.RESULT);
                    }
                    break;
                case LOADING:
                    viewState.postValue(ViewState.LOADING);
                    break;
                case ERROR:
                    viewState.postValue(ViewState.ERROR);
                    break;

                case EXPIRED:
                    viewState.postValue(ViewState.EXPIRED);
                    break;
            }
        });
    }

    /**
     * Check whether there's a viewmodel in the given state
     * @param state view state to search for in the list
     * @return whether or not a view model is in the given state
     */
    public boolean stateExists(ViewState state) {
        for(LiveData<ViewState> viewModelState : viewModelStates) {
            if(viewModelState.getValue() == state) {
                return true;
            }
        }

        if(state == ViewState.LOADING)
            return loading;
        return false;
    }
}
