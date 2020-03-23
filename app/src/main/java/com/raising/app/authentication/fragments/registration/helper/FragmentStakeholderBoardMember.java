package com.raising.app.authentication.fragments.registration.helper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.authentication.fragments.registration.helper.viewModels.BoardMemberViewModel;
import com.raising.app.authentication.fragments.registration.helper.viewModels.FounderViewModel;
import com.raising.app.models.stakeholder.StakeholderBoardMember;

public class FragmentStakeholderBoardMember extends RaisingFragment {
    private BoardMemberViewModel boardMemberViewModel;

    private EditText boardFirstNameInput, boardLastNameInput, boardProfessionInput, boardEducationInput;
    private AutoCompleteTextView boardPositionInput, memberSinceInput;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stakeholder_board_member,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boardMemberViewModel = new ViewModelProvider(requireActivity()).get(BoardMemberViewModel.class);

        //TODO: fetch these values from the backend
        String [] VALUES_YEARS = new String[] {"2000", "1990", "1980", "1970"};
        String [] VALUES_POSITIONS = new String[] {"CEO", "CFO", "VRP" };

        ArrayAdapter adapterPosition = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_POSITIONS);

        ArrayAdapter adapterYear = new ArrayAdapter<>( getContext(),
                R.layout.item_dropdown_menu, VALUES_YEARS);

        boardFirstNameInput = view.findViewById(R.id.input_board_member_first_name);
        boardLastNameInput = view.findViewById(R.id.input_board_member_last_name);
        boardProfessionInput = view.findViewById(R.id.input_board_member_profession);
        boardEducationInput = view.findViewById(R.id.input_board_member_education);

        boardPositionInput = view.findViewById(R.id.input_board_member_position);
        boardPositionInput.setAdapter(adapterPosition);

        memberSinceInput = view.findViewById(R.id.input_board_member_member_since);
        memberSinceInput.setAdapter(adapterYear);

        /**
         * Sets the current values, if the user wants to edit a BoardMember
         */
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            boardFirstNameInput.setText(bundle.getString("firstName"));
            boardLastNameInput.setText(bundle.getString("lastName"));
            boardProfessionInput.setText(bundle.getString("profession"));
            boardPositionInput.setText(bundle.getString("position"));
            memberSinceInput.setText(bundle.getInt("memberSince"));
            boardEducationInput.setText(bundle.getString("education"));
        }

        Button btnCancelBoardMember = view.findViewById(R.id.button_cancel_board_member);
        btnCancelBoardMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveBoardMemberFragment();
            }
        });
        Button btnAddBoardMember = view.findViewById(R.id.button_add_board_member);
        btnAddBoardMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = boardFirstNameInput.getText().toString();
                String lastName = boardLastNameInput.getText().toString();
                String profession = boardProfessionInput.getText().toString();
                String boardPosition = boardPositionInput.getText().toString();
                String memberSince = memberSinceInput.getText().toString();
                String education = boardEducationInput.getText().toString();

                if(firstName.length() == 0 || lastName.length() == 0
                        || profession.length() == 0 || boardPosition.length() == 0
                        || memberSince.length() == 0 || education.length() == 0) {
                    showSimpleDialog(getString(R.string.register_dialog_title),
                            getString(R.string.register_dialog_text_empty_credentials));
                    return;
                }
                StakeholderBoardMember boardMember = new StakeholderBoardMember(
                        firstName, lastName, profession, boardPosition, memberSince, education);

                boardMemberViewModel.select(boardMember);
                leaveBoardMemberFragment();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    /**
     * {@link com.raising.app.RaisingFragment#popCurrentFragment(androidx.fragment.app.Fragment)}
     */
    private void leaveBoardMemberFragment() {
        popCurrentFragment(this);
    }
}
