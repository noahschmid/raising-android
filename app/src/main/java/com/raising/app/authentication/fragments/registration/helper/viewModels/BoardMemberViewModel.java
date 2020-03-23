package com.raising.app.authentication.fragments.registration.helper.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.raising.app.models.stakeholder.BoardMember;

public class BoardMemberViewModel extends ViewModel {
    private final MutableLiveData<BoardMember> selectedBoardMember
            = new MutableLiveData<>();

    public void select(BoardMember boardMember) {
        selectedBoardMember.setValue(boardMember);
    }

    public LiveData<BoardMember> getSelectedBoardMember() {
        return selectedBoardMember;
    }
}
