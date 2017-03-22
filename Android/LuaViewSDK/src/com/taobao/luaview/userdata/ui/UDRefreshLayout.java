/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.util.Log;
import android.view.ViewGroup;

import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVRefreshLayout;
import com.taobao.luaview.view.widget.SuperSwipeRefreshLayout;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Created by tuoli on 12/20/16.
 */

public class UDRefreshLayout extends UDViewGroup<LVRefreshLayout> {

    public UDRefreshLayout(LVRefreshLayout view, Globals globals, LuaValue metaTable, LuaValue initParams) {
        super(view, globals, metaTable, initParams);
        init();
    }

    private void init() {
        LVRefreshLayout v = getView();
        if (v != null) {
            v.setOnRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
                @Override
                public void onRefresh() {
                    if (LuaUtil.isValid(mCallback)) {
                        LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "Refreshing", "refreshing"));
                    }
                }

                @Override
                public void onPullDistance(boolean notify, int distance) {
                    if (LuaUtil.isValid(mCallback)) {
                        if (distance >= 0) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "Scrolling", "scrolling"), LuaUtil.toLuaBoolean(notify), LuaUtil.toLuaInt((int)DimenUtil.pxToDpi(distance)));
                        }
                    }
                }

                @Override
                public void onPullEnable(boolean enable) {
                }
            });
        }
    }

    /**
     * 获取容器view
     * @return
     */
    public ViewGroup getContainer(){
        return getView() != null ? getView().getContainer() : null;
    }

    public void stopRefreshing() {
        getView().setRefreshing(false);
    }

    public void setRefreshingOffset(float offset) {
        getView().setRefreshingOffset(offset);
    }
}

