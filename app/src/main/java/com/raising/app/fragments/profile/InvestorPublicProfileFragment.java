package com.raising.app.fragments.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Investor;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.util.recyclerViewAdapter.PublicProfileMatchingRecyclerViewAdapter;
import com.raising.app.util.recyclerViewAdapter.StartupProfileFounderRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class InvestorPublicProfileFragment extends RaisingFragment {
    private TextView imageIndex, matchingPercent, profileName, profileLocation, profilePitch, profileWebsite;
    private TextView minTicketSize, maxTicketSize;
    private ImageButton profileAccept, profileDecline;
    private Investor profileInvestor;
    private ImageSwitcher imageSwitcher;

    private RecyclerView recyclerInvestorType, recyclerPhase, recyclerIndustry, recyclerInvolvement;

    // this is a placeholder array with image resources, replace with actual images
    private int[] images = {R.drawable.ic_person_24dp,
            R.drawable.ic_edit_blue_32dp,
            R.drawable.ic_trash_can_red_32dp};
    private int currentImageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_investor_public_profile,
                container, false);

        //TODO @noah : store investor for this profile in following line
        // profileInvestor = (Investor) getArguments().get("investor");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageIndex = view.findViewById(R.id.text_investor_profile_gallery_image_index);

        prepareImageSwitcher(view);

        profileAccept = view.findViewById(R.id.button_investor_public_profile_accept);
        profileDecline = view.findViewById(R.id.button_investor_public_profile_decline);

        // setup general investor information
        matchingPercent = view.findViewById(R.id.text_investor_public_profile_matching_percent);
        profileName = view.findViewById(R.id.text_investor_public_profile_name);
        profileLocation = view.findViewById(R.id.text_investor_public_profile_location);
        profilePitch = view.findViewById(R.id.text_investor_public_profile_pitch);
        //TODO: fill with investors data

        profileWebsite = view.findViewById(R.id.button_investor_public_profile_website);
        //TODO:  if(investor.getWebsite() == 0) { profileWebsite.setVisibility(VIEW:GONE); }
        profileWebsite.setOnClickListener(v -> {
            //TODO: replace with actual website
            String website = "https://www.google.com";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
            startActivity(browserIntent);
        });

        // setup matching criteria
        minTicketSize = view.findViewById(R.id.text_investor_public_profile_min_ticket);
        maxTicketSize = view.findViewById(R.id.text_investor_public_profile_max_ticket);
        //TODO: fill texts with investors data

        ArrayList investorsInvestmentType = new ArrayList();
        recyclerInvestorType = view.findViewById(R.id.investor_public_profile_investor_type_list);
        recyclerInvestorType.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerInvestorType.setAdapter(new PublicProfileMatchingRecyclerViewAdapter(investorsInvestmentType));

        ArrayList investorsPhase = new ArrayList();
        recyclerPhase = view.findViewById(R.id.investor_public_profile_phase_list);
        recyclerPhase.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerPhase.setAdapter(new PublicProfileMatchingRecyclerViewAdapter(investorsPhase));

        ArrayList investorsIndustries = new ArrayList();
        recyclerIndustry = view.findViewById(R.id.investor_public_profile_industry_list);
        recyclerIndustry.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerIndustry.setAdapter(new PublicProfileMatchingRecyclerViewAdapter(investorsIndustries));

        ArrayList investorsInvolvement = new ArrayList();
        recyclerInvolvement = view.findViewById(R.id.investor_public_profile_involvement_list);
        recyclerInvolvement.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerInvolvement.setAdapter(new PublicProfileMatchingRecyclerViewAdapter(investorsInvolvement));
    }

    /**
     * Prepares the image switcher for usage, also set onClickListeners to previous and next buttons
     * @param view The view in which the image switcher lies
     */
    private void prepareImageSwitcher(View view) {
        imageSwitcher = view.findViewById(R.id.investor_public_profile_gallery);
        imageSwitcher.setFactory(() -> {
            ImageView imageView = new ImageView(
                    Objects.requireNonNull(getActivity()).getApplicationContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setImageResource(images[currentImageIndex]);
            imageIndex.setText(currentIndexToString(currentImageIndex));
            return imageView;
        });

        //TODO @lorenz : add animation to imageswitcher
        /*
        imageSwitcher.setInAnimation(
                AnimationUtils.loadAnimation(this.getContext(), R.anim.public_profile_gallery_in));
        imageSwitcher.setOutAnimation(
                AnimationUtils.loadAnimation(this.getContext(), R.anim.public_profile_gallery_out));

         */
        ImageButton btnPrevious = view.findViewById(R.id.button_investor_gallery_previous);
        btnPrevious.setOnClickListener(v -> {
            currentImageIndex--;
            if(currentImageIndex == -1) {
                currentImageIndex = images.length - 1;
                imageSwitcher.setImageResource(images[currentImageIndex]);
            } else {
                imageSwitcher.setImageResource(images[currentImageIndex]);
            }
            imageIndex.setText(currentIndexToString(currentImageIndex));
        });
        ImageButton btnNext = view.findViewById(R.id.button_investor_gallery_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImageIndex ++;
                if (currentImageIndex == images.length){
                    currentImageIndex = 0;
                    imageSwitcher.setImageResource(images[currentImageIndex]);
                }
                else {
                    imageSwitcher.setImageResource(images[currentImageIndex]);
                }
                imageIndex.setText(currentIndexToString(currentImageIndex));
            }
        });
    }

    /**
     * Prepares a string representation of the current image index
     * @param index The current index
     * @return The string representation of the current index
     */
    private String currentIndexToString(int index) {
        return ((index + 1) + " / " + images.length);
    }
}
