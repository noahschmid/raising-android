package com.raising.app.fragments.leads;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.ContactData;
import com.raising.app.models.ViewState;
import com.raising.app.models.leads.Interaction;
import com.raising.app.models.leads.InteractionState;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.ContactDataHandler;
import com.raising.app.util.LeadsInteraction;
import com.raising.app.viewModels.LeadsViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LeadsInteractionFragment extends RaisingFragment {
    private long id;
    Lead contact;
    private List<LeadsInteraction> interactions = new ArrayList<>();
    private Button closeContact;

    private final String TAG = "LeadsInteractionFragment";
    private boolean disableContact, declinedContact;
    private long leadId;

    private LeadsViewModel leadsViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_contact), true);
        leadsViewModel = ViewModelProviders.of(getActivity()).get(LeadsViewModel.class);
        leadsViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
          if(state == ViewState.RESULT || state == ViewState.CACHED) {
              processLeads();
          }
        });

        if(leadsViewModel.getViewState().getValue() == ViewState.RESULT ||
                leadsViewModel.getViewState().getValue() == ViewState.CACHED) {
            processLeads();
        }

        return inflater.inflate(R.layout.fragment_leads_interaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeContact = view.findViewById(R.id.button_close_contact);

        if (getArguments() != null) {
            leadId = getArguments().getLong("leadId");

            if (getArguments().getBoolean("disableContact")) {
                disableContact = true;
            }
            if (getArguments().getBoolean("declinedContact")) {
                declinedContact = true;
            }
        }
    }

    protected void processLeads() {
        View view = getView();

        for(Lead lead : leadsViewModel.getLeads().getValue()) {
            if(lead.getId() == leadId) {
                contact = lead;
                break;
            }
        }

        if(contact == null)
            return;

        if(contact.getHandshakeState() != InteractionState.HANDSHAKE) {
            closeContact.setVisibility(View.GONE);
        }

        LinearLayout layout = view.findViewById(R.id.leads_contact_items_layout);
        layout.removeAllViewsInLayout();
        interactions.clear();
        contact.getInteractions().forEach(interaction -> {
            interactions.add(new LeadsInteraction(interaction, layout, getActivity(), contact));
        });

        ConstraintLayout blurOverlay = view.findViewById(R.id.leads_contact_blur_overlay);
        blurOverlay.setVisibility(View.GONE);
        if (disableContact) {
            showSimpleDialog(getString(R.string.leads_contact_disabled_contact_title), getString(R.string.leads_contact_disabled_contact_text));
            blurOverlay.setVisibility(View.VISIBLE);
            interactions.forEach(interaction -> {
                interaction.enableButton(false);
            });
        }

        if (declinedContact) {
            showSimpleDialog(getString(R.string.leads_contact_disabled_contact_title), getString(R.string.leads_contact_declined_contact_text));
            blurOverlay.setVisibility(View.VISIBLE);
            interactions.forEach(interaction -> {
                interaction.enableButton(false);
            });
        }

        Fragment fragment = this;
        if(!disableContact && !declinedContact) {
            closeContact.setOnClickListener(v -> {
                String endpoint = "match/" + contact.getId() + "/decline";

                ApiRequestHandler.performPostRequest(endpoint,
                        res -> {
                            popCurrentFragment();
                            return null;
                        },
                        err -> {
                            Log.e(TAG, "updateRemoteInteraction: " + ApiRequestHandler.parseVolleyError(err));
                            showInformationToast("Action failed");
                            return null;
                        },
                        new JSONObject());
            });
        }
    }

    public void enterInteractionExchange(Interaction interaction) {
        Bundle args = new Bundle();
        args.putLong("id", contact.getId());
        args.putSerializable("contactType", interaction.getInteractionType());
        Fragment fragment = new LeadsContactExchangeFragment();
        fragment.setArguments(args);
        changeFragment(fragment, "LeadContactExchangeFragment");
    }
}
