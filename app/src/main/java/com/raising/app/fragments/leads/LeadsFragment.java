package com.raising.app.fragments.leads;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.LeadState;
import com.raising.app.models.leads.Lead;
import com.raising.app.models.ViewState;
import com.raising.app.util.recyclerViewAdapter.LeadsAdapter;
import com.raising.app.viewModels.LeadsViewModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LeadsFragment extends RaisingFragment {

    private final String TAG = "HandshakeTabFragment";

    private LeadState leadState;
    private LeadsViewModel leadsViewModel;
    private LeadsAdapter todayAdapter, thisWeekAdapter, earlierAdapter;
    private ArrayList<Lead> today, thisWeek, earlier;
    private ConstraintLayout todayLayout, thisWeekLayout, earlierLayout, emptyLeadsLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean observersSet = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        leadsViewModel = ViewModelProviders.of(getActivity())
                .get(LeadsViewModel.class);
        return inflater.inflate(R.layout.fragment_leads, container, false);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyLeadsLayout = view.findViewById(R.id.empty_leads_fragment_text);

        swipeRefreshLayout = view.findViewById(R.id.leads_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        today = new ArrayList<>();
        thisWeek = new ArrayList<>();
        earlier = new ArrayList<>();

        todayLayout = view.findViewById(R.id.leads_today);
        thisWeekLayout = view.findViewById(R.id.leads_this_week);
        earlierLayout = view.findViewById(R.id.leads_earlier);

        todayLayout.setVisibility(View.GONE);
        thisWeekLayout.setVisibility(View.GONE);
        earlierLayout.setVisibility(View.GONE);
        emptyLeadsLayout.setVisibility(View.GONE);
        getView().findViewById(R.id.leads_open_requests).setVisibility(View.GONE);

        // check for leads state
        if (getArguments() != null) {
            leadState = (LeadState) getArguments().getSerializable("leadsState");
            // prepare leadsViewModel for usage

            leadsViewModel.loadLeads();

            todayAdapter = new LeadsAdapter(today, leadState);
            thisWeekAdapter = new LeadsAdapter(thisWeek, leadState);
            earlierAdapter = new LeadsAdapter(earlier, leadState);

            setupRecyclerView(R.id.leads_tab_recycler_today, todayAdapter, today);
            setupRecyclerView(R.id.leads_tab_recycler_this_week, thisWeekAdapter, thisWeek);
            setupRecyclerView(R.id.leads_tab_recycler_earlier, earlierAdapter, earlier);

            if (resourcesViewModel.getViewState().getValue() == ViewState.RESULT ||
                    resourcesViewModel.getViewState().getValue() == ViewState.CACHED) {
                leadsViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
                    processLeadsViewState(state);
                });
                processLeadsViewState(leadsViewModel.getViewState().getValue());

                observersSet = true;
            }
        }
    }

    @Override
    protected void onResourcesLoaded() {
        if (!observersSet && leadState != null) {
            leadsViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
                processLeadsViewState(state);
            });
            processLeadsViewState(leadsViewModel.getViewState().getValue());
        }
    }

    /**
     * @param state
     */
    private void processLeadsViewState(ViewState state) {
        switch (state) {
            case CACHED:
            case RESULT:
                dismissLoadingPanel();
                loadData();
                break;
            case LOADING:
                showLoadingPanel();
                break;
        }
    }

    /**
     * Initialize recyclerview for leads
     *
     * @param id      id of recycler view
     * @param adapter adapter of recycler view
     * @param leads   list of leads for recycler view
     */
    private void setupRecyclerView(int id, LeadsAdapter adapter, List<Lead> leads) {
        RecyclerView recyclerView = getView().findViewById(id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Bundle args = new Bundle();

            args.putSerializable("lead", leads.get(position));
            Fragment contactFragment = new LeadsInteractionFragment();

            if ((leads.get(position).getHandshakeState() == InteractionState.INVESTOR_ACCEPTED)
                    || (leads.get(position).getHandshakeState() == InteractionState.STARTUP_ACCEPTED)) {
                args.putBoolean("disableContact", true);
            } else if ((leads.get(position).getHandshakeState() == InteractionState.INVESTOR_DECLINED)
                    || (leads.get(position).getHandshakeState() == InteractionState.STARTUP_DECLINED)) {
                args.putBoolean("declinedContact", true);
            }
            contactFragment.setArguments(args);
            ((RaisingFragment) getParentFragment()).changeFragment(contactFragment);
        });
    }

    /**
     * Populate recycler views
     */
    @SuppressLint("RestrictedApi")
    private void loadData() {
        ConstraintLayout openRequests = getView().findViewById(R.id.leads_open_requests);
        ImageView openRequestsArrow = getView().findViewById(R.id.leads_open_requests_arrow);
        FrameLayout openRequestsArrowLayout = getView().findViewById(R.id.leads_open_request_arrow_layout);
        if (!(leadState.equals(LeadState.YOUR_TURN))) {
            openRequests.setVisibility(View.GONE);
        } else {
            if (leadsViewModel.getOpenRequests().size() == 0) {
                openRequests.setVisibility(View.GONE);
            } else {
                openRequests.setVisibility(View.VISIBLE);
                ImageView image = getView().findViewById(R.id.leads_open_requests_image);
                // set image of uppermost index in openRequests
                loadProfileImage(leadsViewModel.getOpenRequests().get(0).getProfilePictureId(), image);
                BadgeDrawable badge = BadgeDrawable.create(Objects.requireNonNull(this.getContext()));
                badge.setNumber(leadsViewModel.getOpenRequests().size());
                badge.setBadgeGravity(BadgeDrawable.TOP_START);
                BadgeUtils.attachBadgeDrawable(badge, openRequestsArrow, openRequestsArrowLayout);
                openRequests.setOnClickListener(v ->
                        ((RaisingFragment) getParentFragment())
                                .changeFragment(new LeadsOpenRequestsFragment()));
            }
        }
        filterLeads();
    }


    /**
     * Filter leads by state and timestamp
     */
    private void filterLeads() {
        today.clear();
        thisWeek.clear();
        earlier.clear();
        todayLayout.setVisibility(View.GONE);
        thisWeekLayout.setVisibility(View.GONE);
        earlierLayout.setVisibility(View.GONE);

        leadsViewModel.getLeads().getValue().forEach(lead -> {
            if (lead.getState() == leadState) {
                if (lead.isStartup()) {
                    lead.setTitle(lead.getCompanyName());
                    lead.setAttribute(resources.getInvestmentPhase(
                            lead.getInvestmentPhaseId()).getName());
                } else {
                    lead.setTitle(lead.getFirstName() + " " + lead.getLastName());
                    if (resources.getInvestorType(
                            lead.getInvestorTypeId()) != null) {
                        lead.setAttribute(resources.getInvestorType(
                                lead.getInvestorTypeId()).getName());
                    }
                }

                if (daysSince(lead.getTimestamp()) < 1) {
                    today.add(lead);
                    todayLayout.setVisibility(View.VISIBLE);
                } else if (daysSince(lead.getTimestamp()) < 7) {
                    thisWeek.add(lead);
                    thisWeekLayout.setVisibility(View.VISIBLE);
                } else {
                    earlier.add(lead);
                    earlierLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        if (today.size() == 0 && thisWeek.size() == 0 && earlier.size() == 0) {
            emptyLeadsLayout.setVisibility(View.VISIBLE);
        }

        todayAdapter.notifyDataSetChanged();
        thisWeekAdapter.notifyDataSetChanged();
        earlierAdapter.notifyDataSetChanged();
    }

    /**
     * Calculate difference from given date to today in days
     *
     * @param timestamp timestamp in the past
     * @return difference in days
     */
    private int daysSince(Timestamp timestamp) {
        long diffMillis = Math.abs(new Date().getTime() - timestamp.getTime());
        return (int) TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
    }
}
