package com.raising.app.fragments.registration.startup;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Startup;
import com.raising.app.util.RegistrationHandler;

import java.util.Objects;

public class RegisterStartupVideoFragment extends RaisingFragment {
    static final int REQUEST_VIDEO_FETCH = 1;

    private ImageView videoOverlay;
    private Button deleteVideoPitchButton;
    private VideoView videoPitch;
    private Uri videoUri;
    private Startup startup;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_video,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_video), true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnStartupVideo = view.findViewById(R.id.button_startup_video);
        btnStartupVideo.setOnClickListener(v -> processInformation());

        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_images_progress).setVisibility(View.INVISIBLE);
            btnStartupVideo.setHint(getString(R.string.myProfile_apply_changes));
            editMode = true;
            startup = (Startup)accountViewModel.getAccount().getValue();
            hideBottomNavigation(false);
        } else {
            startup = RegistrationHandler.getStartup();
        }

        videoPitch = view.findViewById(R.id.register_startup_video_pitch_video);
        videoPitch.setBackgroundColor(Color.WHITE);

        videoOverlay = view.findViewById(R.id.register_video_overlay);
        videoOverlay.setImageDrawable(getResources().getDrawable(
                R.drawable.ic_add_24dp,
                Objects.requireNonNull(getContext()).getTheme()));

        videoOverlay.setOnClickListener(v -> {
            if(videoUri == null) {
                chooseVideo();
            } else {
                if(videoPitch.isPlaying()) {
                    videoPitch.pause();
                    videoOverlay.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_play_circle_outline_black_24dp,
                            Objects.requireNonNull(getContext()).getTheme()));
                } else {
                    videoPitch.start();
                    videoOverlay.setImageBitmap(null);
                }
            }
        });

        deleteVideoPitchButton = view.findViewById(R.id.button_delete_video_pitch);
        deleteVideoPitchButton.setVisibility(View.GONE);
        deleteVideoPitchButton.setOnClickListener(v -> {
            deleteVideoPitchButton.setVisibility(View.GONE);
            videoPitch.stopPlayback();
            videoPitch.setVideoURI(null);
            videoOverlay.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_add_24dp,
                    Objects.requireNonNull(getContext()).getTheme()));
            videoUri = null;
            videoPitch.suspend();
            videoPitch.clearAnimation();
            videoPitch.setVisibility(View.GONE);
            videoPitch.setVisibility(View.VISIBLE);
            videoPitch.setBackgroundColor(Color.WHITE);
        });

        videoPitch.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoPitch.seekTo(1);
                videoPitch.pause();
                videoOverlay.setImageDrawable(getResources().getDrawable(
                        R.drawable.ic_play_circle_outline_black_24dp,
                        Objects.requireNonNull(getContext()).getTheme()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        hideBottomNavigation(false);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null )
            return;

        if(requestCode == REQUEST_VIDEO_FETCH) {
            try {
                videoPitch.setBackgroundColor(Color.TRANSPARENT);
                videoUri = data.getData();
                videoPitch.setVideoURI(videoUri);
                videoPitch.seekTo(1);
                videoPitch.pause();
                deleteVideoPitchButton.setVisibility(View.VISIBLE);
                videoOverlay.setImageDrawable(getResources().getDrawable(
                        R.drawable.ic_play_circle_outline_black_24dp,
                        Objects.requireNonNull(getContext()).getTheme()));
            } catch (Exception e) {
                Log.d("debugMessage", e.getMessage());
            }
        } else {
            showSimpleDialog(getString(R.string.general_dialog_error_title),
                    getString(R.string.general_dialog_error_text));
        }
    }

    private void chooseVideo() {
        // lets user choose video from gallery
        Intent openGalleryIntent = new Intent(
                Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        openGalleryIntent.setType("video/*");
        if (openGalleryIntent.resolveActivity(
                Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            startActivityForResult(openGalleryIntent, REQUEST_VIDEO_FETCH);
        }

    }

    private void processInformation() {
        if(!editMode) {
            changeFragment(new RegisterFinancialRequirementsFragment());
        } else {
            popFragment(this);
        }
    }
}
