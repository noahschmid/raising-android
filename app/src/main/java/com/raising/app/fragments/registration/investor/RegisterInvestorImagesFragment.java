package com.raising.app.fragments.registration.investor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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
import androidx.lifecycle.MutableLiveData;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.onboarding.OnboardingPost1Fragment;
import com.raising.app.models.Image;
import com.raising.app.models.Investor;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.ImageRotator;
import com.raising.app.util.ImageUploader;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.Serializer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class RegisterInvestorImagesFragment extends RaisingFragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FETCH = 2;
    static final int REQUEST_GALLERY_CAPTURE = 3;
    static final int REQUEST_GALLERY_FETCH = 4;
    private static final String TAG = "RegisterInvestorImages";
    private MutableLiveData<Boolean> imagesUploaded = new MutableLiveData<>();
    private boolean permissionGranted = false;

    ImageView profileImage, profileImageOverlay;
    View addGalleryImage;
    Button deleteProfileImageButton;
    FlexboxLayout galleryLayout;
    LayoutInflater inflater;
    Button finishButton;
    Investor investor;
    boolean editMode = false;
    boolean profilePictureChanged = false;
    boolean galleryChanged = false;

    List<Image> gallery = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_images,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_profile_images), true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagesUploaded.setValue(false);
        imagesUploaded.observe(getViewLifecycleOwner(),
                value -> {
                    if (value.booleanValue() == true) {
                        submitRegistration();
                    }
                });

        inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        profileImage = view.findViewById(R.id.register_investor_profile_image);
        profileImage.setOnClickListener(v -> {
            if (permissionGranted) {
                showImageMenu(true);
            } else {
                checkPermissions();
            }
        });

        profileImageOverlay = view.findViewById(R.id.register_profile_image_overlay);
        galleryLayout = view.findViewById(R.id.register_investor_images_gallery);

        deleteProfileImageButton = view.findViewById(R.id.button_delete_profile_img);
        deleteProfileImageButton.setVisibility(View.GONE);
        deleteProfileImageButton.setOnClickListener(v -> {
            profileImageOverlay.setVisibility(View.VISIBLE);
            deleteProfileImageButton.setVisibility(View.GONE);
            profileImage.setImageBitmap(null);
        });

        finishButton = view.findViewById(R.id.button_investor_images);
        finishButton.setOnClickListener(v -> {
            finishButton.setEnabled(false);
            processInputs();
        });

        if (this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_images_progress).setVisibility(View.INVISIBLE);
            finishButton.setHint(getString(R.string.myProfile_apply_changes));
            finishButton.setVisibility(View.INVISIBLE);
            investor = (Investor) accountViewModel.getAccount().getValue();
            editMode = true;
            hideBottomNavigation(false);
        } else {
            investor = RegistrationHandler.getInvestor();
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
        galleryImage.setOnClickListener(v -> {
            if (permissionGranted) {
                if (galleryImage.getContentDescription() == "placeholder") {
                    showImageMenu(false);
                }
            } else {
                checkPermissions();
            }
        });
        galleryImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_24dp));
        AppCompatButton deleteButton = galleryObject.findViewById(R.id.button_delete_gallery_img);
        deleteButton.setVisibility(View.GONE);
        addGalleryImage = galleryObject;
        galleryLayout.addView(galleryObject);
    }

    /**
     * Load images from backend into image views
     */
    private void loadImages() {
        if (investor.getProfilePictureId() > 0) {
            Glide
                    .with(this)
                    .load(ApiRequestHandler.getDomain() + "media/profilepicture/" +
                            investor.getProfilePictureId())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_person_24dp)
                    .into(profileImage);
            profileImageOverlay.setVisibility(View.GONE);
            deleteProfileImageButton.setVisibility(View.GONE);
        }

        if (investor.getGalleryIds() != null) {
            investor.getGalleryIds().forEach(imageId -> {
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
                    Log.d("InvestorImages", "" + e.getMessage());
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
                    Log.d("InvestorImages", e.getMessage());
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
            profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (investor.getProfilePictureId() == -1) {
                deleteProfileImageButton.setVisibility(View.VISIBLE);
            }
            profileImageOverlay.setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.d("InvestorImages", e.getMessage());
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
        deleteButton.setOnClickListener(v -> {
            galleryObject.setVisibility(View.GONE);
            galleryLayout.removeView(galleryObject);
            if (image.getId() != -1) {
                ApiRequestHandler.performDeleteRequest("media/gallery/" + image.getId(),
                        success -> {
                            investor.getGalleryIds().remove(image.getId());
                            return null;
                        },
                        error -> {
                            return null;
                        });
                gallery.remove(image);
                if (addGalleryImage == null) {
                    addNewGalleryPlaceholder();
                }
            } else {
                investor.getGalleryIds().remove(image.getId());
            }
        });

        if (addGalleryImage == null) {
            galleryLayout.addView(galleryObject);
        }
        addGalleryImage = null;
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

        try {
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
        } catch (Exception e) {
            Log.d("RegisterInvestorImagesFragment", "Error in process inputs: " + e.getMessage());
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
                    investor.setProfilePictureId(pResponse.getLong("id"));
                }

                if (response.has("galleryResponse")) {
                    JSONArray gResponse = response.getJSONArray("galleryResponse");
                    if (investor.getGalleryIds() == null) {
                        investor.setGalleryIds(new ArrayList<>());
                    }
                    for (int i = 0; i < gResponse.length(); ++i) {
                        investor.getGalleryIds().add(gResponse.getLong(i));
                    }
                }

                Log.d(TAG, "Successfully uploaded images");

                RegistrationHandler.saveInvestor(investor);
                imagesUploaded.postValue(true);
            } catch (Exception e) {
                Log.e(TAG, "uploadImages: " + e.getMessage());
                finishButton.setEnabled(true);
                showGenericError();
            }

            return null;
        }, error -> {
            Log.e(TAG, "upload images: " + error.toString());
            finishButton.setEnabled(true);
            return null;
        }).execute();
    }

    /**
     * Submit the registration to backend server
     */
    private void submitRegistration() {
        imagesUploaded.setValue(false);
        try {
            viewStateViewModel.startLoading();
            RegistrationHandler.saveInvestor(investor);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Investor.class,
                    Serializer.InvestorRegisterSerializer);
            Gson gson = gsonBuilder.create();
            String investor = gson.toJson(RegistrationHandler.getInvestor());
            ApiRequestHandler.performPostRequest("investor/register", registerCallback,
                    errorCallback, new JSONObject(investor));
            Log.d("RegisterInvestorImagesFragment", investor);
        } catch (Exception e) {
            viewStateViewModel.stopLoading();
            Log.e(TAG, "submitRegistration: " + e.getMessage());
            showGenericError();
            finishButton.setEnabled(true);
        }
    }

    /**
     * Save private profile after login response and proceed to matches fragment
     */
    Function<JSONObject, Void> registerCallback = response -> {
        viewStateViewModel.stopLoading();
        try {
            Investor investor = RegistrationHandler.getInvestor();
            investor.setId(response.getLong("id"));
            accountViewModel.setAccount(investor);
            RegistrationHandler.finish(response.getLong("id"),
                    response.getString("token"), false);

            if (isFirstAppLaunch() && !isDisablePostOnboarding()) {
                clearBackstackAndReplace(new OnboardingPost1Fragment());
            } else {
                clearBackstackAndReplace(new MatchesFragment());
            }

        } catch (Exception e) {
            Log.d("InvestorImagesFragment", e.getMessage());
            showGenericError();
        }
        return null;
    };

    /**
     * Display error from backend
     */
    Function<VolleyError, Void> errorCallback = response -> {
        viewStateViewModel.stopLoading();
        try {
            if (response.networkResponse != null) {
                JSONObject body = new JSONObject(new String(
                        response.networkResponse.data, StandardCharsets.UTF_8));
                Log.e(TAG, "status code: " + response.networkResponse.statusCode);
                Log.e("InvestorImages", body.getString("message"));
                showGenericError();
            } else {
                clearBackstackAndReplace(new LoginFragment());
            }
        } catch (Exception e) {
            Log.e("InvestorImages", "errorCallback: " + e.toString());
        }
        return null;
    };
}
