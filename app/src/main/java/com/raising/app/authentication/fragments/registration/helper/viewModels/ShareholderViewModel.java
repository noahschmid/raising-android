package com.raising.app.authentication.fragments.registration.helper.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raising.app.models.stakeholder.Shareholder;

public class ShareholderViewModel extends ViewModel {
    private final MutableLiveData<Shareholder> selectedShareholder
        = new MutableLiveData<>();

    /**
     * Set a shareholder
     * @param shareholder The shareholder that is to be stored
     */
    public void select(Shareholder shareholder ) {
        selectedShareholder.setValue(shareholder);
    }

    /**
     * Retrieve the currently stored shareholder
     * @return The currently stored shareholder
     */
    public LiveData<Shareholder> getSelectedShareholder() {
        return selectedShareholder;
    }
}
