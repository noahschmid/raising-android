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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.LeadState;
import com.raising.app.models.leads.Lead;
import com.raising.app.models.ViewState;
import com.raising.app.util.recyclerViewAdapter.LeadsAdapter;
import com.raising.app.util.recyclerViewAdapter.RecyclerViewMargin;
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
    private RecyclerView todayRecycler, thisWeekRecycler, earlierRecycler;
    private ConstraintLayout todayLayout, thisWeekLayout, earlierLayout;
    private TextView todayLayoutTitle, thisWeekLayoutTitle, earlierLayoutTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leads, container, false);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        today = new ArrayList<>();
        thisWeek = new ArrayList<>();
        earlier = new ArrayList<>();

        todayLayoutTitle = view.findViewById(R.id.leads_tab_today);
        todayLayout = view.findViewById(R.id.leads_today);
        thisWeekLayoutTitle = view.findViewById(R.id.leads_tab_this_week);
        thisWeekLayout = view.findViewById(R.id.leads_this_week);
        earlierLayoutTitle = view.findViewById(R.id.leads_tab_earlier);
        earlierLayout = view.findViewById(R.id.leads_earlier);

        // check for leads state
        if (getArguments() != null) {
            leadState = (LeadState) getArguments().getSerializable("leadsState");
            // prepare leadsViewModel for usage
            leadsViewModel = ViewModelProviders.of(getActivity())
                    .get(LeadsViewModel.class);

            leadsViewModel.loadLeads();

            todayAdapter = new LeadsAdapter(today, leadState);
            thisWeekAdapter = new LeadsAdapter(thisWeek, leadState);
            earlierAdapter = new LeadsAdapter(earlier, leadState);

            setupRecyclerView(R.id.leads_tab_recycler_today, todayAdapter, today);
            setupRecyclerView(R.id.leads_tab_recycler_this_week, thisWeekAdapter, thisWeek);
            setupRecyclerView(R.id.leads_tab_recycler_earlier, earlierAdapter, earlier);

            leadsViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
                processLeadsViewState(state);
            });
            processLeadsViewState(leadsViewModel.getViewState().getValue());
        }
    }

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
        recyclerView.addItemDecoration(new RecyclerViewMargin(15));
        adapter.setOnItemClickListener(position -> {
            Bundle args = new Bundle();
            args.putLong("id", leads.get(position).getId());
            Fragment contactFragment = new LeadsContactFragment();
            contactFragment.setArguments(args);
            ((RaisingFragment)getParentFragment()).changeFragment(contactFragment);
            /*
            if(leads.get(position).getHandshakeState() == InteractionState.HANDSHAKE) {
                Fragment contactFragment = new LeadsContactFragment();
                contactFragment.setArguments(args);
                ((RaisingFragment)getParentFragment()).changeFragment(contactFragment);
            } else {
                showSimpleDialog(getString(R.string.leads_no_handshake_dialog_title), getString(R.string.leads_no_handshake_dialog_text));
            }
             */
        });
    }

    /**
     * Populate recycler views
     */
    @SuppressLint("RestrictedApi")
    private void loadData() {
        ConstraintLayout openRequests = getView().findViewById(R.id.leads_open_requests);
        ImageView openRequestsArrow = getView().findViewById(R.id.leads_open_requests_arrow);
        if (!(leadState.equals(LeadState.YOUR_TURN))) {
            openRequests.setVisibility(View.GONE);
        } else {
            if (leadsViewModel.getOpenRequests().size() == 0) {
                openRequests.setVisibility(View.GONE);
            } else {
                ImageView image = getView().findViewById(R.id.leads_open_requests_image);
                // set image of uppermost index in openRequests
                loadProfileImage(leadsViewModel.getOpenRequests().get(0).getProfilePictureId(), image);
                BadgeDrawable badge = BadgeDrawable.create(Objects.requireNonNull(this.getContext()));
                badge.setNumber(leadsViewModel.getOpenRequests().size());
                badge.setBadgeGravity(BadgeDrawable.TOP_START);
                BadgeUtils.attachBadgeDrawable(badge, openRequestsArrow, null);
                openRequests.setOnClickListener(v ->
                        ((RaisingFragment)getParentFragment())
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
        todayLayoutTitle.setVisibility(View.GONE);
        thisWeekLayout.setVisibility(View.GONE);
        thisWeekLayoutTitle.setVisibility(View.GONE);
        earlierLayout.setVisibility(View.GONE);
        earlierLayoutTitle.setVisibility(View.GONE);

        leadsViewModel.getLeads().getValue().forEach(lead -> {
            if (lead.getState() == leadState) {
                if (lead.isStartup()) {
                    lead.setTitle(lead.getCompanyName());
                    lead.setAttribute(resources.getInvestmentPhase(
                            lead.getInvestmentPhaseId()).getName());
                } else {
                    lead.setTitle(lead.getFirstName() + " " + lead.getLastName());
                    if(resources.getInvestorType(
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
                manageListTitlesVisibility();
            }
        });

        todayAdapter.notifyDataSetChanged();
        thisWeekAdapter.notifyDataSetChanged();
        earlierAdapter.notifyDataSetChanged();
    }

    /**
     * Manage the visibility of the recycler view titles
     */
    private void manageListTitlesVisibility() {
        if (today.size() != 0 && (thisWeek.size() != 0 || earlier.size() != 0)) {
            todayLayoutTitle.setVisibility(View.VISIBLE);
        }
        if (thisWeek.size() != 0 && (today.size() != 0 || earlier.size() != 0)) {
            thisWeekLayoutTitle.setVisibility(View.VISIBLE);
        }
        if(earlier.size() != 0 && (today.size() != 0 || thisWeek.size() != 0)) {
            earlierLayoutTitle.setVisibility(View.VISIBLE);
        }
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
