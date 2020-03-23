package com.raising.app.authentication.fragments.registration.helper.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raising.app.models.stakeholder.StakeholderShareholder;

public class ShareholderViewModel extends ViewModel {
    private final MutableLiveData<StakeholderShareholder> selectedShareholder
        = new MutableLiveData<>();

    public void select(StakeholderShareholder shareholder ) {
        selectedShareholder.setValue(shareholder);
    }

    public LiveData<StakeholderShareholder> getSelectedShareholder() {
        return selectedShareholder;
    }
}
