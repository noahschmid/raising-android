package com.raising.app.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.raising.app.R;
import com.raising.app.models.Image;

import java.util.Date;

public class ImageHandler {
    // configure cache size here
    private final static int PROFILE_PICTURES_CACHE_SIZE = 70;
    private final static int GALLERY_PICTURES_CACHE_SIZE = 20;

    private static ImageCache profilePictureCache = new ImageCache(PROFILE_PICTURES_CACHE_SIZE, "pp");
    private static ImageCache galleryCache = new ImageCache(GALLERY_PICTURES_CACHE_SIZE, "gl");

    private static Drawable placeholder = InternalStorageHandler.getContext().getDrawable(R.drawable.ic_placeholder_24dp);

    private static final String TAG = "ImageHandler";

    public static void init() {
      //  profilePictureCache.loadFromStorage();
    }

    public static Drawable getProfilePicture(long id) {
        Bitmap bitmap = profilePictureCache.get(id);

        if(bitmap != null) {
            Log.d(TAG, "getProfilePicture: Cache HIT id " + id);
            return new BitmapDrawable(InternalStorageHandler.getContext().getResources(), bitmap);
        }

        Log.e(TAG, "getProfilePicture: Cache MISS id " + id);
        return placeholder;
    }

    /**
     * Load profile image into image view
     *
     * @param id id of the profile image
     * @param imageView where to load the image into
     */
    public static void loadProfileImage(long id, ImageView imageView) {
        if (id <= 0) {
            imageView.setImageDrawable(placeholder);
        } else {
            Glide
                    .with(InternalStorageHandler.getContext())
                    .asBitmap()
                    .load(ApiRequestHandler.getDomain() + "media/profilepicture/" + id)
                    .centerCrop()
                    .placeholder(placeholder)
                    //.signature()
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(imageView);
        }
    }
}
