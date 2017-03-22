/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.foreground;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

/**
 * ImageView can set foreground
 */
public class ForegroundImageView extends ImageView implements IForeground {

    private ForegroundDelegate mForegroundDelegate;
    private boolean enableForeground;

    public ForegroundImageView(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate = new ForegroundDelegate();
        }
    }


    @Override
    public int getForegroundGravity() {
        //LogUtil.d("Foreground", "getForegroundGravity", enableForeground);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return mForegroundDelegate.getForegroundGravity();
        } else {
            return super.getForegroundGravity();
        }
    }

    @Override
    public void setForegroundGravity(int foregroundGravity) {
        //LogUtil.d("Foreground", "setForegroundGravity", foregroundGravity, enableForeground);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.setForegroundGravity(this, foregroundGravity);
        } else {
            super.setForegroundGravity(foregroundGravity);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        //LogUtil.d("Foreground", "verifyDrawable", who, enableForeground, super.verifyDrawable(who), (who == mForegroundDelegate.getForeground()));
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return super.verifyDrawable(who) || (who == mForegroundDelegate.getForeground());
        } else {
            return super.verifyDrawable(who);
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        //LogUtil.d("Foreground", "jumpDrawablesToCurrentState", enableForeground);
        super.jumpDrawablesToCurrentState();
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.jumpDrawablesToCurrentState();
        }
    }

    @Override
    protected void drawableStateChanged() {
        //LogUtil.d("Foreground", "drawableStateChanged", enableForeground);
        super.drawableStateChanged();
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.drawableStateChanged(this);
        }
    }


    @Override
    public void setForeground(Drawable foreground) {
        //LogUtil.d("Foreground", "setForeground", enableForeground);
        enableForeground = foreground != null;
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.setForeground(this, foreground);
        } else {
            super.setForeground(foreground);
        }
    }

    @Override
    public void clearForeground() {
        //LogUtil.d("Foreground", "clearForeground");
        enableForeground = false;
    }

    @Override
    public Drawable getForeground() {
        //LogUtil.d("Foreground", "getForeground", enableForeground);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return mForegroundDelegate.getForeground();
        } else {
            return super.getForeground();
        }
    }

    @Override
    public boolean hasForeground() {
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return mForegroundDelegate.getForeground() != null;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getForeground() != null;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //LogUtil.d("Foreground", "onLayout", enableForeground, changed);
        super.onLayout(changed, left, top, right, bottom);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //LogUtil.d("Foreground", "onSizeChanged", enableForeground);
        super.onSizeChanged(w, h, oldw, oldh);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        //LogUtil.d("Foreground", "draw", enableForeground);
        super.draw(canvas);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.draw(this, canvas);
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        //LogUtil.d("Foreground", "drawableHotspotChanged", enableForeground);
        super.drawableHotspotChanged(x, y);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.drawableHotspotChanged(x, y);
        }
    }
}