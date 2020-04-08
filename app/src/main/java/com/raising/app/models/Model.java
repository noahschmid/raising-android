package com.raising.app.models;

import android.graphics.Bitmap;

public interface Model {
    long getId();
    String getName();

    //TODO: change to needed format
    Bitmap getImage();

    void setId(long id);
    void setName(String name);

    @Override
    public boolean equals(Object o);
}
