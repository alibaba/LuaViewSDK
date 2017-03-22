/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.view.View;
import android.view.ViewGroup;

import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVHorizontalScrollView;
import com.taobao.luaview.view.LVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * 容器类-ListView，模拟OC的section分区实现，Section顺序排列
 *
 * @author song
 * @date 15/8/20
 */
public class UDHorizontalScrollView extends UDViewGroup<LVHorizontalScrollView> {

    public UDHorizontalScrollView(LVHorizontalScrollView view, Globals globals, LuaValue metaTable, LuaValue initParams) {
        super(view, globals, metaTable, initParams);
    }

    /**
     * 获取容器view
     * @return
     */
    public ViewGroup getContainer(){
        return getView() != null ? getView().getContainer() : null;
    }

    /**
     * 调用 Cell 的某些方法
     *
     * @param method
     * @param position
     * @return
     */
    private LuaValue callCellFunction(String method, LuaValue cellData, int position) {
        return LuaUtil.callFunction(LuaUtil.getValue(initParams, method), cellData, LuaUtil.toLuaInt(position));
    }

    /**
     * 滚动到某个位置
     *
     * @param x
     * @param y
     * @return
     */
    public UDHorizontalScrollView smoothScrollTo(final int x, final int y) {
        final LVHorizontalScrollView scrollView = getView();
        if (scrollView != null) {
            scrollView.smoothScrollTo(x, y);
        }
        return this;
    }

    /**
     * 滚动dx and dy
     *
     * @param dx
     * @param dy
     * @return
     */
    public UDHorizontalScrollView smoothScrollBy(final int dx, final int dy) {
        final LVHorizontalScrollView scrollView = getView();
        if (scrollView != null) {
            scrollView.smoothScrollBy(dx, dy);
        }
        return this;
    }

    /**
     * 滚动一页 direction (>0, <0)
     *
     * @param direction
     * @return
     */
    public UDHorizontalScrollView pageScroll(final int direction) {
        final LVHorizontalScrollView scrollView = getView();
        if (scrollView != null) {
            if (direction > 0) {
                scrollView.pageScroll(View.FOCUS_RIGHT);
            } else if (direction < 0) {
                scrollView.pageScroll(View.FOCUS_LEFT);
            }
        }
        return this;
    }

    /**
     * 滚动到底
     *
     * @param direction (>0, <0)
     * @return
     */
    public UDHorizontalScrollView fullScroll(final int direction) {
        final LVHorizontalScrollView scrollView = getView();
        if (scrollView != null) {
            if (direction > 0) {
                scrollView.fullScroll(View.FOCUS_RIGHT);
            } else if (direction < 0) {
                scrollView.fullScroll(View.FOCUS_LEFT);
            }
        }
        return this;
    }
}
