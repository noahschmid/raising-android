package com.raising.app.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raising.app.models.ViewState;

public class ViewStateViewModel extends AndroidViewModel {
    private MutableLiveData<ViewState> viewState = new MutableLiveData<ViewState>();

    public ViewStateViewModel(@NonNull Application application) {
        super(application);
        viewState.setValue(ViewState.EMPTY);
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    public void setViewState(ViewState state) {
        this.viewState.postValue(state);
    }
}
