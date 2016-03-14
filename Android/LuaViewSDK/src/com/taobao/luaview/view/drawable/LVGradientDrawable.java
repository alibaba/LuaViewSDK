package com.taobao.luaview.view.drawable;

import android.graphics.drawable.GradientDrawable;

/**
 * 保存颜色跟半径
 *
 * @author song
 * @date 15/10/27
 */
public class LVGradientDrawable extends GradientDrawable {
    private float mCornerRadius;
    private int mColor;
    private int mStrokeWidth;//边框宽
    private int mStrokeColor;//边框颜色

    public void setStrokeWidth(int width){
        setStroke(width, mStrokeColor);
    }

    public void setStrokeColor(int color){
        setStroke(mStrokeWidth, color);
    }

    @Override
    public void setStroke(int width, int color) {
        mStrokeWidth = width;
        mStrokeColor = color;
        super.setStroke(width, color);
    }

    @Override
    public void setCornerRadius(float radius) {
        this.mCornerRadius = radius;
        super.setCornerRadius(radius);
    }

    @Override
    public void setColor(int color) {
        this.mColor = color;
        super.setColor(color);
    }

    public float getCornerRadius() {
        return mCornerRadius;
    }

    public int getColor() {
        return mColor;
    }

    public int getStrokeWidth() {
        return mStrokeWidth;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }
}
