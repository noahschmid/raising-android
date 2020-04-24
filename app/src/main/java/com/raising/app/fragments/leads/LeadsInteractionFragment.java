package com.raising.app.fragments.leads;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.leads.Interaction;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.LeadsInteraction;

import java.util.ArrayList;
import java.util.List;


public class LeadsInteractionFragment extends RaisingFragment {
    private long id;
    Lead contact;
    private List<LeadsInteraction> interactions = new ArrayList<>();

    private boolean disableContact, declinedContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customizeAppBar(getString(R.string.toolbar_title_contact), true);
        return inflater.inflate(R.layout.fragment_leads_interaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            contact = (Lead) getArguments().getSerializable("lead");

            if (getArguments().getBoolean("disableContact")) {
                disableContact = true;
            }
            if (getArguments().getBoolean("declinedContact")) {
                declinedContact = true;
            }
        }

        LinearLayout layout = view.findViewById(R.id.leads_contact_items_layout);
        contact.getInteractions().forEach(interaction -> {
            interactions.add(new LeadsInteraction(interaction, layout, getActivity(), contact));
        });

        TextView disableContactText = view.findViewById(R.id.leads_contact_disabled_text);
        ConstraintLayout blurOverlay = view.findViewById(R.id.leads_contact_blur_overlay);
        blurOverlay.setVisibility(View.GONE);
        if (disableContact) {
            disableContactText.setText(getString(R.string.leads_contact_disabled_contact_text));
            blurOverlay.setVisibility(View.VISIBLE);
            interactions.forEach(interaction -> {
                interaction.enableButton(false);
            });
        }

        if (declinedContact) {
            disableContactText.setText(getString(R.string.leads_contact_declined_contact_text));
            blurOverlay.setVisibility(View.VISIBLE);
            interactions.forEach(interaction -> {
                interaction.enableButton(false);
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
