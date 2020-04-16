package com.raising.app.fragments.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.flexbox.FlexboxLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Model;
import com.raising.app.models.EquityChartLegendItem;
import com.raising.app.models.Startup;
import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.recyclerViewAdapter.PublicProfileMatchingRecyclerViewAdapter;
import com.raising.app.util.recyclerViewAdapter.StartupProfileBoardMemberRecyclerViewAdapter;
import com.raising.app.util.recyclerViewAdapter.StartupProfileFounderRecyclerViewAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class StartupPublicProfileFragment extends RaisingFragment {
    private ImageSwitcher imageSwitcher;
    private ImageButton profileRequest, profileDecline;
    private TextView imageIndex, matchingPercent, profileName, profileLocation, profileSentence,
            profilePitch, profileWebsite;
    private LinearLayout labelsLayout;
    private TextView startupScope, startupMinTicket, startupMaxTicket;
    private RecyclerView recyclerInvestorType, recyclerPhase, recyclerIndustry, recyclerInvolvement;
    private ArrayList<Model> investorTypes, industries, investmentPhases, supports;
    private PublicProfileMatchingRecyclerViewAdapter typeAdapter, industryAdapter, phaseAdapter,
            supportAdapter;
    private TextView startupRevenue, startupBreakEven, startupFoundingYear, startupMarkets, startupFte,
            startupInvestmentType, startupValuation, startupValuationTitle, startupClosingTime, startupCompleted;
    private ProgressBar completedProgress;

    private boolean handshakeRequest = false;
    private boolean handshakeDecline = false;

    private int matchScore;

    private LayoutInflater inflater;

    private Startup startup;

    ArrayList<Bitmap> pictures;
    private int currentImageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_startup_public_profile,
                container, false);

        if (getArguments() == null) {
            return view;
        }

        if (getArguments().getSerializable("startup") != null) {
            Log.d("StartupPublicProfile", "name: " + ((Startup) getArguments()
                    .getSerializable("startup")).getName());
            startup = (Startup) getArguments().getSerializable("startup");
            Log.i("startup", startup.toString());
            // hide matching summary, if user accesses own public profile
            CardView matchingSummary = view.findViewById(R.id.startup_public_profile_matching_summary);
            matchingSummary.setVisibility(View.GONE);
        } else {
            AccountService.getStartupAccount(getArguments().getLong("id"), startup -> {
                matchScore = getArguments().getInt("score");
                customizeAppBar(getArguments().getString("title"), true);
                this.startup = startup;
                Log.i("startup", startup.toString());
                loadData();
                return null;
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        imageIndex = view.findViewById(R.id.text_startup_profile_gallery_image_index);

        prepareImageSwitcher(view);

        profileRequest = view.findViewById(R.id.button_startup_public_profile_accept);
        profileDecline = view.findViewById(R.id.button_startup_public_profile_decline);
        manageHandshakeButtons();

        // link general startup information
        matchingPercent = view.findViewById(R.id.text_startup_public_profile_matching_percent);
        profileName = view.findViewById(R.id.text_startup_public_profile_name);
        labelsLayout = view.findViewById(R.id.layout_startup_public_profile_labels);
        profileLocation = view.findViewById(R.id.text_startup_public_profile_location);
        profileSentence = view.findViewById(R.id.text_startup_public_profile_sentence);
        profilePitch = view.findViewById(R.id.text_startup_public_profile_pitch);

        profileWebsite = view.findViewById(R.id.text_startup_public_profile_website);
        profileWebsite.setOnClickListener(v -> {
            String website = startup.getWebsite();
            if (website.length() == 0)
                return;
            String uri = "http://" + website;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(browserIntent);
        });

        initRecyclerViews(view);

        // link startup specific information
        startupRevenue = view.findViewById(R.id.text_profile_revenue);
        startupBreakEven = view.findViewById(R.id.text_profile_breakeven);
        startupFoundingYear = view.findViewById(R.id.text_profile_founding_year);
        startupMarkets = view.findViewById(R.id.text_profile_current_markets);
        startupFte = view.findViewById(R.id.text_profile_fte);

        this.startupInvestmentType = view.findViewById(R.id.text_profile_investment_type);
        startupValuation = view.findViewById(R.id.text_profile_valuation);
        startupValuationTitle = view.findViewById(R.id.text_profile_valuation_title);
        startupScope = view.findViewById(R.id.text_startup_public_profile_actual_scope);
        startupMinTicket = view.findViewById(R.id.text_startup_public_profile_min_ticket);
        startupMaxTicket = view.findViewById(R.id.text_startup_public_profile_max_ticket);
        startupClosingTime = view.findViewById(R.id.text_profile_closing_time);
        startupCompleted = view.findViewById(R.id.text_profile_completed);

        completedProgress = view.findViewById(R.id.progress_profile_completed);

        // setup recycler view for founders
        ArrayList<Founder> founderList = new ArrayList<>();
        founderList.addAll(startup.getFounders());
        RecyclerView founderRecyclerView = view.findViewById(R.id.startup_profile_founder_list);
        founderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        StartupProfileFounderRecyclerViewAdapter founderListAdapter
                = new StartupProfileFounderRecyclerViewAdapter(founderList);
        founderRecyclerView.setAdapter(founderListAdapter);

        // setup recycler view for board members
        ArrayList<BoardMember> boardMemberList = new ArrayList<>(startup.getBoardMembers());
        RecyclerView boardMemberRecyclerView = view.findViewById(R.id.startup_profile_board_member_list);
        boardMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        StartupProfileBoardMemberRecyclerViewAdapter boardMemberListAdapter
                = new StartupProfileBoardMemberRecyclerViewAdapter(boardMemberList);
        boardMemberRecyclerView.setAdapter(boardMemberListAdapter);

        // check if startup has shareholders, if true load pie chart, if false, hide all views
        if(startup.getCorporateShareholders().size() == 0
                && startup.getPrivateShareholders().size() == 0) {
            view.findViewById(R.id.text_profile_shareholder).setVisibility(View.GONE);
            view.findViewById(R.id.stakeholder_equity_chart).setVisibility(View.GONE);
            view.findViewById(R.id.stakeholder_equity_chart_legend).setVisibility(View.GONE);
        } else {
            setupShareholderPieChart(view);
        }

        if (startup != null) {
            loadData();
        }

        // hide current markets, until a good layout was found
        TextView marketsTitle = view.findViewById(R.id.text_profile_current_markets_title);
        marketsTitle.setVisibility(View.GONE);
        startupMarkets.setVisibility(View.GONE);
    }

    /**
     * Load the startups data into the different views
     */
    private void loadData() {
        startupScope.setText(resources.formatMoneyAmount(startup.getScope()));
        startupMinTicket.setText(resources.getTicketSize(startup.getTicketMinId())
                .toString(getString(R.string.currency),
                        getResources().getStringArray(R.array.revenue_units)));
        startupMaxTicket.setText(resources.getTicketSize(startup.getTicketMaxId())
                .toString(getString(R.string.currency),
                        getResources().getStringArray(R.array.revenue_units)));
        profileName.setText(startup.getCompanyName());
        profileSentence.setText(startup.getDescription());
        profilePitch.setText(startup.getPitch());
        startupRevenue.setText(resources.getRevenueString(
                startup.getRevenueMinId()));
        startupBreakEven.setText(String.valueOf(startup.getBreakEvenYear()));
        startupFoundingYear.setText(String.valueOf(startup.getFoundingYear()));
        startupFte.setText(String.valueOf(startup.getNumberOfFte()));

        customizeAppBar(startup.getCompanyName(), true);

        startupInvestmentType.setText(resources.getFinanceType(
                startup.getFinanceTypeId()).getName());
        DateFormat toFormat = new SimpleDateFormat("MM.dd.yyyy");
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date closing = fromFormat.parse(startup.getClosingTime());
            startupClosingTime.setText(toFormat.format(closing));
        } catch (ParseException e) {
            startupClosingTime.setText("");
            Log.e("StartupPublicProfile", "Error while parsing closing time: " +
                    e.getMessage());
        }
        startupCompleted.setText(resources.formatMoneyAmount(startup.getRaised()));
        completedProgress.setMax(startup.getScope());
        completedProgress.setProgress(startup.getRaised());
        profileLocation.setText(resources.getCountry(startup.getCountryId()).getName());

        String matchingScore = matchScore + "% " + "Match";
        matchingPercent.setText(matchingScore);

        //TODO: add labels to labelsLayout

        startup.getInvestorTypes().forEach(type -> {
            investorTypes.add((Model) resources.getInvestorType(type));
        });

        startup.getIndustries().forEach(industry -> {
            industries.add(resources.getIndustry(industry));
        });

        investmentPhases.add(resources.getInvestmentPhase(startup.getInvestmentPhaseId()));
        startup.getSupport().forEach(support -> {
            supports.add(resources.getSupport(support));
        });

        typeAdapter.notifyDataSetChanged();
        phaseAdapter.notifyDataSetChanged();
        industryAdapter.notifyDataSetChanged();
        supportAdapter.notifyDataSetChanged();

        if (startup.getWebsite() == null || startup.getWebsite().equals("")) {
            profileWebsite.setVisibility(View.GONE);
        }

        if (startup.getGallery() != null) {
            startup.getGallery().forEach(image -> {
                pictures.add(image.getBitmap());
            });
        }

        // hide valuation fields, if they are empty
        if (startup.getPreMoneyValuation() > 0) {
            startupValuation.setText(resources.formatMoneyAmount(startup.getPreMoneyValuation()));
        } else {
            startupValuationTitle.setVisibility(View.GONE);
            startupValuation.setVisibility(View.GONE);
        }
    }

    /**
     * Prepare the recycler views used to display the matching criteria
     * @param view The view in which the recycler views will be displayed
     */
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
     * Set click listeners to the buttons which start the handshake process
     */
    private void manageHandshakeButtons() {
        profileRequest.setOnClickListener(v -> {
            handshakeRequest = true;
            handshakeDecline = false;
            //TODO: change handshake status in backend
            //TODO: remove investor from matchlist
            popCurrentFragment(this);
        });

        profileDecline.setOnClickListener(v -> {
            handshakeDecline = true;
            handshakeRequest = false;
            //TODO: change handshake status in backend
            //TODO: remove investor from matchlist
            popCurrentFragment(this);
        });
    }

    /**
     * Set the needed color for the handshake buttons
     * @param button The button where the color should change
     * @param color The new color of the button
     */
    private void colorHandshakeButtonBackground(View button, int color) {
        Drawable drawable = button.getBackground();
        drawable = DrawableCompat.wrap(drawable);
        drawable.setTint(color);
        button.setBackground(drawable);
    }

    /**
     * Prepares the image switcher for usage, also set onClickListeners to previous and next buttons
     *
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
            if (startup.getProfilePictureId() != -1) {
                Glide
                        .with(this)
                        .load(ApiRequestHandler.getDomain() + "media/profilepicture/" +
                                startup.getProfilePictureId())
                        .centerCrop()
                        .placeholder(R.drawable.ic_person_24dp)
                        .into(imageView);
            }

            /*
            if(pictures.size() == 0) {
                imageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_person_24dp));
            } else {
                imageView.setImageBitmap(pictures.get(currentImageIndex));
            }*/
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

        if (currentImageIndex == 0) {
            btnPrevious.setVisibility(View.GONE);
        }
        if (pictures.size() < 2) {
            btnNext.setVisibility(View.GONE);
        }
        btnPrevious.setOnClickListener(v -> {
            if (currentImageIndex == 0) {
                btnPrevious.setVisibility(View.GONE);
            }

            if (currentImageIndex < pictures.size() - 1) {
                btnNext.setVisibility(View.VISIBLE);
            }

            if (pictures.size() == 0)
                return;
            currentImageIndex--;
            if (currentImageIndex == -1) {
                currentImageIndex = pictures.size() - 1;
            }
            imageSwitcher.setImageDrawable(new
                    BitmapDrawable(pictures.get(currentImageIndex)));
            imageIndex.setText(currentIndexToString(currentImageIndex));
        });

        btnNext.setOnClickListener(v -> {
            if (currentImageIndex == pictures.size() - 1) {
                btnNext.setVisibility(View.GONE);
            }

            if (currentImageIndex > 0) {
                btnPrevious.setVisibility(View.VISIBLE);
            }

            if (pictures.size() == 0)
                return;
            currentImageIndex++;
            if (currentImageIndex == pictures.size()) {
                currentImageIndex = 0;
            }
            imageSwitcher.setImageDrawable(new
                    BitmapDrawable(pictures.get(currentImageIndex)));

            imageIndex.setText(currentIndexToString(currentImageIndex));
        });
    }

    /**
     * Initializes and styles the pie chart used to display all shareholders
     * @param view The view, in which the pie chart should be displayed
     */
    private void setupShareholderPieChart(View view) {
        //stores the colors used in the pie chart
        ArrayList<Integer> pieChartColors = populateColorArray();

        //stores the shareholders combined with their respective chart color
        ArrayList<EquityChartLegendItem> legendItems = new ArrayList<>();

        PieChart pieChart = view.findViewById(R.id.stakeholder_equity_chart);
        List<PieEntry> pieEntries = new ArrayList<>();

        ArrayList<Shareholder> shareholders = new ArrayList<>(startup.getPrivateShareholders());
        shareholders.addAll(startup.getCorporateShareholders());

        int offset = 0;
        for (int i = 0; i < shareholders.size(); i++) {
            int colorIndex = (i + offset) % (pieChartColors.size() - 1);
            if (i % shareholders.size() == 0 && i != 0) {
                offset = offset == 0 ? 1 : 0;
            }
            int shareholderColor = pieChartColors.get(colorIndex);
            Shareholder tmp = shareholders.get(i);
            if (tmp.isPrivateShareholder()) {
                legendItems.add(new EquityChartLegendItem(shareholderColor,
                        resources.getInvestorType(tmp.getInvestorTypeId()).getName(),
                        tmp.getTitle(), tmp.getEquityShare()));
            } else {
                legendItems.add(new EquityChartLegendItem(shareholderColor,
                        resources.getCorporateBody(tmp.getCorporateBodyId()).getName(),
                        tmp.getTitle(), tmp.getEquityShare()));
            }
        }

        legendItems.forEach(legendItem -> {
            pieEntries.add(new PieEntry(legendItem.getEquityShare(), legendItem.getEquityShareString()));

        });

        FlexboxLayout pieChartLegend = view.findViewById(R.id.stakeholder_equity_chart_legend);
        legendItems.forEach(legendItem -> {
            final View legendItemView = inflater.inflate(R.layout.item_startup_public_profile_equity_chart_legend, null);
            TextView title = legendItemView.findViewById(R.id.item_legend_title);
            title.setText(legendItem.getTitle());
            TextView type = legendItemView.findViewById(R.id.item_legend_investor_type);
            type.setText(legendItem.getTypeString());
            View colorView = legendItemView.findViewById(R.id.item_legend_color);
            colorView.setBackgroundColor(legendItem.getColor());

            pieChartLegend.addView(legendItemView);
        });

        PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.startup_shareholder_chart_title));
        pieDataSet.setColors(pieChartColors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(false);
        pieData.setValueTextColor(getResources().getColor(R.color.raisingWhite, null));

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setEntryLabelTextSize(16f);
        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.invalidate();
    }

    /**
     * Populates a color array, which is then used for the shareholder pie chart
     * @return An array of color integers
     */
    private ArrayList<Integer> populateColorArray() {
        ArrayList<Integer> pieChartColors = new ArrayList<>();
        pieChartColors.add(getResources().getColor(R.color.raisingPrimary, null));
        pieChartColors.add(getResources().getColor(R.color.raisingPrimaryLight, null));
        pieChartColors.add(getResources().getColor(R.color.raisingPrimaryDark, null));
        pieChartColors.add(getResources().getColor(R.color.raisingPrimaryAccent, null));
        pieChartColors.add(getResources().getColor(R.color.raisingTextColor, null));
        pieChartColors.add(getResources().getColor(R.color.raisingButtonBackgroundColor, null));

        return pieChartColors;
    }

    /**
     * Prepares a string representation of the current image index
     *
     * @param index The current index
     * @return The string representation of the current index
     */
    private String currentIndexToString(int index) {
        if (pictures.size() == 0)
            return " ";
        return ((index + 1) + " / " + pictures.size());
    }
}
