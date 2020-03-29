package com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raising.app.models.stakeholder.Shareholder;

public class ShareholderViewModel extends AndroidViewModel {
    private final MutableLiveData<Shareholder> selectedShareholder
        = new MutableLiveData<>();

    public ShareholderViewModel(@NonNull Application application) {
        super(application);
    }

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
