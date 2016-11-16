package com.taobao.luaview.view.imageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.taobao.luaview.view.drawable.LVGradientDrawable;
import com.taobao.luaview.view.foreground.ForegroundImageView;

/**
 * Base ImageView
 *
 * @author song
 * @date 16/3/9
 */
public abstract class BaseImageView extends ForegroundImageView {
    protected boolean mAttachedWindow = false;
    protected boolean isNetworkMode = false;

    private LVGradientDrawable mStyleDrawable = null;
    private Path mPath = null;

    public void setIsNetworkMode(boolean isNetworkMode) {
        this.isNetworkMode = isNetworkMode;
    }

    /**
     * 图片加载回调
     */
    public interface LoadCallback {
        //drawable = null 表示失败，drawable != null表示成功
        void onLoadResult(Drawable drawable);
    }

    public BaseImageView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedWindow = false;
    }

    public abstract void loadUrl(final String url, final LoadCallback callback);

    public abstract String getUrl();

    public abstract void setUrl(String url);


    @Override
    protected void onDraw(Canvas canvas) {
        final boolean hasStyle = setupStyleDrawable();
        if (hasStyle && canvas != null) {
            canvas.clipPath(getClipPath());
        }

        super.onDraw(canvas);

        if (hasStyle) {//背景放到上面画
            mStyleDrawable.draw(canvas);
            mStyleDrawable.getBounds();
        }
    }

    //-------------------------------------background style-----------------------------------------

    /**
     * get clip path of StyleDrawable
     *
     * @return
     */
    private Path getClipPath() {
        if (mPath == null) {
            mPath = new Path();
        }
        final Rect rect = mStyleDrawable.getBounds();
        final float radius = mStyleDrawable.getCornerRadius();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPath.addRoundRect(rect.left, rect.top, rect.right, rect.bottom, radius, radius, Path.Direction.CW);
        } else {
            mPath.addCircle(rect.left + radius, rect.top + radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.right - radius, rect.top + radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.right - radius, rect.bottom - radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.left + radius, rect.bottom - radius, radius, Path.Direction.CW);
            mPath.addRect(rect.left + radius, rect.top, rect.right - radius, rect.bottom, Path.Direction.CW);
            mPath.addRect(rect.left, rect.top + radius, rect.right, rect.bottom - radius, Path.Direction.CW);
        }
        return mPath;
    }


    /**
     * 设置好drawable的样式
     *
     * @return
     */
    private boolean setupStyleDrawable() {
        if (mStyleDrawable != null) {
            mStyleDrawable.setBounds(0, 0, getWidth(), getHeight());
            return true;
        }
        return false;
    }

    private synchronized LVGradientDrawable getStyleDrawable() {
        if (mStyleDrawable == null) {
            mStyleDrawable = new LVGradientDrawable();
        }
        return mStyleDrawable;
    }

    /**
     * set corner radius
     *
     * @param radius
     */
    public void setCornerRadius(float radius) {
        getStyleDrawable().setCornerRadius(radius);
    }

    public float getCornerRadius() {
        if (mStyleDrawable != null) {
            return mStyleDrawable.getCornerRadius();
        }
        return 0;
    }

    /**
     * 设置边框宽度
     */
    public void setStrokeWidth(int width) {
        getStyleDrawable().setStrokeWidth(width);
    }

    public int getStrokeWidth() {
        return mStyleDrawable != null ? mStyleDrawable.getStrokeWidth() : 0;
    }

    /**
     * 设置边框颜色
     *
     * @param color
     */
    public void setStrokeColor(int color) {
        getStyleDrawable().setStrokeColor(color);
    }

    public int getStrokeColor() {
        return mStyleDrawable != null ? mStyleDrawable.getStrokeColor() : 0;
    }

    /**
     * Dash size
     *
     * @param dashWidth
     * @param dashGap
     */
    public void setBorderDash(Float dashWidth, Float dashGap) {
        getStyleDrawable().setDashSize(dashWidth, dashGap);
    }

    public float getBorderDashWidth() {
        return mStyleDrawable != null ? mStyleDrawable.getDashWidth() : 0;
    }

    public float getBorderDashGap() {
        return mStyleDrawable != null ? mStyleDrawable.getDashGap() : 0;
    }

}
