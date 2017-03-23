/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.foreground;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.taobao.android.luaview.R;

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

    public ForegroundDelegate() {
    }

    public static void setupDefaultForeground(View view) {
        setupDefaultForeground(view, null, null);
    }

    public static void setupDefaultForeground(View view, Integer color, Integer alpha) {
        if (view instanceof IForeground && ((IForeground) view).hasForeground() == false && view.getResources() != null) {
            setupForeground(view, view.getResources().getDrawable(R.drawable.lv_click_foreground), color, alpha);
        }
    }

    private static void setupForeground(View view, Drawable drawable, Integer color, Integer alpha) {
        if (view instanceof IForeground) {
            if (color != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (drawable instanceof RippleDrawable) {
                        RippleDrawable rippleDrawable = (RippleDrawable) drawable;
                        rippleDrawable.setColor(ColorStateList.valueOf(color));
                        if (alpha != null) {
                            rippleDrawable.setAlpha(alpha);
                        }

                    }
                }
            }
            ((IForeground) view).setForeground(drawable);
        }
    }

    public static void clearForeground(View view) {
        if (view instanceof IForeground) {
            ((IForeground) view).clearForeground();
        }
    }

    public void init(Context context) {
    }

    public void init(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
    }

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mForegroundBoundsChanged = true;
//        mForegroundBoundsChanged = changed;
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
    public void setForeground(View view, Drawable drawable) {
        if (view != null) {
            if (mForeground != drawable) {
                if (mForeground != null) {
                    mForeground.setCallback(null);
                    view.unscheduleDrawable(mForeground);
                }

                mForeground = drawable;

                if (drawable != null) {
                    view.setWillNotDraw(false);
                    drawable.setCallback(view);
                    if (drawable.isStateful()) {
                        drawable.setState(view.getDrawableState());
                    }
                    if (mForegroundGravity == Gravity.FILL) {
                        Rect padding = new Rect();
                        drawable.getPadding(padding);
                    }

                    //update bounds
                    updateBounds(view, drawable);//added by song
                } else {
                    view.setWillNotDraw(true);
                }
                view.requestLayout();
                view.invalidate();
            }
        }
    }

    public int getForegroundGravity() {
        return mForegroundGravity;
    }

    public void setForegroundGravity(View view, int foregroundGravity) {
        if (view != null) {
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

                view.requestLayout();
            }
        }
    }

    public void jumpDrawablesToCurrentState() {
        if (mForeground != null) mForeground.jumpToCurrentState();
    }

    public void drawableStateChanged(View view) {
        if (view != null) {
            if (mForeground != null && mForeground.isStateful()) {
                mForeground.setState(view.getDrawableState());
            }
        }
    }

    public void draw(View view, Canvas canvas) {
        if (view != null) {
            if (mForeground != null) {
                final Drawable foreground = mForeground;

                if (mForegroundBoundsChanged) {
                    mForegroundBoundsChanged = false;
                    updateBounds(view, foreground);
                }

                foreground.draw(canvas);
            }
        }
    }

    public void updateBounds(View view) {
        updateBounds(view, mForeground);
    }

    /**
     * 更新bounds
     *
     * @param view
     * @param drawable
     */
    private void updateBounds(View view, Drawable drawable) {
        if (drawable != null) {
            final Rect selfBounds = mSelfBounds;
            final Rect overlayBounds = mOverlayBounds;

            final int w = view.getRight() - view.getLeft();
            final int h = view.getBottom() - view.getTop();

            if (mForegroundInPadding) {
                selfBounds.set(0, 0, w, h);
            } else {
                selfBounds.set(view.getPaddingLeft(), view.getPaddingTop(), w - view.getPaddingRight(), h - view.getPaddingBottom());
            }

            Gravity.apply(mForegroundGravity, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), selfBounds, overlayBounds);
            drawable.setBounds(overlayBounds);
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