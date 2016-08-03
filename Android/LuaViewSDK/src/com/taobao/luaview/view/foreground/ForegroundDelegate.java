package com.taobao.luaview.view.foreground;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * Delegate that actually does the foreground things, so that the logic can be shared between views
 * and so that others can easily create views that support a foreground
 */
public class ForegroundDelegate {

    private Drawable mForeground;

    private final Rect mSelfBounds = new Rect();

    private final Rect mOverlayBounds = new Rect();

    private int mForegroundGravity = Gravity.FILL;

    protected boolean mForegroundInPadding = true;

    boolean mForegroundBoundsChanged = false;

    private View mView;

    public ForegroundDelegate(View view) {
        mView = view;
    }

    public void init(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
    }

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mForegroundBoundsChanged = changed;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mForegroundBoundsChanged = true;
    }

    /**
     * Returns the drawable used as the foreground of this FrameLayout. The
     * foreground drawable, if non-null, is always drawn on top of the children.
     *
     * @return A Drawable or null if no foreground was set.
     */
    public Drawable getForeground() {
        return mForeground;
    }

    /**
     * Supply a Drawable that is to be rendered on top of all of the child
     * views in the frame layout.  Any padding in the Drawable will be taken
     * into account by ensuring that the children are inset to be placed
     * inside of the padding area.
     *
     * @param drawable The Drawable to be drawn on top of the children.
     */
    public void setForeground(Drawable drawable) {
        if (mForeground != drawable) {
            if (mForeground != null) {
                mForeground.setCallback(null);
                mView.unscheduleDrawable(mForeground);
            }

            mForeground = drawable;

            if (drawable != null) {
                mView.setWillNotDraw(false);
                drawable.setCallback(mView);
                if (drawable.isStateful()) {
                    drawable.setState(mView.getDrawableState());
                }
                if (mForegroundGravity == Gravity.FILL) {
                    Rect padding = new Rect();
                    drawable.getPadding(padding);
                }
            } else {
                mView.setWillNotDraw(true);
            }
            mView.requestLayout();
            mView.invalidate();
        }
    }

    public int getForegroundGravity() {
        return mForegroundGravity;
    }

    public void setForegroundGravity(int foregroundGravity) {
        if (mForegroundGravity != foregroundGravity) {
            if ((foregroundGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
                foregroundGravity |= Gravity.START;
            }

            if ((foregroundGravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
                foregroundGravity |= Gravity.TOP;
            }

            mForegroundGravity = foregroundGravity;

            if (mForegroundGravity == Gravity.FILL && mForeground != null) {
                Rect padding = new Rect();
                mForeground.getPadding(padding);
            }

            mView.requestLayout();
        }
    }

    public void jumpDrawablesToCurrentState() {
        if (mForeground != null) mForeground.jumpToCurrentState();
    }

    public void drawableStateChanged() {
        if (mForeground != null && mForeground.isStateful()) {
            mForeground.setState(mView.getDrawableState());
        }
    }

    public void draw(Canvas canvas) {
        if (mForeground != null) {
            final Drawable foreground = mForeground;

            if (mForegroundBoundsChanged) {
                mForegroundBoundsChanged = false;
                final Rect selfBounds = mSelfBounds;
                final Rect overlayBounds = mOverlayBounds;

                final int w = mView.getRight() - mView.getLeft();
                final int h = mView.getBottom() - mView.getTop();

                if (mForegroundInPadding) {
                    selfBounds.set(0, 0, w, h);
                } else {
                    selfBounds.set(mView.getPaddingLeft(), mView.getPaddingTop(), w - mView.getPaddingRight(), h - mView.getPaddingBottom());
                }

                Gravity.apply(mForegroundGravity, foreground.getIntrinsicWidth(),
                        foreground.getIntrinsicHeight(), selfBounds, overlayBounds);
                foreground.setBounds(overlayBounds);
            }
            foreground.draw(canvas);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void drawableHotspotChanged(float x, float y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mForeground != null) {
                mForeground.setHotspot(x, y);
            }
        }
    }

}