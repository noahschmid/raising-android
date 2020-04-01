package com.raising.app.fragments.registration.startup.stakeholderInputs;

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
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.BoardMemberViewModel;
import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.ResourcesManager;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;

import java.util.ArrayList;
import java.util.Arrays;

public class BoardMemberInputFragment extends RaisingFragment {
    private BoardMemberViewModel boardMemberViewModel;
    private EditText boardFirstNameInput, boardLastNameInput, boardProfessionInput,
            boardEducationInput, memberSinceInput, countryInput;
    private AutoCompleteTextView boardPositionInput;
    private BoardMember boardMember;
    private CustomPicker countryPicker;
    private int countryId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stakeholder_board_member,
                container, false);

        hideBottomNavigation(true);

        return view;
    }

    public void passBoardMember(BoardMember member) {
        boardMember = member;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boardMemberViewModel = new ViewModelProvider(requireActivity()).get(BoardMemberViewModel.class);

        //TODO: fetch these values from the backend
        ArrayList<String> positions = new ArrayList<String>(Arrays.asList(new String[]{"CEO", "CFO", "VRP"}));

        NoFilterArrayAdapter<String> adapterPosition = new NoFilterArrayAdapter<String>( getContext(),
                R.layout.item_dropdown_menu, positions);

        boardFirstNameInput = view.findViewById(R.id.input_board_member_first_name);
        boardLastNameInput = view.findViewById(R.id.input_board_member_last_name);
        boardProfessionInput = view.findViewById(R.id.input_board_member_profession);
        boardEducationInput = view.findViewById(R.id.input_board_member_education);

        boardPositionInput = view.findViewById(R.id.input_board_member_position);
        boardPositionInput.setAdapter(adapterPosition);

        memberSinceInput = view.findViewById(R.id.input_board_member_member_since);
        countryInput = view.findViewById(R.id.input_board_member_country);
        memberSinceInput.setShowSoftInputOnFocus(false);
        countryInput.setShowSoftInputOnFocus(false);

        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .listener(new OnCustomPickerListener() {
                            @Override
                            public void onSelectItem(PickerItem country) {
                                countryInput.setText(country.getName());
                                countryId = (int)country.getId();
                            }
                        })
                        .setItems(ResourcesManager.getCountries());

        countryPicker = builder.build();

        countryInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    countryPicker.showDialog(getActivity());
            }
        });

        if(boardMember == null) {
            boardMember = new BoardMember();
        } else {
            boardFirstNameInput.setText(boardMember.getFirstName());
            boardLastNameInput.setText(boardMember.getLastName());
            boardProfessionInput.setText(boardMember.getProfession());
            boardPositionInput.setText(boardMember.getBoardPosition());
            memberSinceInput.setText(String.valueOf(boardMember.getMemberSince()));
            boardEducationInput.setText(boardMember.getEducation());
            if(boardMember.getCountryId() != -1)
                countryInput.setText(ResourcesManager.getCountry(boardMember.getCountryId()).getName());
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
                        || memberSince.length() == 0 || countryId == -1) {
                    showSimpleDialog(getString(R.string.register_dialog_title),
                            getString(R.string.register_dialog_text_empty_credentials));
                    return;
                }
                BoardMember boardMember = new BoardMember(
                        firstName, lastName, profession, boardPosition,
                        memberSince, education, countryId);

                boardMemberViewModel.select(boardMember);
                leaveBoardMemberFragment();
            }
        });

        memberSinceInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showYearPicker("Select year", memberSinceInput);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    /**
     * {@link RaisingFragment#popCurrentFragment(androidx.fragment.app.Fragment)}
     */
    private void leaveBoardMemberFragment() {
        popCurrentFragment(this);
    }
}
