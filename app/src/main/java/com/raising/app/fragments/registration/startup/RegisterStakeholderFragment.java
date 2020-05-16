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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raising.app.R;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.onboarding.OnboardingPost1Fragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.BoardMemberInputFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.FounderInputFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.ShareholderInputFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.BoardMemberViewModel;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.FounderViewModel;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.ShareholderViewModel;
import com.raising.app.models.Startup;
import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.models.stakeholder.StakeholderItem;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.Serializer;
import com.raising.app.util.recyclerViewAdapter.StakeholderAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.Function;

public class RegisterStakeholderFragment extends RaisingFragment {
    private final String TAG = "RegisterStakeholderFragment";
    private FounderViewModel founderViewModel;
    private BoardMemberViewModel boardMemberViewModel;
    private ShareholderViewModel shareholderViewModel;
    Button finishButton;

    private boolean editMode = false;
    private Startup startup;

    // hold references to the respective recycler views
    private RecyclerView founderRecyclerView, boardMemberRecyclerView, shareholderRecyclerView;

    // hold references to the respective recycler view adapters
    private StakeholderAdapter founderAdapter, boardMemberAdapter, shareholderAdapter;

    // the lists, that are displayed in the respective recycler views
    private ArrayList<StakeholderItem> founderList,
            boardMemberList, shareholderList;

    public RegisterStakeholderFragment() {
        setArguments(new Bundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_stakeholder), true);

        return inflater.inflate(R.layout.fragment_register_stakeholder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        finishButton = view.findViewById(R.id.button_stakeholder);
        finishButton.setOnClickListener(v -> {
            finishButton.setEnabled(false);
            processInputs();
        });

        // check if this fragment is opened for registration or for profile
        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            // this fragment is opened via profile
            view.findViewById(R.id.registration_profile_progress).setVisibility(View.INVISIBLE);
            finishButton.setHint(getString(R.string.myProfile_apply_changes));
            finishButton.setEnabled(false);
            startup = (Startup) accountViewModel.getAccount().getValue();
            editMode = true;
            hideBottomNavigation(false);
        } else {
            startup = RegistrationHandler.getStartup();
        }

        if (getArguments().getString("founderList") == null
                && getArguments().getString("boardMemberList") == null
                && getArguments().getString("shareholderList") == null) {
            founderList = new ArrayList<>();
            boardMemberList = new ArrayList<>();
            shareholderList = new ArrayList<>();
            founderList.addAll(startup.getFounders());
            boardMemberList.addAll(startup.getBoardMembers());
            shareholderList.addAll(startup.getPrivateShareholders());
            shareholderList.addAll(startup.getCorporateShareholders());
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

        Button btnAddFounder = view.findViewById(R.id.button_add_founder);
        btnAddFounder.setOnClickListener(v -> changeFragment(new FounderInputFragment()));

        Button btnAddBoardMember
                = view.findViewById(R.id.button_add_board_member);
        btnAddBoardMember.setOnClickListener(v -> changeFragment(new BoardMemberInputFragment()));

        Button btnAddShareholder
                = view.findViewById(R.id.button_add_shareholder);
        btnAddShareholder.setOnClickListener(v -> changeFragment(new ShareholderInputFragment()));
    }

    @Override
    public void onDestroyView() {
        hideBottomNavigation(false);
        super.onDestroyView();
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
                    if (getArguments().getBoolean("editFounder")) {
                        int position = getArguments().getInt("editIndex");
                        founderList.set(position, founder);
                        founderAdapter.notifyItemChanged(position);
                        finishButton.setEnabled(true);
                        getArguments().putBoolean("editFounder", false);
                    }

                    if (!founderList.contains(founder)) {
                        founderList.add(founder);
                        founderAdapter.notifyDataSetChanged();
                        finishButton.setEnabled(true);
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
        founderAdapter = new StakeholderAdapter(founderList);
        founderRecyclerView.setAdapter(founderAdapter);
        founderAdapter.setOnClickListener(new StakeholderAdapter.OnClickListener() {
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
                if (editMode) {
                    if (founderList.get(position).getId() != -1) {
                        ApiRequestHandler.performDeleteRequest("startup/founder/" +
                                        founderList.get(position).getId(),
                                result -> {
                                    founderList.remove(position);
                                    founderAdapter.notifyItemRemoved(position);
                                    finishButton.setEnabled(true);
                                    return null;
                                },
                                ApiRequestHandler.errorHandler);
                    } else {
                        founderList.remove(position);
                        founderAdapter.notifyItemRemoved(position);
                    }
                } else {
                    founderList.remove(position);
                    founderAdapter.notifyItemRemoved(position);
                    finishButton.setEnabled(true);
                }
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
                    if (getArguments().getBoolean("editBoardMember")) {
                        int position = getArguments().getInt("editIndex");
                        boardMemberList.set(position, boardMember);
                        boardMemberAdapter.notifyItemChanged(position);
                        finishButton.setEnabled(true);
                        getArguments().putBoolean("editBoardMember", false);
                    }

                    if (!boardMemberList.contains(boardMember)) {
                        boardMemberList.add(boardMember);
                        boardMemberAdapter.notifyDataSetChanged();
                        finishButton.setEnabled(true);
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
        boardMemberAdapter = new StakeholderAdapter(boardMemberList);
        boardMemberRecyclerView.setAdapter(boardMemberAdapter);
        boardMemberAdapter.setOnClickListener(new StakeholderAdapter.OnClickListener() {
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
                if (editMode) {
                    if (boardMemberList.get(position).getId() != -1) {
                        ApiRequestHandler.performDeleteRequest("startup/boardmember/" +
                                        boardMemberList.get(position).getId(),
                                result -> {
                                    boardMemberList.remove(position);
                                    boardMemberAdapter.notifyItemRemoved(position);
                                    finishButton.setEnabled(true);
                                    return null;
                                },
                                ApiRequestHandler.errorHandler);
                    } else {
                        boardMemberList.remove(position);
                        boardMemberAdapter.notifyItemRemoved(position);
                    }
                } else {
                    boardMemberList.remove(position);
                    boardMemberAdapter.notifyItemRemoved(position);
                    finishButton.setEnabled(true);
                }
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
                    if (getArguments().getBoolean("editShareholder")) {
                        int position = getArguments().getInt("editIndex");
                        shareholderList.set(position, shareholder);
                        shareholderAdapter.notifyItemChanged(position);
                        finishButton.setEnabled(true);
                        getArguments().putBoolean("editShareholder", false);
                    }

                    if (!shareholderList.contains(shareholder)) {
                        shareholderList.add(shareholder);
                        shareholderAdapter.notifyDataSetChanged();
                        finishButton.setEnabled(true);
                    }
                });
    }

    /**
     * Creating the recycler view for the shareholder by linking the UI components and setting the adapter
     *
     * @param view The view containing this recycler view
     */
    private void createShareholderRecyclerView(View view) {
        shareholderRecyclerView = view.findViewById(R.id.stakeholder_shareholder_recycler_view);
        shareholderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        shareholderAdapter = new StakeholderAdapter(shareholderList);
        shareholderRecyclerView.setAdapter(shareholderAdapter);
        shareholderAdapter.setOnClickListener(new StakeholderAdapter.OnClickListener() {
            @Override
            public void onClickEdit(int position) {
                ShareholderInputFragment shareholderFragment = new ShareholderInputFragment();
                Shareholder selectedShareholder = ((Shareholder) shareholderList.get(position));
                shareholderFragment.passShareholder(selectedShareholder);
                changeFragment(shareholderFragment);
                if (getArguments() != null) {
                    getArguments().putBoolean("editShareholder", true);
                    getArguments().putInt("editIndex", position);
                }
            }

            @Override
            public void onClickDelete(int position) {
                if (editMode) {
                    if (shareholderList.get(position).getId() != -1) {
                        String endpoint;
                        if (((Shareholder) shareholderList.get(position)).isPrivateShareholder()) {
                            endpoint = "startup/privateshareholder/";
                        } else {
                            endpoint = "startup/corporateshareholder/";
                        }
                        ApiRequestHandler.performDeleteRequest(endpoint +
                                        shareholderList.get(position).getId(),
                                result -> {
                                    shareholderList.remove(position);
                                    shareholderAdapter.notifyItemRemoved(position);
                                    finishButton.setEnabled(true);
                                    return null;
                                },
                                ApiRequestHandler.errorHandler);
                    } else {
                        shareholderList.remove(position);
                        shareholderAdapter.notifyItemRemoved(position);
                    }
                } else {
                    shareholderList.remove(position);
                    shareholderAdapter.notifyItemRemoved(position);
                    finishButton.setEnabled(true);
                }
            }
        });
    }

    /**
     * Check validity of user inputs, then handle the inputs
     */
    private void processInputs() {
        if (founderList.isEmpty() || boardMemberList.isEmpty()) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            finishButton.setEnabled(true);
            return;
        }

        float totalEquity = 0;
        for (StakeholderItem stakeholderItem : shareholderList) {
            totalEquity += ((Shareholder) stakeholderItem).getEquityShare();
        }
        if (totalEquity > 100) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_stakeholder_error_equity));
            return;
        }

        try {
            if (!editMode) {
                registerNewStakeholders();
            } else {
                updateExistingStakeholders();
            }
        } catch (IOException | JSONException e) {
            viewStateViewModel.stopLoading();
            //TODO: remove manually set loading panel
            Log.e("RegisterStakeholderFragment", "Error in processInputs: " +
                    e.getMessage());
        }
    }

    /**
     * Cancel ongoing registration as request was successful and proceed to login page
     */
    Function<JSONObject, Void> registerCallback = response -> {
        viewStateViewModel.stopLoading();
        try {
            startup = RegistrationHandler.getStartup();
            startup.setId(response.getLong("id"));
            accountViewModel.setAccount(startup);

            RegistrationHandler.finish(response.getLong("id"),
                    response.getString("token"), true);

            if (isFirstAppLaunch() && !isDisablePostOnboarding()) {
                clearBackstackAndReplace(new OnboardingPost1Fragment());
            } else {
                clearBackstackAndReplace(new MatchesFragment());
            }

        } catch (Exception e) {
            showGenericError();
            Log.d("StartupStakeholder", "" + e.getMessage());
        }
        return null;
    };

    Function<VolleyError, Void> errorCallback = response -> {
        viewStateViewModel.stopLoading();
        try {
            if (response.networkResponse != null) {
                if (response.networkResponse.statusCode == 500) {
                    JSONObject body = new JSONObject(new String(
                            response.networkResponse.data, "UTF-8"));
                    Log.d("StartupImages", body.getString("message"));
                }
            } else {
                clearBackstackAndReplace(new LoginFragment());
            }
        } catch (Exception e) {
            Log.d("InvestorImagesErrorException", "" + e.getMessage());
        }

        showGenericError();
        finishButton.setEnabled(true);
        return null;
    };

    /**
     * Add new stakeholders to account during registration
     * @throws IOException
     * @throws JSONException
     */
    private void registerNewStakeholders() throws IOException, JSONException {
        startup.clearFounders();
        startup.clearCorporateShareholders();
        startup.clearPrivateShareholders();
        startup.clearBoardMembers();

        founderList.forEach(founder -> {
            startup.addFounder((Founder) founder);
        });

        boardMemberList.forEach(boardMember -> {
            startup.addBoardMember((BoardMember) boardMember);
        });

        shareholderList.forEach(shareholder -> {
            if (((Shareholder) shareholder).isPrivateShareholder()) {
                startup.addPrivateShareholder((Shareholder) shareholder);
            } else {
                startup.addCorporateShareholder((Shareholder) shareholder);
            }
        });

        RegistrationHandler.saveStartup(startup);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Startup.class,
                Serializer.StartupRegisterSerializer);
        Gson gson = gsonBuilder.create();
        String startup = gson.toJson(RegistrationHandler.getStartup());
        JSONObject jsonStartup = new JSONObject(startup);
        viewStateViewModel.startLoading();
        ApiRequestHandler.performPostRequest("startup/register", registerCallback,
                errorCallback, jsonStartup);
        Log.d("RegistrationString", startup);
    }

    /**
     * Update existing accounts stakeholders
     */
    private void updateExistingStakeholders() {
        ArrayList<Founder> founders = new ArrayList<>();
        founderList.forEach(founder -> {
            founders.add((Founder) founder);
        });
        startup.setFounders(founders);

        ArrayList<BoardMember> boardMembers = new ArrayList<>();
        boardMemberList.forEach(boardMember -> {
            boardMembers.add((BoardMember) boardMember);
        });
        startup.setBoardMembers(boardMembers);

        ArrayList<Shareholder> corpShareholders = new ArrayList<>();
        ArrayList<Shareholder> privShareholders = new ArrayList<>();
        shareholderList.forEach(shareholder -> {
            if (((Shareholder) shareholder).isPrivateShareholder()) {
                privShareholders.add((Shareholder) shareholder);
            } else {
                corpShareholders.add((Shareholder) shareholder);
            }
        });
        startup.setPrivateShareholders(privShareholders);
        startup.setCorporateShareholders(corpShareholders);

        popCurrentFragment();
    }
}
