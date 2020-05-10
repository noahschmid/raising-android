package com.raising.app.models;

import android.graphics.Bitmap;

public interface Model {
    long getId();
    String getName();
    boolean isChecked();
    void setChecked(boolean checked);

    Bitmap getImage();

    void setId(long id);
    void setName(String name);

    @Override
    public boolean equals(Object o);
}
