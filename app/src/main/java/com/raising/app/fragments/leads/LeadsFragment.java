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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.UnlockPremiumFragment;
import com.raising.app.fragments.profile.InvestorPublicProfileFragment;
import com.raising.app.fragments.profile.StartupPublicProfileFragment;
import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.LeadState;
import com.raising.app.models.leads.Lead;
import com.raising.app.models.ViewState;
import com.raising.app.util.ImageHandler;
import com.raising.app.util.SubscriptionHandler;
import com.raising.app.util.recyclerViewAdapter.LeadsAdapter;
import com.raising.app.viewModels.LeadsViewModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LeadsFragment extends RaisingFragment {
    private String TAG = "LeadsFragment";

    private LeadState leadState;
    private LeadsViewModel leadsViewModel;
    private LeadsAdapter todayAdapter, thisWeekAdapter, thisMonthAdapter, earlierAdapter;
    private ArrayList<Lead> today, thisWeek, thisMonth, earlier;
    private ConstraintLayout todayLayout, thisWeekLayout, thisMonthLayout, earlierLayout, emptyLeadsLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        // find all views and set their visibilities
        emptyLeadsLayout = view.findViewById(R.id.empty_leads_fragment_text);
        emptyLeadsLayout.setVisibility(View.GONE);
        Log.d(TAG, "onViewCreated: EmptyLeadsVisibility GONE");

        swipeRefreshLayout = view.findViewById(R.id.leads_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> leadsViewModel.loadLeads());

        today = new ArrayList<>();
        thisWeek = new ArrayList<>();
        thisMonth = new ArrayList<>();
        earlier = new ArrayList<>();

        todayLayout = view.findViewById(R.id.leads_today);
        thisWeekLayout = view.findViewById(R.id.leads_this_week);
        thisMonthLayout = view.findViewById(R.id.leads_this_month);
        earlierLayout = view.findViewById(R.id.leads_earlier);

        todayLayout.setVisibility(View.GONE);
        thisWeekLayout.setVisibility(View.GONE);
        thisMonthLayout.setVisibility(View.GONE);
        earlierLayout.setVisibility(View.GONE);

        view.findViewById(R.id.leads_open_requests).setVisibility(View.GONE);

        // check for leads state
        if (getArguments() != null) {
            leadState = (LeadState) getArguments().getSerializable("leadsState");
            TAG = "LeadsFragment" + leadState;
            leadsViewModel.loadLeads();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        leadsViewModel.loadLeads();
    }

    @Override
    public void onResourcesLoaded() {
        //prepare recycler view adapters and recycler views
        todayAdapter = new LeadsAdapter(today, leadState);
        thisWeekAdapter = new LeadsAdapter(thisWeek, leadState);
        thisMonthAdapter = new LeadsAdapter(thisMonth, leadState);
        earlierAdapter = new LeadsAdapter(earlier, leadState);

        setupRecyclerView(R.id.leads_tab_recycler_today, todayAdapter, today);
        setupRecyclerView(R.id.leads_tab_recycler_this_week, thisWeekAdapter, thisWeek);
        setupRecyclerView(R.id.leads_tab_recycler_this_month, thisMonthAdapter, thisMonth);
        setupRecyclerView(R.id.leads_tab_recycler_earlier, earlierAdapter, earlier);

        // set observer for view state of leadsViewModel
        leadsViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
            if (state == ViewState.RESULT || state == ViewState.CACHED) {
                filterLeads();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (leadsViewModel.getViewState().getValue() == ViewState.RESULT
                || leadsViewModel.getViewState().getValue() == ViewState.CACHED) {
            filterLeads();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Initialize recyclerview for leads. The leads are grouped by their timestamp.
     * Each timestamp has its own recycler view.
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
            // check if user has valid subscription
            if (!SubscriptionHandler.hasValidSubscription()) {
                ((RaisingFragment) getParentFragment()).changeFragment(new UnlockPremiumFragment());
            } else {
                Bundle args = new Bundle();

                args.putLong("leadId", leads.get(position).getId());
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
            }
        });

        adapter.setOnClickListener(position -> {
            // check if user has valid subscription
            if (!SubscriptionHandler.hasValidSubscription()) {
                ((RaisingFragment) getParentFragment()).changeFragment(new UnlockPremiumFragment());
            } else {
                Bundle args = new Bundle();
                args.putLong("id", leads.get(position).getAccountId());
                args.putInt("score", leads.get(position).getMatchingPercent());
                args.putString("title", leads.get(position).getTitle());
                args.putLong("relationshipId", leads.get(position).getId());
                args.putSerializable("handshakeState", leads.get(position).getHandshakeState());
                if (leads.get(position).isStartup()) {
                    Fragment fragment = new StartupPublicProfileFragment();
                    fragment.setArguments(args);
                    ((RaisingFragment) getParentFragment()).changeFragment(fragment);
                } else {
                    Fragment fragment = new InvestorPublicProfileFragment();
                    fragment.setArguments(args);
                    ((RaisingFragment) getParentFragment()).changeFragment(fragment);
                }
            }
        });
    }

    /**
     * Prepare open requests layout and toggle its visibility
     */
    @SuppressLint("RestrictedApi")
    private void prepareOpenRequestsLayout() {
        // prepare and fill open requests layout
        ConstraintLayout openRequests = getView().findViewById(R.id.leads_open_requests);
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
                ImageHandler.loadProfileImage(leadsViewModel.getOpenRequests().get(0), image);
                BadgeDrawable badge = BadgeDrawable.create(Objects.requireNonNull(this.getContext()));
                badge.setNumber(leadsViewModel.getOpenRequests().size());
                badge.setBadgeGravity(BadgeDrawable.TOP_START);
                View badgeLayout = getView().findViewById(R.id.leads_open_request_badge_layout);
                BadgeUtils.attachBadgeDrawable(badge, badgeLayout, openRequestsArrowLayout);
                openRequests.setOnClickListener(v -> {
                    ((RaisingFragment) getParentFragment()).changeFragment(new LeadsOpenRequestsFragment());
                });
            }
        }
    }

    /**
     * Filter all leads based on their timestamps into four different categories.
     */
    private void filterLeads() {
        prepareOpenRequestsLayout();
        // initially clear all array lists and set everything invisible
        today.clear();
        todayLayout.setVisibility(View.GONE);
        thisWeek.clear();
        thisWeekLayout.setVisibility(View.GONE);
        thisMonth.clear();
        thisMonthLayout.setVisibility(View.GONE);
        earlier.clear();
        earlierLayout.setVisibility(View.GONE);

        emptyLeadsLayout.setVisibility(View.GONE);

        // loop over all leads and filter them
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
                } else if (daysSince(lead.getTimestamp()) < 30) {
                    thisMonth.add(lead);
                    thisMonthLayout.setVisibility(View.VISIBLE);
                } else {
                    earlier.add(lead);
                    earlierLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // hide empty leads layout
        if (leadState == LeadState.YOUR_TURN) {
            if (today.size() == 0 && thisWeek.size() == 0 && thisMonth.size() == 0 && earlier.size() == 0 && leadsViewModel.getOpenRequests().size() == 0) {
                emptyLeadsLayout.setVisibility(View.VISIBLE);
            } else {
                emptyLeadsLayout.setVisibility(View.GONE);
            }
        } else {
            if (today.size() == 0 && thisWeek.size() == 0 && thisMonth.size() == 0 && earlier.size() == 0) {
                emptyLeadsLayout.setVisibility(View.VISIBLE);
            } else {
                emptyLeadsLayout.setVisibility(View.GONE);
            }
        }
        // notify recycler view adapters, that data has changed
        todayAdapter.notifyDataSetChanged();
        thisWeekAdapter.notifyDataSetChanged();
        thisMonthAdapter.notifyDataSetChanged();
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