package com.taobao.luaview.view.recyclerview.decoration;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Divider Item for RecyclerView
 * @author song
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpacing;
    private int mOrientation;

    public DividerItemDecoration(int spacing) {
        this(null, spacing, LinearLayoutManager.VERTICAL);
    }

    public DividerItemDecoration(int spacing, int orientation) {
        this(null, spacing, orientation);
    }

    public DividerItemDecoration(Drawable divider, int spacing, int orientation) {
        this.mSpacing = spacing;
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != LinearLayoutManager.HORIZONTAL && orientation != LinearLayoutManager.VERTICAL) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mSpacing);
        } else {
            outRect.set(0, 0, mSpacing, 0);
        }
    }
}