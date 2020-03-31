package com.raising.app.authentication.fragments.profile;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.raising.app.R;
import com.raising.app.RaisingFragment;
import com.raising.app.models.Investor;
import com.raising.app.models.Startup;

public class InvestorPublicProfileFragment extends RaisingFragment {
    private Investor profileInvestor;

    private ImageSwitcher imageSwitcher;
    private ImageButton btnPrevious, btnNext;

    private Integer[] images = {R.drawable.ic_button_delete_red_32dp, R.drawable.ic_button_edit_blue_32dp, R.drawable.ic_menu_person_black_24dp};
    private int currentImageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_investor_public_profile,
                container, false);

        profileInvestor = (Investor) getArguments().get("startup");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageSwitcher = view.findViewById(R.id.investor_profile_gallery);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        imageSwitcher.setImageResource(images[currentImageIndex]);

        btnPrevious = view.findViewById(R.id.button_gallery_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentImageIndex > 0) {
                    currentImageIndex--;
                    imageSwitcher.setImageResource(images[currentImageIndex]);
                }

            }
        });
        btnNext = view.findViewById(R.id.button_gallery_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentImageIndex < images.length - 1) {
                    currentImageIndex++;
                    imageSwitcher.setImageResource(images[currentImageIndex]);
                }

            }
        });
    }
}
