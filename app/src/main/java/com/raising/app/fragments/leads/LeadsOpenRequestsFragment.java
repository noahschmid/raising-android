package com.raising.app.fragments.leads;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.profile.InvestorPublicProfileFragment;
import com.raising.app.fragments.profile.StartupPublicProfileFragment;
import com.raising.app.models.leads.Lead;
import com.raising.app.models.ViewState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.recyclerViewAdapter.LeadsOpenRequestAdapter;
import com.raising.app.viewModels.LeadsViewModel;

import org.json.JSONObject;

import java.util.ArrayList;

public class LeadsOpenRequestsFragment extends RaisingFragment {
    private final String TAG = "HandshakeOpenRequestFragment";
    private LeadsViewModel leadsViewModel;

    private ArrayList<Lead> openRequestItems;
    private RecyclerView openRequestRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_open_requests), true);

        return inflater.inflate(R.layout.fragment_leads_open_requests, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        leadsViewModel.loadLeads();
        checkForEmptyLayout();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");

        openRequestRecycler = view.findViewById(R.id.leads_open_requests_recycler_view);

        // prepare leadsViewModel for usage
        leadsViewModel = ViewModelProviders.of(getActivity())
                .get(LeadsViewModel.class);

        leadsViewModel.getViewState().observe(getViewLifecycleOwner(), state -> processViewState(state));
        processViewState(leadsViewModel.getViewState().getValue());
        openRequestItems = new ArrayList<>();

        resourcesViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
            Log.d(TAG, "onViewCreated: ResourceViewModel ViewState: " + state.toString());
        });

        // populate open requests
        if (resourcesViewModel.getViewState().getValue() == ViewState.RESULT ||
                resourcesViewModel.getViewState().getValue() == ViewState.CACHED) {
            leadsViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
                if(state == ViewState.RESULT || state == ViewState.CACHED) {
                    Log.d(TAG, "onViewCreated: ResourcesViewState " + resourcesViewModel.getViewState().getValue());
                    Log.d(TAG, "onViewCreated: LeadsViewState " + state);
                    populateOpenRequests();
                }
            });
        }
    }

    private void populateOpenRequests() {
        openRequestItems.clear();
        ArrayList<Lead> openRequests = leadsViewModel.getOpenRequests();

        for (int i = 0; i < openRequests.size(); ++i) {
            Lead request = openRequests.get(i);
            if (request.isStartup()) {
                request.setTitle(request.getCompanyName());
                request.setAttribute(resources.getInvestmentPhase(
                        request.getInvestmentPhaseId()).getName());
            } else {
                request.setTitle(request.getFirstName() + " " + request.getLastName());
                request.setAttribute(resources.getInvestorType(
                        request.getInvestorTypeId()).getName());
            }
            Log.d(TAG, "onViewCreated: Add OpenRequest: " + request.getTitle());
            openRequestItems.add(request);
        }
        checkForEmptyLayout();

        openRequestRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        LeadsOpenRequestAdapter adapter = new LeadsOpenRequestAdapter(openRequestItems);
        openRequestRecycler.setAdapter(adapter);

        adapter.setOnClickListener(new LeadsOpenRequestAdapter.OnClickListener() {
            @Override
            public void onClickAccept(int position) {
                String endpoint = "match/" + openRequestItems.get(position).getId() + "/accept";
                ApiRequestHandler.performPostRequest(endpoint, v -> {
                            openRequestItems.remove(position);
                            adapter.notifyItemRemoved(position);
                            checkForEmptyLayout();
                            return null;
                        },
                        err -> {
                            showGenericError();
                            Log.e(TAG, "onClickAccept: " + ApiRequestHandler.parseVolleyError(err));
                            return null;
                        },
                        new JSONObject());
            }

            @Override
            public void onClickDecline(int position) {
                String endpoint = "match/" + openRequestItems.get(position).getId() + "/decline";
                ApiRequestHandler.performPostRequest(endpoint, v -> {
                            openRequestItems.remove(position);
                            adapter.notifyItemRemoved(position);
                            checkForEmptyLayout();
                            return null;
                        },
                        err -> {
                            showGenericError();
                            Log.e(TAG, "onClickAccept: " + ApiRequestHandler.parseVolleyError(err));
                            return null;
                        },
                        new JSONObject());
            }
        });

        adapter.setOnItemClickListener(position -> {
            Bundle args = new Bundle();
            args.putLong("id", openRequestItems.get(position).getAccountId());
            args.putInt("score", openRequestItems.get(position).getMatchingPercent());
            args.putLong("relationshipId", openRequestItems.get(position).getId());
            args.putString("title", openRequestItems.get(position).getTitle());
            if (openRequestItems.get(position).isStartup()) {
                Fragment fragment = new StartupPublicProfileFragment();
                fragment.setArguments(args);
                changeFragment(fragment, "StartupPublicProfileFragment");
            } else {
                Fragment fragment = new InvestorPublicProfileFragment();
                fragment.setArguments(args);
                changeFragment(fragment, "InvestorPublicProfileFragment");
            }
        });
    }

    private void checkForEmptyLayout() {
        if (openRequestItems == null || openRequestItems.size() == 0) {
            popCurrentFragment(this);
        }
    }
}
