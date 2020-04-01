package com.raising.app.fragments.registration.investor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.raising.app.R;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.models.Image;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.RegistrationHandler;

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

    ImageView profileImage, addGalleryImage;
    Button deleteProfileImageButton;
    FlexboxLayout galleryLayout;
    LayoutInflater inflater;

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

        galleryLayout = view.findViewById(R.id.register_investor_images_gallery);

        deleteProfileImageButton = view.findViewById(R.id.button_delete_profile_img);
        deleteProfileImageButton.setVisibility(View.GONE);
        deleteProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfileImageButton.setVisibility(View.GONE);
                profileImage.setImageDrawable(getResources().getDrawable(
                        R.drawable.ic_add_24dp, Objects.requireNonNull(getContext()).getTheme()));
            }
        });

        addGalleryImage = view.findViewById(R.id.gallery_add);
        addGalleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageMenu(addGalleryImage, false);
            }
        });

        Button finishButton = view.findViewById(R.id.button_investor_images);
        finishButton.setOnClickListener(v -> processInputs());

        if(this.getArguments() != null && this.getArguments().getBoolean("isProfileFragment")) {
            view.findViewById(R.id.registration_images_progress).setVisibility(View.GONE);
            finishButton.setHint(getString(R.string.myProfile_apply_changes));
            finishButton.setOnClickListener(v -> popCurrentFragment(this));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private void setProfileImage(Bitmap image) {
        try {
            profileImage.setImageBitmap(image);
            deleteProfileImageButton.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            Log.d("InvestorImages", e.getMessage());
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

    private void processInputs() {
        if(profileImage.getDrawable() == null ||
                profileImage.getDrawable() == getResources().getDrawable(
                        R.drawable.ic_add_24dp, Objects.requireNonNull(getContext()).getTheme())) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_no_picture_text));
            return;
        }
        //encode image to base64 string
        Image logo = imageViewToImageInstance(profileImage);

        ArrayList<Image> gallery = new ArrayList<>();
        for(int i = 0; i < galleryLayout.getChildCount(); ++i) {
            View view = galleryLayout.getChildAt(i);
            if(view.getId() != R.id.gallery_add) {
                gallery.add(imageViewToImageInstance(view.findViewById(R.id.gallery_image)));
            }
        }

        try {
            RegistrationHandler.setImages(logo, gallery);
            Gson gson = new Gson();
            String investor = gson.toJson(RegistrationHandler.getInvestor());
            ApiRequestHandler.performPostRequest("investor/register", registerCallback,
                    errorCallback, new JSONObject(investor), getContext());
            Log.d("debugMessage", investor);
        } catch (JSONException | IOException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Get image of imageView and convert it to a base64 encoded string, then create a new image
     * object and return the result
     * @param imageView Instance of an ImageView
     * @return Instance of Image class
     */
    private Image imageViewToImageInstance(ImageView imageView) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawable().getIntrinsicWidth(),
                imageView.getDrawable().getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return new Image(Base64.encodeToString(imageBytes, Base64.DEFAULT));
    }

    /**
     * Cancel the current registration (as it was successfully sent to backend) and
     * proceed to login screen
     */
    Function<JSONObject, Void> registerCallback = response -> {
        RegistrationHandler.cancel();
        clearBackstackAndReplace(new LoginFragment());
        return null;
    };

    /**
     * Display error from backend
     */
    Function<VolleyError, Void> errorCallback = response -> {
        try {
            if (response.networkResponse.statusCode == 500) {
                showSimpleDialog(getString(R.string.generic_error_title),
                        getString(R.string.generic_error_text));
            }
        } catch (Exception e) {
            Log.d("debugMessage", e.toString());
        }
        Log.d("debugMessage", ApiRequestHandler.parseVolleyError(response));
        return null;
    };
}
