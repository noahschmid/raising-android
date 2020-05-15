package com.raising.app.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.raising.app.models.Image;
import com.raising.app.models.ImageCacheItem;

import java.util.ArrayList;

public class ImageCache {
    private ArrayList<ImageCacheItem> imageCacheItems = new ArrayList<>();
    private ArrayList<Image> images = new ArrayList<>();

    private final String TAG = "ImageCache";
    private int cacheSize;
    private String tag;

    /**
     * Create a new cache for images
     * @param cacheSize maximal count of images that are cache
     * @param tag tag used to identify cache on the internal storage
     */
    public ImageCache(int cacheSize, @NonNull String tag) {
        this.cacheSize = cacheSize;
        this.tag = tag;
    }

    /**
     * Add new image to cache using LRU strategy.
     * @param image the image to save
     */
    public void add(Image image) {
        if(images.size() < cacheSize) { ;
            images.add(image);
        } else {
            images.set(0, image);
        }
/*
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if(imageCacheItems.size() < cacheSize) {
                    ImageCacheItem imageCacheItem = new ImageCacheItem();
                    String filename = "img_" + tag + "_" + imageCacheItems.size();
                    imageCacheItem.setFilename(filename);
                    imageCacheItems.add(imageCacheItem);
                    InternalStorageHandler.saveBitmap(image.getImage(), filename);
                } else {
                    imageCacheItems.get(0).setId(image.getId());
                }
                try {
                    InternalStorageHandler.saveObject(imageCacheItems, tag);
                } catch (Exception e) {
                    Log.e(TAG, "run: " + e.getMessage());
                }
            }
        });*/
    }

    /**
     * Retrieve bitmap from array list
     * @param id
     * @return
     */
    public Bitmap get(long id) {
        for(int i = 0; i < images.size(); ++i) {
            if(images.get(i).getId() == id) {
                return images.get(i).getImage();
            }
        }

        return null;
    }

    /**
     * Retrieve images from storage
     * @return
     */
    public void loadFromStorage() {
        try {
            imageCacheItems = (ArrayList<ImageCacheItem>)InternalStorageHandler.loadObject(tag);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < imageCacheItems.size(); ++i) {
                        images.add(new Image(imageCacheItems.get(i).getId(),
                                InternalStorageHandler.loadBitmap("images/" + imageCacheItems.get(i).getFilename())));
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "loadFromStorage: " + e.getMessage() );
        }
    }
}
