package com.raising.app.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;

import com.raising.app.fragments.RaisingFragment;

import lombok.Getter;
import lombok.Setter;

/**
 * ViewModel which saves the navigation state of the individual tabs. If you switch from one tab
 * to another and back, you should land on the same fragment you left of. That's what this ViewModel
 * tries to manage. Thanks to lombok this ViewModel is rather small
 */

public class TabViewModel extends AndroidViewModel {
    @Getter
    @Setter
    private Fragment currentLeadsFragment;

    @Getter
    @Setter
    private Fragment currentProfileFragment;

    @Getter
    @Setter
    private Fragment currentSettingsFragment;

    @Getter
    @Setter
    private Fragment currentMatchesFragment;

    @Getter
    @Setter
    private Fragment currentRegistrationFragment;

    @Getter
    @Setter
    private int currentLeadsTab = 0;

    public TabViewModel(@NonNull Application application) {
        super(application);
    }

    public void resetCurrentLeadsFragment() { currentLeadsFragment = null; }
    public void resetCurrentProfileFragment() { currentProfileFragment = null; }
    public void resetCurrentMatchesFragment() { currentMatchesFragment = null; }
    public void resetCurrentSettingsFragment() { currentSettingsFragment = null; }
    public void resetCurrentRegistrationFragment() { currentRegistrationFragment = null; }

    /**
     * Reset all tabs
     */
    public void resetAll() {
        resetCurrentLeadsFragment();
        resetCurrentSettingsFragment();
        resetCurrentProfileFragment();
        resetCurrentMatchesFragment();
        resetCurrentRegistrationFragment();
    }
}
