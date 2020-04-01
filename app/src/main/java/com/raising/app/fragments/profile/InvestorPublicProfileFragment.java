package com.raising.app.fragments.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.investor.RegisterInvestorMatchingFragment;
import com.raising.app.models.Investor;

import java.util.Objects;

public class InvestorPublicProfileFragment extends RaisingFragment {
    TextView imageIndex, matchingPercent, profileName, profileLocation, profilePitch, profileWebsite;
    ImageButton profileAccept, profileDecline;
    Investor profileInvestor;
    ImageSwitcher imageSwitcher;

    // this is a placeholder array with image resources, replace with actual images
    int[] images = {R.drawable.ic_person_24dp,
            R.drawable.ic_edit_blue_32dp,
            R.drawable.ic_trash_can_red_32dp};
    int currentImageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_investor_public_profile,
                container, false);

        //TODO @noah : store investor for this profile in following line
        // profileInvestor = (Investor) getArguments().get("investor");

        /*
        Fragment matchingFragment = new RegisterInvestorMatchingFragment();

        Bundle args = new Bundle();
        //TODO: add all data for matching fragment to args bundle
        // args.putBoolean("isProfileMatching", true);

        // matchingFragment.setArguments(args);

        getChildFragmentManager().beginTransaction()
                .add(R.id.investor_matching_fragment_container, matchingFragment)
                .commit();
         */
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageIndex = view.findViewById(R.id.text_investor_profile_gallery_image_index);

        prepareImageSwitcher(view);

        matchingPercent = view.findViewById(R.id.text_investor_public_profile_matching_percent);
        profileName = view.findViewById(R.id.text_investor_public_profile_name);
        profileLocation = view.findViewById(R.id.text_investor_public_profile_location);
        profilePitch = view.findViewById(R.id.text_investor_public_profile_pitch);
        profileWebsite = view.findViewById(R.id.button_investor_public_profile_website);
        profileWebsite.setOnClickListener(v -> {
            //TODO: replace with actual website
            String website = "https://www.google.com";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
            startActivity(browserIntent);
        });
        //TODO: fill texts with investors data

        profileAccept = view.findViewById(R.id.button_investor_public_profile_accept);
        profileDecline = view.findViewById(R.id.button_investor_public_profile_decline);

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
