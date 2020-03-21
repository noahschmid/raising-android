package com.raising.app.authentication.fragments.registration.helper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.raising.app.R;
import com.raising.app.RaisingFragment;

public class FragmentStakeholderBoardMember extends RaisingFragment implements View.OnClickListener {
    private EditText boardFirstNameInput, boardLastNameInput, boardProfessionInput, boardEducationInput;
    private AutoCompleteTextView boardPositionInput, boardSinceInput;


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

        boardSinceInput = view.findViewById(R.id.input_board_member_member_since);
        boardSinceInput.setAdapter(adapterYear);

        Button btnCancelBoardMember = view.findViewById(R.id.button_cancel_board_member);
        btnCancelBoardMember.setOnClickListener(this);
        Button btnAddBoardMember = view.findViewById(R.id.button_add_board_member);
        btnAddBoardMember.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (getId()) {
            case R.id.button_cancel_board_member:
                //TODO: insert method
                break;
            case R.id.button_add_board_member:
                //TODO: insert method
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }
}
