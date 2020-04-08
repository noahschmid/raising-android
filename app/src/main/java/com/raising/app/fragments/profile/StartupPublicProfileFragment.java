package com.raising.app.fragments.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Investor;
import com.raising.app.models.Model;
import com.raising.app.models.Startup;
import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.util.AccountService;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.ResourcesManager;
import com.raising.app.util.recyclerViewAdapter.PublicProfileMatchingRecyclerViewAdapter;
import com.raising.app.util.recyclerViewAdapter.StartupProfileBoardMemberRecyclerViewAdapter;
import com.raising.app.util.recyclerViewAdapter.StartupProfileFounderRecyclerViewAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class StartupPublicProfileFragment extends RaisingFragment {
    private Startup profileStartup;
    private ImageSwitcher imageSwitcher;
    private ImageButton profileAccept, profileDecline;
    private TextView imageIndex, matchingPercent, profileName, profileLocation, profilePitch, profileWebsite;
    private TextView scope, minTicket, maxTicket;
    private RecyclerView recyclerInvestorType, recyclerPhase, recyclerIndustry, recyclerInvolvement;
    private ArrayList<Model> investorTypes, industries, investmentPhases, supports;
    private PublicProfileMatchingRecyclerViewAdapter typeAdapter, industryAdapter, phaseAdapter,
            supportAdapter;
    private TextView startupRevenue, startupBreakEven, startupFoundingYear, startupMarkets, startupFte,
            startupInvestmentType, startupValuation, startupClosingTime, startupCompleted;

    private Startup startup;

    ArrayList<Bitmap> pictures;
    private int currentImageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO: store this profiles startup with the following line
        View view =  inflater.inflate(R.layout.fragment_startup_public_profile,
                container, false);

        if(getArguments() == null) {
            return view;
        }

        if(getArguments().getSerializable("startup") != null) {
            Log.d("StartupPublicProfile", "name: " + ((Startup)getArguments()
                    .getSerializable("startup")).getName());
            startup = (Startup) getArguments().getSerializable("startup");
        } else {
            AccountService.getStartupAccount(getArguments().getLong("id"), startup -> {
                this.startup = startup;
                loadData();
                return null;
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageIndex = view.findViewById(R.id.text_startup_profile_gallery_image_index);

        prepareImageSwitcher(view);

        profileAccept = view.findViewById(R.id.button_startup_public_profile_accept);
        profileDecline = view.findViewById(R.id.button_startup_public_profile_decline);

        // setup general startup information
        matchingPercent = view.findViewById(R.id.text_startup_public_profile_matching_percent);
        profileName = view.findViewById(R.id.text_startup_public_profile_name);
        profileLocation = view.findViewById(R.id.text_startup_public_profile_location);
        profilePitch = view.findViewById(R.id.text_startup_public_profile_pitch);
        //TODO: fill texts with startup data

        profileWebsite = view.findViewById(R.id.text_startup_public_profile_website);
        //TODO:  if(startup.getWebsite() == 0) { profileWebsite.setVisibility(VIEW:GONE); }
        profileWebsite.setOnClickListener(v -> {
            //TODO: replace with actual website
            String website = "";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
            startActivity(browserIntent);
        });

        // setup matching criteria
        scope = view.findViewById(R.id.text_startup_public_profile_actual_scope);
        minTicket = view.findViewById(R.id.text_startup_public_profile_min_ticket);
        maxTicket = view.findViewById(R.id.text_startup_public_profile_max_ticket);


        initRecyclerViews(view);

        // setup startup specific information
        startupRevenue = view.findViewById(R.id.text_profile_revenue_title);
        startupBreakEven = view.findViewById(R.id.text_profile_breakeven_title);
        startupFoundingYear = view.findViewById(R.id.text_profile_founding_year_title);
        startupMarkets = view.findViewById(R.id.text_profile_current_markets_title);
        startupFte = view.findViewById(R.id.text_profile_fte_title);

        this.startupInvestmentType = view.findViewById(R.id.text_profile_investment_type_title);
        startupValuation = view.findViewById(R.id.text_profile_valuation_title);
        startupClosingTime = view.findViewById(R.id.text_profile_closing_time_title);
        startupCompleted = view.findViewById(R.id.text_profile_completed_title);
        //TODO: fill texts with startup data

        ProgressBar completed = view.findViewById(R.id.progress_profile_completed);
        completed.setMax(getResources().getInteger(R.integer.maxPercent));
        //TODO: set actual progress
        completed.setProgress(75);

        // setup recycler views for founders and board members
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

        setupShareholderPieChart(view);

        if(startup != null) {
            loadData();
        }
    }

    private void loadData() {
        minTicket.setText(ResourcesManager.getTicketSize(startup.getTicketMinId())
                .toString(getString(R.string.currency),
                        getResources().getStringArray(R.array.revenue_units)));
        maxTicket.setText(ResourcesManager.getTicketSize(startup.getTicketMaxId())
                .toString(getString(R.string.currency),
                        getResources().getStringArray(R.array.revenue_units)));
        profileName.setText(startup.getName());
        profilePitch.setText(startup.getPitch());
        startupRevenue.setText(ResourcesManager.getRevenueString(
                startup.getRevenueMinId()));
        startupBreakEven.setText(String.valueOf(startup.getBreakEvenYear()));
        startupFoundingYear.setText(String.valueOf(startup.getFoundingYear()));
        startupFte.setText(String.valueOf(startup.getNumberOfFte()));
        startupInvestmentType.setText(ResourcesManager.getFinanceType(
                startup.getFinanceTypeId()).getName());
        DateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        Date date = new Date(startup.getClosingTime());
        startupClosingTime.setText(formatter.format(date));
        startupCompleted.setText(String.valueOf(startup.getRaised()));

        //TODO: change to actual value
        matchingPercent.setText("80% MATCH");

        startup.getInvestorTypes().forEach(type -> {
            investorTypes.add((Model)ResourcesManager.getInvestorType(type));
        });

        startup.getIndustries().forEach(industry -> {
            industries.add(ResourcesManager.getIndustry(industry));
        });

        investmentPhases.add(ResourcesManager.getInvestmentPhase(startup.getInvestmentPhaseId()));
        startup.getSupport().forEach(support -> {
            supports.add(ResourcesManager.getSupport(support));
        });

        typeAdapter.notifyDataSetChanged();
        phaseAdapter.notifyDataSetChanged();
        industryAdapter.notifyDataSetChanged();
        supportAdapter.notifyDataSetChanged();

        if(startup.getWebsite() == null) {
            profileWebsite.setVisibility(View.GONE);
        }

        if(startup.getProfilePicture() != null) {
            pictures.add(startup.getProfilePicture().getBitmap());
        }

        if(startup.getGallery() != null) {
            startup.getGallery().forEach(image -> {
                pictures.add(image.getBitmap());
            });
        }
    }

    private void initRecyclerViews(View view) {
        investorTypes = new ArrayList<Model>();
        typeAdapter = new PublicProfileMatchingRecyclerViewAdapter(investorTypes);
        recyclerInvestorType = view.findViewById(R.id.startup_public_profile_investor_type_list);
        recyclerInvestorType.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerInvestorType.setAdapter(typeAdapter);

        investmentPhases = new ArrayList<Model>();
        phaseAdapter = new PublicProfileMatchingRecyclerViewAdapter(investmentPhases);
        recyclerPhase = view.findViewById(R.id.startup_public_profile_phase_list);
        recyclerPhase.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerPhase.setAdapter(phaseAdapter);

        industries = new ArrayList<Model>();
        industryAdapter = new PublicProfileMatchingRecyclerViewAdapter(industries);
        recyclerIndustry = view.findViewById(R.id.startup_public_profile_industry_list);
        recyclerIndustry.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerIndustry.setAdapter(industryAdapter);

        supports = new ArrayList<Model>();
        supportAdapter = new PublicProfileMatchingRecyclerViewAdapter(supports);
        recyclerInvolvement = view.findViewById(R.id.startup_public_profile_involvement_list);
        recyclerInvolvement.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerInvolvement.setAdapter(supportAdapter);
    }

    /**
     * Prepares the image switcher for usage, also set onClickListeners to previous and next buttons
     * @param view The view in which the image switcher lies
     */
    private void prepareImageSwitcher(View view) {
        pictures = new ArrayList<>();
        imageSwitcher = view.findViewById(R.id.startup_public_profile_gallery);
        imageSwitcher.setFactory(() -> {
            ImageView imageView = new ImageView(
                    Objects.requireNonNull(getActivity()).getApplicationContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            if(pictures.size() == 0) {
                imageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_person_24dp));
            } else {
                imageView.setImageBitmap(pictures.get(currentImageIndex));
            }
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
        ImageButton btnNext = view.findViewById(R.id.button_startup_gallery_next);

        if(currentImageIndex == 0) {
            btnPrevious.setVisibility(View.GONE);
        }
        if(pictures.size() < 2) {
            btnNext.setVisibility(View.GONE);
        }
        btnPrevious.setOnClickListener(v -> {
            if(currentImageIndex == 0) {
                btnPrevious.setVisibility(View.GONE);
            }

            if(currentImageIndex < pictures.size() - 1) {
                btnNext.setVisibility(View.VISIBLE);
            }

            if(pictures.size() == 0)
                return;
            currentImageIndex--;
            if(currentImageIndex == -1) {
                currentImageIndex = pictures.size() - 1;
            }
            imageSwitcher.setImageDrawable(new
                    BitmapDrawable(pictures.get(currentImageIndex)));
            imageIndex.setText(currentIndexToString(currentImageIndex));
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentImageIndex == pictures.size() - 1) {
                    btnNext.setVisibility(View.GONE);
                }

                if(currentImageIndex > 0) {
                    btnPrevious.setVisibility(View.VISIBLE);
                }

                if(pictures.size() == 0)
                    return;
                currentImageIndex ++;
                if (currentImageIndex == pictures.size()) {
                    currentImageIndex = 0;
                }
                imageSwitcher.setImageDrawable(new
                        BitmapDrawable(pictures.get(currentImageIndex)));

                imageIndex.setText(currentIndexToString(currentImageIndex));
            }
        });
    }

    private void setupShareholderPieChart(View view) {
        PieChart pieChart = view.findViewById(R.id.stakeholder_equity_chart);
        List<PieEntry> pieEntries = new ArrayList<>();

        //TODO: store shareholders in this array list
        ArrayList<Shareholder> shareholders = new ArrayList<>();

        //add all shareholders to pieEntries list
        for (Shareholder shareholder : shareholders) {
            // extract the chart name and value for every shareholder
            /*
            String investorType = ResourcesManager.getInvestorType(shareholder.getInvestortypeId()).toString();
            String chartTitle = shareholder.getTitle() + ", " + investorType;
            String equityShareString = shareholder.getEquityShare();
            float equityShare = Float.parseFloat(equityShareString.substring(0, equityShareString.length() - 2));
            pieEntries.add(new PieEntry(equityShare, chartTitle));

             */
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.startup_shareholder_chart_title));
        pieDataSet.setColors(populateColorArray());
        pieDataSet.setSliceSpace(1f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setHoleRadius(0f);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    /**
     * Add colors to the pieChartColors array used for the shareholder pie chart
     */
    private ArrayList<Integer> populateColorArray() {
        ArrayList<Integer> pieChartColors = new ArrayList<>();
        pieChartColors.add(getResources().getColor(R.color.raisingPrimary, null));
        pieChartColors.add(getResources().getColor(R.color.raisingPrimaryLight, null));
        pieChartColors.add(getResources().getColor(R.color.raisingPrimaryDark, null));
        pieChartColors.add(getResources().getColor(R.color.raisingPrimaryAccent, null));
        pieChartColors.add(getResources().getColor(R.color.raisingPrimaryTextColor, null));
        pieChartColors.add(getResources().getColor(R.color.raisingPrimaryButton, null));

        return pieChartColors;
    }

    /**
     * Prepares a string representation of the current image index
     * @param index The current index
     * @return The string representation of the current index
     */
    private String currentIndexToString(int index) {
        if(pictures.size() == 0)
            return " ";
        return ((index + 1) + " / " + pictures.size());
    }
}
