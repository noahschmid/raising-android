package com.raising.app.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.raising.app.R;
import com.raising.app.models.Image;
import com.raising.app.models.ImageCacheItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageHandler {
    // configure cache size here
    private final static int PROFILE_PICTURES_CACHE_SIZE = 50;
    private final static int GALLERY_PICTURES_CACHE_SIZE = 20;

    private static ImageCache profilePictureCache = new ImageCache(PROFILE_PICTURES_CACHE_SIZE, "pp");
    private static ImageCache galleryCache = new ImageCache(GALLERY_PICTURES_CACHE_SIZE, "gl");

    private static Drawable placeholder = InternalStorageHandler.getContext().getDrawable(R.drawable.ic_placeholder_24dp);

    private static final String TAG = "ImageHandler";

    public static Drawable getProfilePicture(long id) {
        Bitmap bitmap = profilePictureCache.get(id);

        if(bitmap != null) {
            Log.d(TAG, "getProfilePicture: Cache HIT");
            return new BitmapDrawable(InternalStorageHandler.getContext().getResources(), bitmap);
        }

        Log.d(TAG, "getProfilePicture: Cache MISS");
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
            Drawable cachedImage = getProfilePicture(id);
            imageView.setImageDrawable(cachedImage);

            Glide
                    .with(InternalStorageHandler.getContext())
                    .asBitmap()
                    .load(ApiRequestHandler.getDomain() + "media/profilepicture/" + id)
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                            profilePictureCache.add(new Image(id, resource));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
    }
}
