package com.taobao.luaview.view.foreground;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Relative Layout can set foreground drawable
 */
public class ForegroundRelativeLayout extends RelativeLayout implements IForeground {
    private ForegroundDelegate mForegroundDelegate;
    private boolean enableForeground;

    public ForegroundRelativeLayout(Context context) {
        this(context, null);
    }

    public ForegroundRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ForegroundRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate = new ForegroundDelegate();
            mForegroundDelegate.init(context, attrs, defStyleAttr, defStyleRes);
        }
    }


    @Override
    public int getForegroundGravity() {
        if (!enableForeground || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getForegroundGravity();
        }
        return mForegroundDelegate.getForegroundGravity();
    }

    @Override
    public void setForegroundGravity(int foregroundGravity) {
        if (!enableForeground || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setForegroundGravity(foregroundGravity);
        } else {
            mForegroundDelegate.setForegroundGravity(this, foregroundGravity);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return super.verifyDrawable(who) || (who == mForegroundDelegate.getForeground());
        } else {
            return super.verifyDrawable(who);
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.jumpDrawablesToCurrentState();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.drawableStateChanged(this);
        }
    }


    @Override
    public void setForeground(Drawable foreground) {
        enableForeground = foreground != null;
        if (!enableForeground || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setForeground(foreground);
        } else {
            mForegroundDelegate.setForeground(this, foreground);
        }
    }

    @Override
    public Drawable getForeground() {
        if (!enableForeground || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getForeground();
        } else {
            return mForegroundDelegate.getForeground();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.draw(this, canvas);
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.drawableHotspotChanged(x, y);
        }
    }
}