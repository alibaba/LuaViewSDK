/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.indicator;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.indicator.LVCircleViewPagerIndicator;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * 指示器-ViewPagerIndicator
 *
 * @author song
 * @date 15/8/20
 */
public class UDCircleViewPagerIndicator<U extends LVCircleViewPagerIndicator> extends UDView<U> {

    public UDCircleViewPagerIndicator(U view, Globals globals, LuaValue metaTable, Varargs initParams) {
        super(view, globals, metaTable, initParams);
    }

    /**
     * 设置填充颜色
     *
     * @param color
     * @return
     */
    public UDCircleViewPagerIndicator setFillColor(Integer color) {
        if (color != null && getView() != null) {
            getView().setFillColor(color);
        }
        return this;
    }

    /**
     * 获取填充的颜色
     *
     * @return
     */
    public int getFillColor() {
        return getView() != null ? getView().getFillColor() : -1;
    }

    /**
     * 设置页面标示颜色
     *
     * @param color
     * @return
     */
    public UDCircleViewPagerIndicator setPageColor(Integer color) {
        if (color != null && getView() != null) {
            getView().setPageColor(color);
        }
        return this;
    }

    /**
     * 获取页面标示颜色
     *
     * @return
     */
    public int getPageColor() {
        return getView() != null ? getView().getPageColor() : -1;
    }

    /**
     * 设置线条宽度
     *
     * @param strokeWidth
     * @return
     */
    public UDCircleViewPagerIndicator setStrokeWidth(float strokeWidth) {
        if (strokeWidth != -1 && getView() != null) {
            getView().setStrokeWidth(strokeWidth);
        }
        return this;
    }

    /**
     * 获取线条宽度
     *
     * @return
     */
    public float getStrokeWidth() {
        return getView() != null ? getView().getStrokeWidth() : -1.0f;
    }

    /**
     * 设置线条颜色
     *
     * @param color
     * @return
     */
    public UDCircleViewPagerIndicator setStrokeColor(Integer color) {
        if (color != null && getView() != null) {
            getView().setStrokeColor(color);
        }
        return this;
    }

    /**
     * 获取线条颜色
     *
     * @return
     */
    public int getStrokeColor() {
        return getView() != null ? getView().getStrokeColor() : -1;
    }

    /**
     * 设置圆圈半径
     *
     * @param radius
     * @return
     */
    public UDCircleViewPagerIndicator setRadius(float radius) {
        if (radius != -1 && getView() != null) {
            getView().setRadius(radius);
        }
        return this;
    }

    /**
     * 获取半径
     *
     * @return
     */
    public float getRadius() {
        return getView() != null ? getView().getRadius() : -1.0f;
    }

    /**
     * 设置是否需要动画
     *
     * @param snap
     * @return
     */
    public UDCircleViewPagerIndicator setSnap(boolean snap) {
        if (getView() != null) {
            getView().setSnap(snap);
        }
        return this;
    }

    /**
     * 是否需要动画
     *
     * @return
     */
    public boolean isSnap() {
        return getView() != null ? getView().isSnap() : false;
    }

    /**
     * 设置当前页面
     *
     * @param item
     * @return
     */
    public UDCircleViewPagerIndicator setCurrentItem(final int item) {
        if (item != -1 && getView() != null) {
            getView().setCurrentItem(item);
        }
        return this;
    }
}
