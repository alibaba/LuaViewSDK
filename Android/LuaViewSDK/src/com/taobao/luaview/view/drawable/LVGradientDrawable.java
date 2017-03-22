/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

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

    private float mDashWidth;//虚线尺寸
    private float mDashGap;

    public void setStrokeWidth(int width) {
        setStroke(width, mStrokeColor);
    }

    public void setStrokeColor(int color) {
        setStroke(mStrokeWidth, color);
    }

    public void setDashSize(Float dashWidth, Float dashGap) {//set dash size and dash gap
        super.setStroke(mStrokeWidth, mStrokeColor, dashWidth != null ? dashWidth : 0, dashGap != null ? dashGap : 0);
    }

    @Override
    public void setStroke(int width, int color, float dashWidth, float dashGap) {
        mStrokeWidth = width;
        mStrokeColor = color;
        mDashWidth = dashWidth;
        mDashGap = dashGap;
        super.setStroke(width, color, dashWidth, dashGap);
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

    public float getDashWidth() {
        return mDashWidth;
    }

    public float getDashGap() {
        return mDashGap;
    }
}
