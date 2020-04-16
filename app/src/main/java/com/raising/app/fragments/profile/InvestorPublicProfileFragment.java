package com.raising.app.fragments.profile;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Investor;
import com.raising.app.models.Model;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.recyclerViewAdapter.PublicProfileMatchingRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class InvestorPublicProfileFragment extends RaisingFragment {
    private TextView imageIndex, matchingPercent, profileName, profilePitch, profileSentence, profileWebsite;
    private TextView minTicketSize, maxTicketSize;
    private ImageButton profileRequest, profileDecline;
    private Investor investor;
    private ImageSwitcher imageSwitcher;
    private ArrayList<Model> investorTypes, industries, investmentPhases, supports;
    private PublicProfileMatchingRecyclerViewAdapter typeAdapter, industryAdapter, phaseAdapter,
            supportAdapter;

    private boolean handshakeRequest = false;
    private boolean handshakeDecline = false;

    private RecyclerView recyclerInvestorType, recyclerPhase, recyclerIndustry, recyclerInvolvement;

    ArrayList<Bitmap> pictures;
    private int currentImageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_investor_public_profile,
                container, false);

        if(getArguments() == null) {
            return view;
        }

        if(getArguments().getSerializable("investor") != null) {
            Log.d("InvestorPublicProfile", "name: " + ((Investor)getArguments()
                    .getSerializable("investor")).getName());
            investor = (Investor)getArguments().getSerializable("investor");
            // hide matching summary, if user accesses own public profile
            CardView matchingSummary = view.findViewById(R.id.investor_public_profile_matching_summary);
            matchingSummary.setVisibility(View.GONE);
        } else {
            AccountService.getInvestorAccount(getArguments().getLong("id"), investor -> {
                this.investor = investor;
                loadData(investor);
                return null;
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageIndex = view.findViewById(R.id.text_investor_profile_gallery_image_index);

        pictures = new ArrayList<Bitmap>();
        prepareImageSwitcher(view);

        profileRequest = view.findViewById(R.id.button_investor_public_profile_request);
        profileDecline = view.findViewById(R.id.button_investor_public_profile_decline);
        manageHandshakeButtons();

        // setup general investor information
        matchingPercent = view.findViewById(R.id.text_investor_public_profile_matching_percent);
        profileName = view.findViewById(R.id.text_investor_public_profile_name);
        profilePitch = view.findViewById(R.id.text_investor_public_profile_pitch);
        profileSentence = view.findViewById(R.id.text_investor_public_profile_sentence);

        profileWebsite = view.findViewById(R.id.button_investor_public_profile_website);
        profileWebsite.setOnClickListener(v -> {
            String website = investor.getWebsite();
            if(website.length() == 0)
                return;
            String uri = "http://" + website;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(browserIntent);
        });

        // setup matching criteria
        minTicketSize = view.findViewById(R.id.text_investor_public_profile_min_ticket);
        maxTicketSize = view.findViewById(R.id.text_investor_public_profile_max_ticket);

        initRecyclerViews(view);

        if(investor != null) {
            loadData(investor);
        }
    }

    private void initRecyclerViews(View view) {
        investorTypes = new ArrayList<Model>();
        typeAdapter = new PublicProfileMatchingRecyclerViewAdapter(investorTypes);
        recyclerInvestorType = view.findViewById(R.id.investor_public_profile_investor_type_list);
        recyclerInvestorType.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerInvestorType.setAdapter(typeAdapter);

        investmentPhases = new ArrayList<Model>();
        phaseAdapter = new PublicProfileMatchingRecyclerViewAdapter(investmentPhases);
        recyclerPhase = view.findViewById(R.id.investor_public_profile_phase_list);
        recyclerPhase.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerPhase.setAdapter(phaseAdapter);

        industries = new ArrayList<Model>();
        industryAdapter = new PublicProfileMatchingRecyclerViewAdapter(industries);
        recyclerIndustry = view.findViewById(R.id.investor_public_profile_industry_list);
        recyclerIndustry.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerIndustry.setAdapter(industryAdapter);

        supports = new ArrayList<Model>();
        supportAdapter = new PublicProfileMatchingRecyclerViewAdapter(supports);
        recyclerInvolvement = view.findViewById(R.id.investor_public_profile_involvement_list);
        recyclerInvolvement.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerInvolvement.setAdapter(supportAdapter);
    }

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
    private void toggleHandshakeButtonBackground(View button, int color) {
        Drawable drawable = button.getBackground();
        drawable = DrawableCompat.wrap(drawable);
        drawable.setTint(color);
        button.setBackground(drawable);
    }

    private void loadData(Investor investor) {
        minTicketSize.setText(resources.getTicketSize(investor.getTicketMinId())
                .toString(getString(R.string.currency),
                        getResources().getStringArray(R.array.revenue_units)));
        maxTicketSize.setText(resources.getTicketSize(investor.getTicketMaxId())
                .toString(getString(R.string.currency),
                        getResources().getStringArray(R.array.revenue_units)));
       profileName.setText(investor.getFirstName() + " " + investor.getLastName());
       profilePitch.setText(investor.getPitch());
       profileSentence.setText(investor.getDescription());
       //TODO: change to actual value
       matchingPercent.setText("80% MATCH");

       investorTypes.add((Model)resources.getInvestorType(investor.getInvestorTypeId()));
       investor.getIndustries().forEach(industry -> {
           industries.add(resources.getIndustry(industry));
       });
       investor.getInvestmentPhases().forEach(phase -> {
           investmentPhases.add(resources.getInvestmentPhase(phase));
       });
       investor.getSupport().forEach(support -> {
           supports.add(resources.getSupport(support));
       });
       typeAdapter.notifyDataSetChanged();
       phaseAdapter.notifyDataSetChanged();
       industryAdapter.notifyDataSetChanged();
       supportAdapter.notifyDataSetChanged();
       if(investor.getWebsite() == null || investor.getWebsite().equals("")) {
           profileWebsite.setVisibility(View.GONE);
       }

       if(investor.getProfilePicture() != null) {
           pictures.add(investor.getProfilePicture().getBitmap());
       }

       if(investor.getGallery() != null) {
           investor.getGallery().forEach(image -> {
               pictures.add(image.getBitmap());
           });
       }
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
            if(pictures.size() == 0) {
                imageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_person_24dp));
            } else {
                imageView.setImageBitmap(pictures.get(currentImageIndex));
            }
            /*

            if (investor.getProfilePictureId() != -1) {
                Glide
                        .with(this)
                        .load(ApiRequestHandler.getDomain() + "media/profilepicture/" +
                                investor.getProfilePictureId())
                        .centerCrop()
                        .placeholder(R.drawable.ic_person_24dp)
                        .into(imageView);
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
        ImageButton btnPrevious = view.findViewById(R.id.button_investor_gallery_previous);
        ImageButton btnNext = view.findViewById(R.id.button_investor_gallery_next);

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
