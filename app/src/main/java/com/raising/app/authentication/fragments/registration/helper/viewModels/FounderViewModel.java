package com.raising.app.authentication.fragments.registration.helper.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raising.app.models.stakeholder.StakeholderFounder;

public class FounderViewModel extends ViewModel {
    private final MutableLiveData<StakeholderFounder> selectedFounder
            = new MutableLiveData<StakeholderFounder>();

    /**
     * Set a founder
     * @param founder The founder that is to be stored
     */
    public void select(StakeholderFounder founder ) {
        selectedFounder.setValue(founder);
    }

    /**
     * Retrieve the currently stored founder
     * @return The currently stored founder
     */
    public LiveData<StakeholderFounder> getSelectedFounder() {
        return selectedFounder;
    }
}
