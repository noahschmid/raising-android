package com.raising.app.fragments.registration.investor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

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

import com.android.volley.VolleyError;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raising.app.R;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Image;
import com.raising.app.models.Investor;
import com.raising.app.util.AccountService;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.util.Serializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class RegisterInvestorImagesFragment extends RaisingFragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FETCH = 2;
    static final int REQUEST_GALLERY_CAPTURE = 3;
    static final int REQUEST_GALLERY_FETCH = 4;

    ImageView profileImage, addGalleryImage, profileImageOverlay;
    Button deleteProfileImageButton;
    FlexboxLayout galleryLayout;
    LayoutInflater inflater;
    Button finishButton;
    Investor investor;
    boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_images,
                container, false);
        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflater = (LayoutInflater)getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        profileImage = view.findViewById(R.id.register_investor_profile_image);
        profileImage.setOnClickListener(v -> {
            showImageMenu(profileImage, true);
        });

        profileImageOverlay = view.findViewById(R.id.register_profile_image_overlay);
        galleryLayout = view.findViewById(R.id.register_investor_images_gallery);

        deleteProfileImageButton = view.findViewById(R.id.button_delete_profile_img);
        deleteProfileImageButton.setVisibility(View.GONE);
        deleteProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImageOverlay.setVisibility(View.VISIBLE);
                deleteProfileImageButton.setVisibility(View.GONE);
                profileImage.setImageBitmap(null);
            }
        });

        addGalleryImage = view.findViewById(R.id.gallery_add);
        addGalleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageMenu(addGalleryImage, false);
            }
        });

        finishButton = view.findViewById(R.id.button_investor_images);
        finishButton.setOnClickListener(v -> { processInputs(); finishButton.setEnabled(false);});

        if(this.getArguments() != null && this.getArguments().getBoolean("editMode")) {
            view.findViewById(R.id.registration_images_progress).setVisibility(View.INVISIBLE);
            finishButton.setHint(getString(R.string.myProfile_apply_changes));
            investor = (Investor)AccountService.getAccount();
            editMode = true;
        } else {
            investor = RegistrationHandler.getInvestor();
        }

        loadImages();
    }

    private void loadImages() {
        if(investor.getProfilePicture() != null) {
            profileImage.setImageBitmap(investor.getProfilePicture().getBitmap());
        }

        if(investor.getGallery() != null) {
            investor.getGallery().forEach(image -> {
                addImageToGallery(image.getBitmap());
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    /**
     * Process media selected from gallery/camera
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
                    Log.d("InvestorImages", e.getMessage());
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
                    Log.d("InvestorImages", e.getMessage());
                }
                break;

            default:
                showSimpleDialog(getString(R.string.general_dialog_error_title),
                        getString(R.string.general_dialog_error_text));
        }
    }

    /**
     * Set the chosen profile image
     * @param image
     */
    private void setProfileImage(Bitmap image) {
        try {
            profileImage.setImageBitmap(image);
            deleteProfileImageButton.setVisibility(View.VISIBLE);
            profileImageOverlay.setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.d("InvestorImages", e.getMessage());
        }
    }

    /**
     * Add a new image to the gallery
     * @param image
     */
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

    /**
     * Show popup menu where user can choose between taking a new photo or choosing an old one
     * @param view
     * @param profileImage
     */
    private void showImageMenu(View view, boolean profileImage) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), view);
        popupMenu.setGravity(Gravity.END);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.select_image:
                    // lets user choose picture from gallery
                    Intent openGalleryIntent = new Intent(
                            Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    if (openGalleryIntent.resolveActivity(
                            Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                        startActivityForResult(openGalleryIntent, profileImage ? REQUEST_IMAGE_FETCH :
                                REQUEST_GALLERY_FETCH);
                    }
                    return true;
                case R.id.take_image:
                    // lets user take a picture
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(
                            Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, profileImage ? REQUEST_IMAGE_CAPTURE :
                                REQUEST_GALLERY_CAPTURE);
                    }
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.inflate(R.menu.image_floating_menu);
        popupMenu.show();
    }

    /**
     * Process the given inputss
     */
    private void processInputs() {
        if(profileImage.getDrawable() == null ||
                profileImage.getDrawable().getIntrinsicWidth()  == 0 ||
                profileImage.getDrawable().getIntrinsicHeight() == 0 ||
                profileImage.getDrawable() == getResources().getDrawable(
                        R.drawable.ic_add_24dp, Objects.requireNonNull(getContext()).getTheme())) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_no_picture_text));
            finishButton.setEnabled(true);
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
                gallery.add(new Image(galleryImg));            }
        }

        investor.setGallery(gallery);

        try {
            if(editMode) {
                AccountService.updateProfilePicture(new Image(logo));
                popCurrentFragment(this);
            } else {
                RegistrationHandler.saveInvestor(investor);
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Investor.class,
                        Serializer.InvestorRegisterSerializer);
                Gson gson = gsonBuilder.create();
                String investor = gson.toJson(RegistrationHandler.getInvestor());
                ApiRequestHandler.performPostRequest("investor/register", registerCallback,
                        errorCallback, new JSONObject(investor));
                Log.d("RegisterInvestorImagesFragment", investor);
            }
        } catch (JSONException | IOException e) {
            Log.d("RegisterInvestorImagesFragment","Error in process inputs: " + e.getMessage());
        }
    }

    /**
     * Save private profile after login response and proceed to matches fragment
     */
    Function<JSONObject, Void> registerCallback = response -> {
        try {
            RegistrationHandler.finish(response.getLong("id"),
                    response.getString("token"), false);
            clearBackstackAndReplace(new MatchesFragment());
        } catch (Exception e) {
            Log.d("InvestorImagesFragment", e.getMessage());
            showSimpleDialog(getString(R.string.generic_error_title),
                    getString(R.string.generic_error_text));
        }
        return null;
    };

    /**
     * Display error from backend
     */
    Function<VolleyError, Void> errorCallback = response -> {
        try {
            if (response.networkResponse.statusCode == 500) {
                JSONObject body = new JSONObject(new String(
                        response.networkResponse.data,"UTF-8"));
                showSimpleDialog(getString(R.string.generic_error_title),
                        body.getString("message"));
                Log.d("InvestorImages", body.getString("message"));
            }
        } catch (Exception e) {
            Log.d("debugMessage", e.toString());
        }
        Log.d("debugMessage", ApiRequestHandler.parseVolleyError(response));
        return null;
    };
}
