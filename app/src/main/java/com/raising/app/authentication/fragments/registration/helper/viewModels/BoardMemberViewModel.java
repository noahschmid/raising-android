package com.raising.app.authentication.fragments.registration.helper.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raising.app.models.stakeholder.StakeholderBoardMember;

public class BoardMemberViewModel extends ViewModel {
    private final MutableLiveData<StakeholderBoardMember> selectedBoardMember
            = new MutableLiveData<>();

    public void select(StakeholderBoardMember boardMember) {
        selectedBoardMember.setValue(boardMember);
    }

    public LiveData<StakeholderBoardMember> getSelectedBoardMember() {
        return selectedBoardMember;
    }
}
