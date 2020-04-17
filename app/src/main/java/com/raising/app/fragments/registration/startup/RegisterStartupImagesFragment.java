package com.raising.app.fragments.registration.startup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.raising.app.R;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Image;
import com.raising.app.models.Startup;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.ImageRotator;
import com.raising.app.util.RegistrationHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class RegisterStartupImagesFragment extends RaisingFragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FETCH = 2;
    static final int REQUEST_GALLERY_CAPTURE = 3;
    static final int REQUEST_GALLERY_FETCH = 4;

    private ImageView profileImage, addGalleryImage, profileImageOverlay;
    private Button deleteProfileImageButton;
    private FlexboxLayout galleryLayout;
    private LayoutInflater inflater;
    private Startup startup;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_images, container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_profile_images), true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater = (LayoutInflater)getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        profileImage = view.findViewById(R.id.register_startup_profile_image);
        profileImage.setOnClickListener(v -> showImageMenu(true));

        Button finishButton = view.findViewById(R.id.button_startup_images);
        finishButton.setOnClickListener(v -> processInputs());

        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_images_progress).setVisibility(View.INVISIBLE);
            finishButton.setHint(getString(R.string.myProfile_apply_changes));
            editMode = true;
            startup = (Startup)accountViewModel.getAccount().getValue();
            hideBottomNavigation(false);
        } else {
            startup = RegistrationHandler.getStartup();
        }

        galleryLayout = view.findViewById(R.id.register_startup_images_gallery);
        profileImageOverlay = view.findViewById(R.id.register_profile_image_overlay);

        deleteProfileImageButton = view.findViewById(R.id.button_delete_profile_img);
        deleteProfileImageButton.setVisibility(View.GONE);
        deleteProfileImageButton.setOnClickListener(v -> {
            deleteProfileImageButton.setVisibility(View.GONE);
            profileImageOverlay.setVisibility(View.VISIBLE);
            profileImage.setImageBitmap(null);
        });

        addGalleryImage = view.findViewById(R.id.gallery_add);
        addGalleryImage.setOnClickListener(v -> showImageMenu(false));

        loadImages();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    private void loadImages() {
        if(startup.getProfilePictureId() != -1) {
                Glide
                .with(this)
                .load(ApiRequestHandler.getDomain() + "media/profilepicture/" +
                        startup.getProfilePictureId())
                .centerCrop()
                .placeholder(R.drawable.ic_person_24dp)
                .into(profileImage);
            profileImageOverlay.setVisibility(View.GONE);
            deleteProfileImageButton.setVisibility(View.VISIBLE);
        }

        if(startup.getGallery() != null) {
            startup.getGallery().forEach(image -> {
                addImageToGallery(image.getBitmap());
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;
        switch (requestCode) {
            case REQUEST_GALLERY_CAPTURE:
                Bitmap image = (Bitmap) data.getExtras().get("data");
                addImageToGallery(image);
                break;

            case REQUEST_GALLERY_FETCH:
                Uri imageUri = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    addImageToGallery(image);
                } catch (Exception e) {
                    Log.d("StartupImages", e.getMessage());
                }
                break;

            case REQUEST_IMAGE_CAPTURE:
                image = (Bitmap) data.getExtras().get("data");
                setProfileImage(image);
                break;

            case REQUEST_IMAGE_FETCH:
                imageUri = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    setProfileImage(image);
                } catch (Exception e) {
                    Log.d("StartupImages", e.getMessage());
                }
                break;
            default:
                showSimpleDialog(getString(R.string.general_dialog_error_title),
                        getString(R.string.general_dialog_error_text));
        }
    }

    private void setProfileImage(Bitmap image) {
        try {
            profileImage.setImageBitmap(image); //TODO: ImageRotator.rotateImage(image)
            deleteProfileImageButton.setVisibility(View.VISIBLE);
            profileImageOverlay.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.d("StartupImages", e.getMessage());
        }
    }

    private void addImageToGallery(Bitmap image) {
        final View galleryObject = inflater.inflate(R.layout.item_gallery, null);
        ImageView galleryImage = galleryObject.findViewById(R.id.gallery_image);
        galleryImage.setImageBitmap(image);
        AppCompatButton deleteButton = galleryObject.findViewById(R.id.button_delete_gallery_img);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryObject.setVisibility(View.GONE);
                galleryLayout.removeView(galleryObject);
            }
        });
        galleryLayout.addView(galleryObject);
    }

    private void showImageMenu(boolean profileImage) {
        final String [] options = {getString(R.string.image_action_dialog_take),
                getString(R.string.image_action_dialog_choose), getString(R.string.cancel_text)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        //prepare custom title view
        TextView titleView = new TextView(this.getContext());
        titleView.setText(getString(R.string.image_action_dialog_title));
        titleView.setTextSize(28f);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setPadding(50, 20, 0, 20);
        titleView.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.raisingPrimary));
        titleView.setTextColor(ContextCompat.getColor(this.getContext(), R.color.raisingWhite));
        builder.setCustomTitle(titleView);

        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals(getString(R.string.image_action_dialog_take))) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(
                        Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, profileImage ? REQUEST_IMAGE_CAPTURE :
                            REQUEST_GALLERY_CAPTURE);
                }

            } else if (options[item].equals(getString(R.string.image_action_dialog_choose))) {
                Intent openGalleryIntent = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                if (openGalleryIntent.resolveActivity(
                        Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                    startActivityForResult(openGalleryIntent, profileImage ? REQUEST_IMAGE_FETCH :
                            REQUEST_GALLERY_FETCH);
                }
            } else if (options[item].equals(getString(R.string.cancel_text))) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onAccountUpdated() {
        popCurrentFragment(this);
        accountViewModel.updateCompleted();
    }

    private void processInputs() {
        if(profileImage.getDrawable() == null ||
                profileImage.getDrawable().getIntrinsicWidth()  == 0 ||
                profileImage.getDrawable().getIntrinsicHeight() == 0 ||
                profileImage.getDrawable() == getResources().getDrawable(
                        R.drawable.ic_add_24dp, Objects.requireNonNull(getContext()).getTheme())) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_no_picture_text));
            return;
        }
        //encode image to base64 string

        Bitmap logo =((BitmapDrawable)profileImage.getDrawable()).getBitmap();
        ArrayList<Image> gallery = new ArrayList<>();
        for(int i = 0; i < galleryLayout.getChildCount(); ++i) {
            View view = galleryLayout.getChildAt(i);
            if(view.getId() != R.id.gallery_add) {
                ImageView img = view.findViewById(R.id.gallery_image);
                Bitmap galleryImg = ((BitmapDrawable)(img).getDrawable()).getBitmap();
                gallery.add(new Image(galleryImg));
            }
        }

        startup.setProfilePicture(new Image(logo));
        startup.setGallery(gallery);

        try {
            if(!editMode) {
                RegistrationHandler.saveStartup(startup);
                changeFragment(new RegisterStartupVideoFragment());
            } else {
                accountViewModel.updateProfilePicture(new Image(logo));
                popCurrentFragment(this);
            }

        } catch (IOException e) {
            Log.e("StartupImages", "Error in processInputs: " + e.getMessage());
        }
    }
}
