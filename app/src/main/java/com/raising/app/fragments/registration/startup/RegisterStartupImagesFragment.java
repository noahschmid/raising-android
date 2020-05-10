package com.raising.app.fragments.registration.startup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raising.app.R;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Image;
import com.raising.app.models.Startup;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.ImageRotator;
import com.raising.app.util.ImageUploader;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.Serializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class RegisterStartupImagesFragment extends RaisingFragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FETCH = 2;
    static final int REQUEST_GALLERY_CAPTURE = 3;
    static final int REQUEST_GALLERY_FETCH = 4;
    private static final String TAG = "RegisterStartupImages";
    private boolean permissionGranted = false;

    ImageView profileImage, profileImageOverlay;
    View addGalleryImage;
    Button deleteProfileImageButton;
    FlexboxLayout galleryLayout;
    LayoutInflater inflater;
    Button finishButton;
    Startup startup;
    boolean editMode = false;
    boolean profilePictureChanged = false;
    boolean galleryChanged = false;

    private int successfulUploads = 0;

    List<Image> gallery = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_images,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_profile_images), true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        profileImage = view.findViewById(R.id.register_startup_profile_image);
        profileImage.setOnClickListener(v -> {
            if (permissionGranted) {
                showImageMenu(true);
            } else {
                checkPermissions();
            }
        });

        profileImageOverlay = view.findViewById(R.id.register_profile_image_overlay);
        galleryLayout = view.findViewById(R.id.register_startup_images_gallery);

        deleteProfileImageButton = view.findViewById(R.id.button_delete_profile_img);
        deleteProfileImageButton.setVisibility(View.GONE);
        deleteProfileImageButton.setOnClickListener(v -> {
            profileImageOverlay.setVisibility(View.VISIBLE);
            deleteProfileImageButton.setVisibility(View.GONE);
            profileImage.setImageBitmap(null);
        });

        finishButton = view.findViewById(R.id.button_startup_images);
        finishButton.setOnClickListener(v -> {
            finishButton.setEnabled(false);
            processInputs();
        });

        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_images_progress).setVisibility(View.INVISIBLE);
            finishButton.setHint(getString(R.string.myProfile_apply_changes));
            finishButton.setVisibility(View.INVISIBLE);
            startup = (Startup) accountViewModel.getAccount().getValue();
            editMode = true;
            hideBottomNavigation(false);
        } else {
            startup = RegistrationHandler.getStartup();
        }

        loadImages();
        addNewGalleryPlaceholder();
        checkPermissions();
    }

    /**
     * Add an image view at the end of the gallery with an + drawable
     */
    private void addNewGalleryPlaceholder() {
        if (gallery.size() >= 9) {
            return;
        }

        final View galleryObject = inflater.inflate(R.layout.item_gallery, null);
        ImageView galleryImage = galleryObject.findViewById(R.id.gallery_image);
        galleryImage.setContentDescription("placeholder");
        galleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionGranted) {
                    if (galleryImage.getContentDescription() == "placeholder") {
                        showImageMenu(false);
                    }
                } else {
                    checkPermissions();
                }
            }
        });
        galleryImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_24dp));
        AppCompatButton deleteButton = galleryObject.findViewById(R.id.button_delete_gallery_img);
        deleteButton.setVisibility(View.GONE);
        addGalleryImage = galleryObject;
        galleryLayout.addView(galleryObject);
    }

    private void loadImages() {
        if (startup.getProfilePictureId() > 0) {
            Glide
                    .with(this)
                    .load(ApiRequestHandler.getDomain() + "media/profilepicture/" +
                            startup.getProfilePictureId())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.ic_person_24dp)
                    .into(profileImage);
            profileImageOverlay.setVisibility(View.GONE);
            deleteProfileImageButton.setVisibility(View.GONE);
        }

        if (startup.getGalleryIds() != null) {
            startup.getGalleryIds().forEach(imageId -> {
                if (imageId > 0) {
                    Glide.with(this)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .load(ApiRequestHandler.getDomain() + "media/gallery/" +
                                    imageId)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable
                                        Transition<? super Bitmap> transition) {
                                    addImageToGallery(new Image(imageId, resource));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onAccountUpdated() {
        popCurrentFragment(this);
        accountViewModel.updateCompleted();
    }

    /**
     * Process media selected from gallery/camera
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        switch (requestCode) {
            case REQUEST_GALLERY_CAPTURE:
                Bitmap image = (Bitmap) data.getExtras().get("data");
                addImageToGallery(new Image(image));
                galleryChanged = true;
                finishButton.setVisibility(View.VISIBLE);
                break;

            case REQUEST_GALLERY_FETCH:
                Uri imageUri = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    addImageToGallery(new Image(ImageRotator.checkRotation(getPath(imageUri), image)));
                    galleryChanged = true;
                    finishButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.d("StartupImages", e.getMessage());
                }
                break;

            case REQUEST_IMAGE_CAPTURE:
                image = (Bitmap) data.getExtras().get("data");
                setProfileImage(image);
                profilePictureChanged = true;
                finishButton.setVisibility(View.VISIBLE);
                break;

            case REQUEST_IMAGE_FETCH:
                imageUri = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    setProfileImage(ImageRotator.checkRotation(getPath(imageUri), image));
                    profilePictureChanged = true;
                    finishButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.d("StartupImages", e.getMessage());
                }
                break;

            default:
                showSimpleDialog(getString(R.string.general_dialog_error_title),
                        getString(R.string.general_dialog_error_text));
        }
    }

    /**
     * Check whether the needed read/write permissions to external storage are granted and if not
     * show dialog
     */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);

        } else {
            this.permissionGranted = true;
        }

    }

    /**
     * Callback after granting or denying external storage permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1052) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                this.permissionGranted = true;

            } else {
                showSimpleDialog(getString(R.string.permissionTitle),
                        getString(R.string.permissionText));
            }
        }
    }

    /**
     * Set the chosen profile image
     *
     * @param image
     */
    private void setProfileImage(Bitmap image) {
        try {
            profileImage.setImageBitmap(image);
            if (startup.getProfilePictureId() == -1) {
                deleteProfileImageButton.setVisibility(View.VISIBLE);
            }
            profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            profileImageOverlay.setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.d("StartupImages", e.getMessage());
        }
    }

    /**
     * Add a new image to the gallery
     *
     * @param image
     */
    private void addImageToGallery(Image image) {
        final View galleryObject;
        if (addGalleryImage == null) {
            galleryObject = inflater.inflate(R.layout.item_gallery, null);
        } else {
            galleryObject = addGalleryImage;
        }

        galleryObject.setOnClickListener(null);

        ImageView galleryImage = galleryObject.findViewById(R.id.gallery_image);
        galleryImage.setContentDescription("gallery");
        gallery.add(image);
        galleryImage.setImageBitmap(image.getImage());
        AppCompatButton deleteButton = galleryObject.findViewById(R.id.button_delete_gallery_img);
        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryObject.setVisibility(View.GONE);
                galleryLayout.removeView(galleryObject);
                if (image.getId() != -1) {
                    ApiRequestHandler.performDeleteRequest("media/gallery/" + image.getId(),
                            success -> {
                                startup.getGalleryIds().remove(image.getId());
                                return null;
                            },
                            error -> {
                                return null;
                            });
                    gallery.remove(image);
                    addNewGalleryPlaceholder();
                } else {
                    startup.getGalleryIds().remove(image.getId());
                }
            }
        });

        if (addGalleryImage == null) {
            galleryLayout.addView(galleryObject);
        }
        addNewGalleryPlaceholder();
    }

    /**
     * Show menu where user can choose between taking a new photo or choosing an existing one
     *
     * @param profileImage true, if user adds profile picture
     *                     false, if user adds picture to gallery
     */
    private void showImageMenu(boolean profileImage) {
        final String[] options = {getString(R.string.image_action_dialog_take),
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

    /**
     * Process the given inputs
     */
    private void processInputs() {
        if (profileImage.getDrawable() == null) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_no_picture_text));
            finishButton.setEnabled(true);
            return;
        }

        if (((BitmapDrawable) profileImage.getDrawable()).getBitmap() == null ||
                profileImage.getDrawable().getIntrinsicWidth() == 0 ||
                profileImage.getDrawable().getIntrinsicHeight() == 0 ||
                profileImage.getDrawable() == getResources().getDrawable(
                        R.drawable.ic_add_24dp, Objects.requireNonNull(getContext()).getTheme())) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_no_picture_text));
            finishButton.setEnabled(true);
            return;
        }

        Bitmap logo = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        if (editMode) {
            if (profilePictureChanged) {
                accountViewModel.updateProfilePicture(new Image(logo));
            }
            if (galleryChanged) {
                accountViewModel.updateGallery(gallery);
            }
        } else {
            uploadImages(logo);
        }
    }

    /**
     * Upload profile picture to backend server
     *
     * @param logo the profile picture
     */
    private void uploadImages(Bitmap logo) {
        List<Bitmap> bitmaps = new ArrayList<>();
        gallery.forEach(img -> {
            if (img.getId() < 1) {
                bitmaps.add(img.getImage());
            }
        });
        new ImageUploader(logo, bitmaps, response -> {
            try {
                if (response.has("profileResponse")) {
                    JSONObject pResponse = response.getJSONObject("profileResponse");
                    startup.setProfilePictureId(pResponse.getLong("id"));
                }

                if (response.has("galleryResponse")) {
                    JSONArray gResponse = response.getJSONArray("galleryResponse");
                    if (startup.getGalleryIds() == null) {
                        startup.setGalleryIds(new ArrayList<>());
                    }
                    for (int i = 0; i < gResponse.length(); ++i) {
                        startup.getGalleryIds().add(gResponse.getLong(i));
                    }
                }

                Log.d(TAG, "Successfully uploaded images");

                RegistrationHandler.saveStartup(startup);
                changeFragment(new RegisterFinancialRequirementsFragment());
            } catch (Exception e) {
                Log.e(TAG, "uploadImages: " + e.getMessage());
                finishButton.setEnabled(true);
                displayGenericError();
            }

            return null;
        }, error -> {
            Log.e(TAG, "upload images: " + error.toString());
            finishButton.setEnabled(true);
            return null;
        }).execute();
    }
}
