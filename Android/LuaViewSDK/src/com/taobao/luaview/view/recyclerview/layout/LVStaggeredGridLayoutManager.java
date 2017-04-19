/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.recyclerview.layout;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.taobao.luaview.util.AndroidUtil;

/**
 *
 * Staggered Grid LayoutManager for RecyclerView
 * @author song
 * @date 15/11/30
 */
public class LVStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    /**
     * 用屏幕宽度dp，来表示有多少列，模拟iOS的自动放置位置，默认纵向布局
     *
     * @param context
     */
    public LVStaggeredGridLayoutManager(Context context) {
        this(AndroidUtil.getScreenWidthInDp(context), StaggeredGridLayoutManager.VERTICAL);
    }

    public LVStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }
}
