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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Investor;
import com.raising.app.models.Model;
import com.raising.app.util.AccountService;
import com.raising.app.util.recyclerViewAdapter.PublicProfileMatchingAdapter;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.viewModels.MatchesViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class InvestorPublicProfileFragment extends RaisingFragment {
    private static final String TAG = "InvestorPublicProfile";
    private TextView imageIndex, matchingPercent, profileName, profileLocation, profilePitch, profileSentence, profileWebsite;
    private TextView minTicketSize, maxTicketSize;
    private ImageView locationPin;
    private ImageButton profileRequest, profileDecline, btnPrevious, btnNext;
    private Investor investor;
    private ImageSwitcher imageSwitcher;
    private ArrayList<Model> investorTypes, industries, investmentPhases, supports;
    private PublicProfileMatchingAdapter typeAdapter, industryAdapter, phaseAdapter,
            supportAdapter;
    private ConstraintLayout profileLayout;
    private ScrollView scrollView;

    private boolean leadsRequest = false;
    private boolean leadsDecline = false;
    private int matchScore = 0;
    private long relationshipId = -1;

    private RecyclerView recyclerInvestorType, recyclerPhase, recyclerIndustry, recyclerInvolvement;

    ArrayList<Bitmap> pictures;
    private int currentImageIndex = 0;

    MatchesViewModel matchesViewModel;

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
            customizeAppBar(getString(R.string.toolbar_my_public_profile), true);
            // hide matching summary, if user accesses own public profile
            CardView matchingSummary = view.findViewById(R.id.investor_public_profile_matching_summary);
            matchingSummary.setVisibility(View.GONE);
        } else {
            AccountService.getInvestorAccount(getArguments().getLong("id"), investor -> {
                matchScore = getArguments().getInt("score");
                relationshipId = getArguments().getLong("relationshipId");
                customizeAppBar(getArguments().getString("title"), true);
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
        btnPrevious = view.findViewById(R.id.button_investor_gallery_previous);
        btnNext = view.findViewById(R.id.button_investor_gallery_next);
        imageIndex.setVisibility(View.GONE);
        btnPrevious.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        matchesViewModel = ViewModelProviders.of(getActivity())
                .get(MatchesViewModel.class);

        profileLayout = view.findViewById(R.id.profile_layout);
        profileLayout.setVisibility(View.INVISIBLE);
        pictures = new ArrayList<Bitmap>();
        scrollView = view.findViewById(R.id.scroll_layout);

        profileRequest = view.findViewById(R.id.button_investor_public_profile_request);
        profileDecline = view.findViewById(R.id.button_investor_public_profile_decline);

        // setup general investor information
        matchingPercent = view.findViewById(R.id.text_investor_public_profile_matching_percent);
        profileName = view.findViewById(R.id.text_investor_public_profile_name);
        profileLocation = view.findViewById(R.id.text_investor_public_profile_location);
        locationPin = view.findViewById(R.id.investor_public_profile_location_pin);
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

        Fragment fragment = this;
        profileRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leadsRequest = true;
                leadsDecline = false;

                colorHandshakeButtonBackground(profileRequest, R.color.raisingDarkGrey);

                ApiRequestHandler.performPostRequest("match/" + relationshipId + "/accept",
                        res -> {
                            matchesViewModel.removeMatch(relationshipId);
                            popCurrentFragment(fragment);
                            return null;
                        },
                        err -> {
                            displayGenericError();
                            Log.e(TAG, "manageHandshakeButtons: " +
                                    ApiRequestHandler.parseVolleyError(err) );
                            return null;
                        },
                        new JSONObject());
            }
        });

        profileDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leadsDecline = true;
                leadsRequest = false;

                colorHandshakeButtonBackground(profileDecline, R.color.raisingDarkGrey);

                ApiRequestHandler.performPostRequest("match/" + relationshipId + "/decline",
                        res -> {
                            matchesViewModel.removeMatch(relationshipId);
                            popCurrentFragment(fragment);
                            return null;
                        },
                        err -> {
                            displayGenericError();
                            Log.e(TAG, "manageHandshakeButtons: " +
                                    ApiRequestHandler.parseVolleyError(err) );
                            return null;
                        },
                        new JSONObject());
            }
        });
    }

    /**
     * Prepare the recycler views used to display the matching criteria
     * @param view The view in which the recycler views will be displayed
     */
    private void initRecyclerViews(View view) {
        investorTypes = new ArrayList<Model>();
        typeAdapter = new PublicProfileMatchingAdapter(investorTypes);
        recyclerInvestorType = view.findViewById(R.id.investor_public_profile_investor_type_list);
        recyclerInvestorType.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerInvestorType.setAdapter(typeAdapter);

        investmentPhases = new ArrayList<Model>();
        phaseAdapter = new PublicProfileMatchingAdapter(investmentPhases);
        recyclerPhase = view.findViewById(R.id.investor_public_profile_phase_list);
        recyclerPhase.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerPhase.setAdapter(phaseAdapter);

        industries = new ArrayList<Model>();
        industryAdapter = new PublicProfileMatchingAdapter(industries);
        recyclerIndustry = view.findViewById(R.id.investor_public_profile_industry_list);
        recyclerIndustry.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerIndustry.setAdapter(industryAdapter);

        supports = new ArrayList<Model>();
        supportAdapter = new PublicProfileMatchingAdapter(supports);
        recyclerInvolvement = view.findViewById(R.id.investor_public_profile_involvement_list);
        recyclerInvolvement.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerInvolvement.setAdapter(supportAdapter);
    }


    /**
     * Set the needed color for the leads buttons
     * @param button The button where the color should change
     * @param color The new color of the button
     */
    private void colorHandshakeButtonBackground(View button, int color) {
        Drawable drawable = button.getBackground();
        drawable = DrawableCompat.wrap(drawable);
        drawable.setTint(getResources().getColor(color));
        button.setBackground(drawable);
    }

    /**
     * Load the investors data into the different views
     */
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
       String matchingScore = matchScore + "% " + "Match";
       matchingPercent.setText(matchingScore);

       if(investor.getCountryId() > 0) {
           profileLocation.setText(resources.getCountry(investor.getCountryId()).getName());
       } else {
           profileLocation.setVisibility(View.GONE);
           locationPin.setVisibility(View.GONE);
       }

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

       /*
       if(investor.getProfilePicture() != null) {
           pictures.add(investor.getProfilePicture().getImage());
       }

       if(investor.getGallery() != null) {
           investor.getGallery().forEach(image -> {
               pictures.add(image.getImage());
           });
       }*/
        if(investor.getProfilePictureId() > 0) {
            Glide.with(this)
                    .asBitmap()
                    .load(ApiRequestHandler.getDomain() + "media/profilepicture/" +
                            investor.getProfilePictureId())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable
                                Transition<? super Bitmap> transition) {
                            pictures.add(0, resource);

                            if (pictures.size() == investor.getGalleryIds().size() + 1) {
                                prepareImageSwitcher(getView());
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }

       if(investor.getGalleryIds() != null) {
           investor.getGalleryIds().forEach(galleryId -> {
               if(galleryId > 0) {
                   Glide.with(this)
                           .asBitmap()
                           .load(ApiRequestHandler.getDomain() + "media/gallery/" +
                                   galleryId)
                           .diskCacheStrategy(DiskCacheStrategy.NONE)
                           .skipMemoryCache(true)
                           .placeholder(R.drawable.ic_hourglass_empty_black_24dp)
                           .into(new CustomTarget<Bitmap>() {
                               @Override
                               public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                   if (!pictures.isEmpty()) {
                                       pictures.add(1, resource);
                                   } else {
                                       pictures.add(resource);
                                   }

                                   if (pictures.size() == investor.getGalleryIds().size() + 1) {
                                       prepareImageSwitcher(getView());
                                   }
                               }

                               @Override
                               public void onLoadCleared(@Nullable Drawable placeholder) {
                               }
                           });
               }
           });
       }

       profileLayout.setVisibility(View.VISIBLE);
       scrollView.scrollTo(0,0);
       scrollView.smoothScrollTo(0, 0);

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
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            if(pictures.size() == 0) {
                imageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_person_24dp));
            } else {
                imageView.setImageBitmap(pictures.get(currentImageIndex));
                imageIndex.setVisibility(View.VISIBLE);
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

        if(currentImageIndex == 0) {
            btnPrevious.setVisibility(View.GONE);
        } else {
            btnPrevious.setVisibility(View.VISIBLE);
        }
        if(investor.getGalleryIds().size() + 1 < 2) {
            btnNext.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
        }

        btnPrevious.setOnClickListener(v -> {
            if(pictures.size() == 0)
                return;

            currentImageIndex--;

            if(currentImageIndex == 0) {
                btnPrevious.setVisibility(View.GONE);
            }

            if(currentImageIndex < pictures.size() - 1) {
                btnNext.setVisibility(View.VISIBLE);
            }

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
                if(pictures.size() == 0)
                    return;

                currentImageIndex ++;

                if(currentImageIndex == pictures.size() - 1) {
                    btnNext.setVisibility(View.GONE);
                }

                if(currentImageIndex > 0) {
                    btnPrevious.setVisibility(View.VISIBLE);
                }

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
        return ((index + 1) + "/" + pictures.size());
    }
}
