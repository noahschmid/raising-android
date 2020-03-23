package com.raising.app.authentication.fragments.registration.helper.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raising.app.models.stakeholder.Founder;

public class FounderViewModel extends ViewModel {
    private final MutableLiveData<Founder> selectedFounder
            = new MutableLiveData<Founder>();

    public void select(Founder founder ) {
        selectedFounder.setValue(founder);
    }

    public LiveData<Founder> getSelectedFounder() {
        return selectedFounder;
    }
}
