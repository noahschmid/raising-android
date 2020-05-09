package com.raising.app.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raising.app.models.ViewState;
import com.raising.app.util.InternalStorageHandler;

import java.util.ArrayList;

public class ViewStateViewModel extends AndroidViewModel {
    private MutableLiveData<ViewState> viewState = new MutableLiveData<ViewState>();
    private ArrayList<LiveData<ViewState>> viewModelStates = new ArrayList<>();

    public ViewStateViewModel(@NonNull Application application) {
        super(application);
        viewState.setValue(ViewState.EMPTY);
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    /**
     *
     * @param viewModelState
     * @param owner
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
            }
        });
    }

    /**
     * Check whether there's a viewmodel in the given state
     * @param state
     * @return
     */
    public boolean stateExists(ViewState state) {
        for(LiveData<ViewState> viewModelState : viewModelStates) {
            if(viewModelState.getValue() == state) {
                return true;
            }
        }
        return false;
    }
}
