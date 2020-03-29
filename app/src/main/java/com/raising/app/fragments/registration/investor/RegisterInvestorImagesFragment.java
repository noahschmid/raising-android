package com.raising.app.fragments.registration.investor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;

public class RegisterInvestorImagesFragment extends RaisingFragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FETCH = 2;

    ImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_investor_images, container, false);

        hideBottomNavigation(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.register_investor_profile_image);
        profileImage.setOnClickListener(v -> {
            showImageMenu(profileImage);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(bitmap);
        } else if (requestCode == REQUEST_IMAGE_FETCH) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }


    private void showImageMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), view, R.style.CentralizeImagePopup);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.image_menu_select_image:
                    // lets user choose picture from gallery
                    Intent openGalleryIntent = new Intent(
                            Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(openGalleryIntent, REQUEST_IMAGE_FETCH);
                    return true;
                case R.id.image_menu_take_image:
                    // lets user take a picture
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    return true;
                default:
                    return false;

            }
        });
        popupMenu.inflate(R.menu.image_floating_popup_menu);
        popupMenu.show();
    }
}
