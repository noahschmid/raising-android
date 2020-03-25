package com.raising.app.authentication.fragments.registration.helper.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raising.app.models.stakeholder.BoardMember;

public class BoardMemberViewModel extends AndroidViewModel {
    private final MutableLiveData<BoardMember> selectedBoardMember
            = new MutableLiveData<>();

    public BoardMemberViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Set a board member
     * @param boardMember The board member that is to be stored
     */
    public void select(BoardMember boardMember) {
        selectedBoardMember.setValue(boardMember);
    }

    /**
     * Retrieve the currently stored board member
     * @return The board member currently stored
     */
    public LiveData<BoardMember> getSelectedBoardMember() {
        return selectedBoardMember;
    }
}
