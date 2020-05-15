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
import com.raising.app.models.Account;
import com.raising.app.models.Image;
import com.raising.app.models.Match;
import com.raising.app.models.MatchListItem;
import com.raising.app.models.leads.Lead;

import java.sql.Timestamp;
import java.util.Date;

public class ImageHandler {
    private static Drawable placeholder = InternalStorageHandler.getContext().getDrawable(R.drawable.ic_placeholder_24dp);

    private static final String TAG = "ImageHandler";

    /**
     * Load profile image into image view
     *
     * @param id id of the profile image
     * @param imageView where to load the image into
     */
    public static void loadProfileImage(long id, ImageView imageView, Timestamp timestamp) {
        if (id <= 0) {
            imageView.setImageDrawable(placeholder);
        } else {
            Glide
                    .with(InternalStorageHandler.getContext())
                    .asBitmap()
                    .load(ApiRequestHandler.getDomain() + "media/profilepicture/" + id)
                    .centerCrop()
                    .placeholder(placeholder)
                    .signature(new ObjectKey(timestamp.getTime()))
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(imageView);
        }
    }

    public static void loadProfileImage(Lead lead, ImageView imageView) {
        loadProfileImage(lead.getProfilePictureId(), imageView, lead.getAccountLastChanged());
    }

    public static void loadProfileImage(Match match, ImageView imageView) {
        loadProfileImage(match.getProfilePictureId(), imageView, match.getAccountLastChanged());
    }

    public static void loadProfileImage(Account account, ImageView imageView) {
        loadProfileImage(account.getProfilePictureId(), imageView, account.getLastChanged());
    }

    public static void loadProfileImage(MatchListItem matchListItem, ImageView imageView) {
        loadProfileImage(matchListItem.getPictureId(), imageView, matchListItem.getAccountLastChanged());
    }

}
