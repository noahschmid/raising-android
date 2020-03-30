package com.raising.app.fragments.registration.startup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class RegisterStartupImagesFragment extends RaisingFragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FETCH = 2;

    ImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_startup_images, container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.register_startup_profile_image);
        profileImage.setOnClickListener(v -> showImageMenu(profileImage));
        Button finishButton = view.findViewById(R.id.button_startup_images);
        finishButton.setOnClickListener(v -> processInputs());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideBottomNavigation(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(bitmap);
            } catch (NullPointerException e) {
                Log.d("InvestorImages", "Nullpointer");
            }
        } else if (requestCode == REQUEST_IMAGE_FETCH) {
            try {
                Uri imageUri = data.getData();
                profileImage.setImageURI(imageUri);
            } catch (NullPointerException e) {
                Log.d("InvestorImages", "Nullpointer");
            }
        } else {
            showSimpleDialog(getString(R.string.general_dialog_error_title),
                    getString(R.string.general_dialog_error_text));
        }
    }

    private void showImageMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), view);
        popupMenu.setGravity(Gravity.END);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.image_menu_select_image:
                    // lets user choose picture from gallery
                    Intent openGalleryIntent = new Intent(
                            Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    if (openGalleryIntent.resolveActivity(
                            Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                        startActivityForResult(openGalleryIntent, REQUEST_IMAGE_FETCH);
                    }
                    return true;
                case R.id.image_menu_take_image:
                    // lets user take a picture
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(
                            Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = Bitmap.createBitmap(profileImage.getDrawable().getIntrinsicWidth(),
                profileImage.getDrawable().getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        Image logo = new Image(Base64.encodeToString(imageBytes, Base64.DEFAULT));

        ArrayList<Image> gallery = new ArrayList<>();

        try {
            RegistrationHandler.setImages(logo, gallery);
            changeFragment(new RegisterFinancialRequirementsFragment());
        } catch (IOException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }
}
