package com.raising.app.util.recyclerViewAdapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewMargin extends RecyclerView.ItemDecoration {
    private int margin;

    /**
     * constructor
     * @param margin desirable margin size in px between the views in the recyclerView
     */
    public RecyclerViewMargin(@IntRange(from=0)int margin) {
        this.margin = margin;
    }

    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildLayoutPosition(view);
        if(position != 0) {
            outRect.top = margin;
        }

        outRect.bottom = margin;
    }
}