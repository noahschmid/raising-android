package com.raising.app.authentication.fragments.registration.helper.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raising.app.models.stakeholder.StakeholderBoardMember;

public class BoardMemberViewModel extends ViewModel {
    private final MutableLiveData<StakeholderBoardMember> selectedBoardMember
            = new MutableLiveData<>();

    /**
     * Set a board member
     * @param boardMember The board member that is to be stored
     */
    public void select(StakeholderBoardMember boardMember) {
        selectedBoardMember.setValue(boardMember);
    }

    /**
     * Retrieve the currently stored board member
     * @return The board member currently stored
     */
    public LiveData<StakeholderBoardMember> getSelectedBoardMember() {
        return selectedBoardMember;
    }
}
