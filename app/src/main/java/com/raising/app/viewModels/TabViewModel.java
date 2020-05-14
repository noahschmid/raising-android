package com.raising.app.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;

import com.raising.app.fragments.RaisingFragment;

import lombok.Getter;
import lombok.Setter;

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
}
