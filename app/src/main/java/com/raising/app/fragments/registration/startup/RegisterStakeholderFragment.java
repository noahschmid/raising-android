package com.raising.app.fragments.registration.startup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raising.app.R;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.BoardMemberInputFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.FounderInputFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.ShareholderInputFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.BoardMemberViewModel;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.FounderViewModel;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.ShareholderViewModel;
import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.models.stakeholder.StakeholderItem;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.recyclerViewAdapter.StakeholderRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.Function;

public class RegisterStakeholderFragment extends RaisingFragment implements View.OnClickListener {
    private FounderViewModel founderViewModel;
    private BoardMemberViewModel boardMemberViewModel;
    private ShareholderViewModel shareholderViewModel;
    Button finishButton;

    boolean EditMode = false;
    int editedIndex;

    // hold references to the respective recycler views
    private RecyclerView founderRecyclerView, boardMemberRecyclerView, shareholderRecyclerView;

    // hold references to the respective recycler view adapters
    private StakeholderRecyclerViewAdapter founderAdapter, boardMemberAdapter, shareholderAdapter;

    // the lists, that are displayed in the respective recycler views
    private ArrayList<StakeholderItem> founderList,
            boardMemberList, shareholderList;

    public RegisterStakeholderFragment() {
        setArguments(new Bundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_stakeholder,
                container, false);


        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments().getString("founderList") == null
                && getArguments().getString("boardMemberList") == null
                && getArguments().getString("shareholderList") == null) {
            founderList = new ArrayList<>();
            boardMemberList = new ArrayList<>();
            shareholderList = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            Type founderListType = new TypeToken<ArrayList<Founder>>() {
            }.getType();
            founderList = gson.fromJson(getArguments().getString("founderList"), founderListType);

            Type boardMemberListType = new TypeToken<ArrayList<BoardMember>>() {
            }.getType();
            boardMemberList = gson.fromJson(getArguments().getString("boardMemberList"), boardMemberListType);

            Type shareholderListType = new TypeToken<ArrayList<Shareholder>>() {
            }.getType();
            shareholderList = gson.fromJson(getArguments().getString("shareholderList"), shareholderListType);
        }

        // initialize the different recycler views
        setupFounderRecyclerView(view);
        setupBoardMemberRecyclerView(view);
        setupShareholderRecyclerView(view);

        Button btnAddFounder = view.findViewById(R.id.floating_button_add_founder);
        btnAddFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new FounderInputFragment());
            }
        });

        Button btnAddBoardMember
                = view.findViewById(R.id.floating_button_add_board_member);
        btnAddBoardMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new BoardMemberInputFragment());
            }
        });

        Button btnAddShareholder
                = view.findViewById(R.id.floating_button_add_shareholder);
        btnAddShareholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new ShareholderInputFragment());
            }
        });

        Button btnFinishRegistration = view.findViewById(R.id.button_stakeholder);
        btnFinishRegistration.setOnClickListener(this);

        if(this.getArguments() != null && this.getArguments().getBoolean("isProfileFragment")) {
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.GONE);
            btnFinishRegistration.setHint(getString(R.string.myProfile_apply_changes));
            btnFinishRegistration.setOnClickListener(v -> popCurrentFragment(this));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Gson gson = new Gson();
        String json = gson.toJson(founderList.toArray());
        getArguments().putString("founderList", json);
        founderViewModel.deselectFounder();

        json = gson.toJson(boardMemberList.toArray());
        getArguments().putString("boardMemberList", json);
        boardMemberViewModel.deselectBoardMember();

        json = gson.toJson(shareholderList.toArray());
        getArguments().putString("shareholderList", json);
        shareholderViewModel.deselectShareholder();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();
        outState.clear();
        String json = gson.toJson(founderList.toArray());
        outState.putString("founderList", json);

        json = gson.toJson(boardMemberList.toArray());
        outState.putString("boardMemberList", json);

        json = gson.toJson(shareholderList.toArray());
        outState.putString("shareholderList", json);
    }

    /**
     * Delegate creating of recycler view and create observer for FounderViewModel
     *
     * @param view The view containing the recycler view
     */
    private void setupFounderRecyclerView(View view) {
        createFounderRecyclerView(view);
        founderViewModel = new ViewModelProvider(requireActivity()).get(FounderViewModel.class);
        founderViewModel.getSelectedFounder().observe(getViewLifecycleOwner(),
                founder -> {
                    founder.updateTitle();
                    if(getArguments().getBoolean("editFounder")) {
                        int position = getArguments().getInt("editIndex");
                        founderList.set(position, founder);
                        founderAdapter.notifyItemChanged(position);
                        getArguments().putBoolean("editFounder", false);
                    }

                    if(!founderList.contains(founder)) {
                        founderList.add(founder);
                        founderAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * Create the recycler view for the founders by linking the UI components and setting the adapter
     *
     * @param view The view containing this recycler view
     */
    private void createFounderRecyclerView(View view) {
        founderRecyclerView = view.findViewById(R.id.stakeholder_founder_recycler_view);
        founderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        founderAdapter = new StakeholderRecyclerViewAdapter(founderList);
        founderRecyclerView.setAdapter(founderAdapter);
        founderAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                FounderInputFragment founderFragment = new FounderInputFragment();
                Bundle args = new Bundle();
                Founder selectedFounder = ((Founder) founderList.get(position));
                founderFragment.passFounder(selectedFounder);
                changeFragment(founderFragment, null);

                getArguments().putBoolean("editFounder", true);
                getArguments().putInt("editIndex", position);
            }

            @Override
            public void onClickDelete(int position) {
                founderList.remove(position);
                founderAdapter.notifyItemRemoved(position);
            }
        });
    }

    /**
     * Delegate creating of the recycler view and create observer for BoardMemberViewModel
     *
     * @param view The view containing the recycler view
     */
    private void setupBoardMemberRecyclerView(View view) {
        createBoardMemberRecyclerView(view);
        boardMemberViewModel = new ViewModelProvider(requireActivity()).get(BoardMemberViewModel.class);
        boardMemberViewModel.getSelectedBoardMember().observe(getViewLifecycleOwner(),
                boardMember -> {
                    boardMember.updateTitle();
                    if(getArguments().getBoolean("editBoardMember")) {
                        int position = getArguments().getInt("editIndex");
                        boardMemberList.set(position, boardMember);
                        boardMemberAdapter.notifyItemChanged(position);
                        getArguments().putBoolean("editBoardMember", false);
                    }

                    if(!boardMemberList.contains(boardMember)) {
                        boardMemberList.add(boardMember);
                        boardMemberAdapter.notifyDataSetChanged();
                    }
                });

    }

    /**
     * Create the recycler view for the board members by linking the UI components and setting the adapter
     *
     * @param view The view containing this recycler view
     */
    private void createBoardMemberRecyclerView(View view) {
        boardMemberRecyclerView = view.findViewById(R.id.stakeholder_board_member_recycler_view);
        boardMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        boardMemberAdapter = new StakeholderRecyclerViewAdapter(boardMemberList);
        boardMemberRecyclerView.setAdapter(boardMemberAdapter);
        boardMemberAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                BoardMemberInputFragment boardMemberFragment = new BoardMemberInputFragment();
                BoardMember selectedBoardMember = ((BoardMember) boardMemberList.get(position));
                boardMemberFragment.passBoardMember(selectedBoardMember);
                changeFragment(boardMemberFragment);

                getArguments().putBoolean("editBoardMember", true);
                getArguments().putInt("editIndex", position);
            }

            @Override
            public void onClickDelete(int position) {
                boardMemberList.remove(position);
                boardMemberAdapter.notifyItemRemoved(position);
            }
        });
    }

    /**
     * Delegate creating of ShareholderRecycler view and create observer for ShareholderViewModel
     *
     * @param view The view containing the recycler view
     */
    private void setupShareholderRecyclerView(View view) {
        createShareholderRecyclerView(view);
        shareholderViewModel = new ViewModelProvider(requireActivity()).get(ShareholderViewModel.class);
        shareholderViewModel.getSelectedShareholder().observe(getViewLifecycleOwner(),
                shareholder -> {
                    shareholder.updateTitle();
                    if(getArguments().getBoolean("editShareholder")) {
                        int position = getArguments().getInt("editIndex");
                        shareholderList.set(position, shareholder);
                        shareholderAdapter.notifyItemChanged(position);
                        getArguments().putBoolean("editShareholder", false);
                    }

                    if(!shareholderList.contains(shareholder)) {
                        shareholderList.add(shareholder);
                        shareholderAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * Creating the recycler view for the shareholder by linking the UI components and setting the adapter
     *
     * @param view
     */
    private void createShareholderRecyclerView(View view) {
        shareholderRecyclerView = view.findViewById(R.id.stakeholder_shareholder_recycler_view);
        shareholderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        shareholderAdapter = new StakeholderRecyclerViewAdapter(shareholderList);
        shareholderRecyclerView.setAdapter(shareholderAdapter);
        shareholderAdapter.setOnClickListener(new StakeholderRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                ShareholderInputFragment shareholderFragment = new ShareholderInputFragment();
                Shareholder selectedShareholder = ((Shareholder) shareholderList.get(position));
                shareholderFragment.passShareholder(selectedShareholder);
                changeFragment(shareholderFragment);

                getArguments().putBoolean("editShareholder", true);
                getArguments().putInt("editIndex", position);
            }

            @Override
            public void onClickDelete(int position) {
                shareholderList.remove(position);
                shareholderAdapter.notifyItemRemoved(position);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_stakeholder:
                processInputs();
                finishButton.setEnabled(false);
                break;
            default:
                break;
        }
    }

    /**
     * Process given inputs
     */
    private void processInputs() {
        if (founderList.isEmpty()) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            finishButton.setEnabled(true);
            return;
        }

        try {
            RegistrationHandler.saveStakeholder(shareholderList, boardMemberList, founderList);

            Gson gson = new Gson();
            String startup = gson.toJson(RegistrationHandler.getStartup());
            JSONObject jsonStartup = new JSONObject(startup);
            ApiRequestHandler.performPostRequest("startup/register", registerCallback,
                    errorCallback, jsonStartup, getContext());
            Log.d("debugMessage", startup);
        } catch (IOException | JSONException e) {
            //TODO: Proper exception handling
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Cancel ongoing registration as request was successful and proceed to login page
     */
    Function<JSONObject, Void> registerCallback = response -> {
        try {
            RegistrationHandler.finish(response.getLong("id"), response.getString("token"));
            clearBackstackAndReplace(new MatchesFragment());
        } catch (Exception e ){
            showSimpleDialog(getString(R.string.generic_error_title),
                    getString(R.string.generic_error_text));
            Log.d("StartupStakeholder", e.getMessage());
        }

        finishButton.setEnabled(true);
        return null;
    };

    Function<VolleyError, Void> errorCallback = response -> {
        try {
            if (response.networkResponse.statusCode == 500) {
                JSONObject body = new JSONObject(new String(
                        response.networkResponse.data, "UTF-8"));
                Log.d("StartupImages", body.getString("message"));
            }
        } catch(Exception e) {
            Log.d("InvestorImagesErrorException", e.getMessage());
        }

        showSimpleDialog(getString(R.string.generic_error_title),
                getString(R.string.generic_error_text));
        finishButton.setEnabled(true);
        return null;
    };
}
