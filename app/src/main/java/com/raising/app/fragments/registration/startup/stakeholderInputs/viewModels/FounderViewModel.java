package com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raising.app.models.stakeholder.Founder;

public class FounderViewModel extends AndroidViewModel {
    private MutableLiveData<Founder> selectedFounder
            = new MutableLiveData<Founder>();

    public FounderViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Set a founder
     * @param founder The founder that is to be stored
     */
    public void select(Founder founder ) {
        selectedFounder.setValue(founder);
    }

    /**
     * Retrieve the currently stored founder
     * @return The currently stored founder
     */
    public LiveData<Founder> getSelectedFounder() {
        return selectedFounder;
    }

    /**
     * Deselect the currently selected founder before every fragment change
     */
    public void deselectFounder() {
        selectedFounder = new MutableLiveData<>();
    }
}
