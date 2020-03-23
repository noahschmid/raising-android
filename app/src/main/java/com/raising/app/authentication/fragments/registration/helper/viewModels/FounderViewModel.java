package com.raising.app.authentication.fragments.registration.helper.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raising.app.models.stakeholder.StakeholderFounder;

public class FounderViewModel extends ViewModel {
    private final MutableLiveData<StakeholderFounder> selectedFounder
            = new MutableLiveData<StakeholderFounder>();

    public void select(StakeholderFounder founder ) {
        selectedFounder.setValue(founder);
    }

    public LiveData<StakeholderFounder> getSelectedFounder() {
        return selectedFounder;
    }
}
