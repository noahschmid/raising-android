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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Startup;
import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.util.StakeholderRecyclerViewAdapter;
import com.raising.app.util.StartupProfileBoardMemberRecyclerViewAdapter;
import com.raising.app.util.StartupProfileFounderRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class StartupPublicProfileFragment extends RaisingFragment {
    private Startup profileStartup;
    private ImageSwitcher imageSwitcher;
    private TextView imageIndex, matchingPercent, profileName, profileLocation, profilePitch;
    private Button profileAccept, profileDecline, profileWebsite;

    private TextView startupRevenue, startupBreakEven, startupFoundingYear, startupMarkets, startupFte,
            startupInvestmentType, startupValuation, startupClosingTime, startupCompleted;

    // this is a placeholder array with image resources, replace with actual images
    int[] images = {R.drawable.ic_person_24dp,
            R.drawable.ic_edit_blue_32dp,
            R.drawable.ic_trash_can_red_32dp};
    int currentImageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startup_public_profile,
                container, false);

        // TODO: store this profiles startup with the following line
        // profileStartup = (Startup) getArguments().get("startup");

        /*
        Fragment matchingFragment = new RegisterStartupMatchingFragment();
        Bundle args = new Bundle();
        //TODO: add all data for matching fragment to args bundle
        args.putBoolean("isProfileMatching", true);

        matchingFragment.setArguments(args);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.startup_profile_matching, matchingFragment, "StartupProfileMatching");
        */
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageIndex = view.findViewById(R.id.text_startup_profile_gallery_image_index);

        prepareImageSwitcher(view);

        matchingPercent = view.findViewById(R.id.text_startup_public_profile_matching_percent);
        profileName = view.findViewById(R.id.text_startup_public_profile_name);
        profileLocation = view.findViewById(R.id.text_startup_public_profile_location);
        profilePitch = view.findViewById(R.id.text_startup_public_profile_pitch);
        //TODO: fill texts with startup data

        profileAccept = view.findViewById(R.id.button_startup_public_profile_accept);
        profileDecline = view.findViewById(R.id.button_startup_public_profile_decline);
        profileWebsite = view.findViewById(R.id.button_startup_public_profile_website);
        profileWebsite.setOnClickListener(v -> {
            //TODO: replace with actual website
            String website = "";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
            startActivity(browserIntent);
        });

        startupRevenue = view.findViewById(R.id.text_profile_revenue_title);
        startupBreakEven = view.findViewById(R.id.text_profile_breakeven_title);
        startupFoundingYear = view.findViewById(R.id.text_profile_founding_year_title);
        startupMarkets = view.findViewById(R.id.text_profile_current_markets_title);
        startupFte = view.findViewById(R.id.text_profile_fte_title);

        startupInvestmentType = view.findViewById(R.id.text_profile_investment_type_title);
        startupValuation = view.findViewById(R.id.text_profile_valuation_title);
        startupClosingTime = view.findViewById(R.id.text_profile_closing_time_title);
        startupCompleted = view.findViewById(R.id.text_profile_completed_title);
        //TODO: fill texts with startup data

        ProgressBar completed = view.findViewById(R.id.progress_profile_completed);
        completed.setMax(getResources().getInteger(R.integer.maxPercent));
        //TODO: set actual progress
        completed.setProgress(75);

        //TODO: fill array list with founder
        ArrayList<Founder> founderList = new ArrayList<>();
        RecyclerView founderRecyclerView = view.findViewById(R.id.startup_profile_founder_list);
        founderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        StartupProfileFounderRecyclerViewAdapter founderListAdapter
                = new StartupProfileFounderRecyclerViewAdapter(founderList);
        founderRecyclerView.setAdapter(founderListAdapter);

        //TODO: fill array list with board member
        ArrayList<BoardMember> boardMemberList = new ArrayList<>();
        RecyclerView boardMemberRecyclerView = view.findViewById(R.id.startup_profile_board_member_list);
        boardMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        StartupProfileBoardMemberRecyclerViewAdapter boardMemberListAdapter
                = new StartupProfileBoardMemberRecyclerViewAdapter(boardMemberList);
        boardMemberRecyclerView.setAdapter(boardMemberListAdapter);
    }

    /**
     * Prepares the image switcher for usage, also set onClickListeners to previous and next buttons
     * @param view The view in which the image switcher lies
     */
    private void prepareImageSwitcher(View view) {
        imageSwitcher = view.findViewById(R.id.startup_public_profile_gallery);
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
        ImageButton btnPrevious = view.findViewById(R.id.button_startup_gallery_previous);
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
        ImageButton btnNext = view.findViewById(R.id.button_startup_gallery_next);
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
