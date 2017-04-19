/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.taobao.luaview.userdata.list.UDRefreshRecyclerView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVRecyclerView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView - Refresh Recycler View
 *
 * @author song
 * @date 15/8/20
 */
public class LVRefreshRecyclerView extends SwipeRefreshLayout implements ILVRecyclerView {
    //adapter
    private LVRecyclerView mRecyclerView;

    public LVRefreshRecyclerView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mRecyclerView = LVRecyclerView.createVerticalView(globals, metaTable, varargs, new UDRefreshRecyclerView(this, globals, metaTable, varargs));
        init(globals);
    }

    private void init(Globals globals) {
        globals.saveContainer(mRecyclerView);
        this.addView(mRecyclerView, LuaViewUtil.createRelativeLayoutParamsMM());
        globals.restoreContainer();

        if (!globals.isRefreshContainerEnable) {
            this.setEnabled(false);
        } else {
            ((UDRefreshRecyclerView) getUserdata()).initPullRefresh();
        }
    }

    @Override
    public void setVerticalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        if (mRecyclerView != null) {
            mRecyclerView.setVerticalScrollBarEnabled(horizontalScrollBarEnabled);
        }
    }

    @Override
    public boolean isVerticalScrollBarEnabled() {
        return mRecyclerView != null ? mRecyclerView.isVerticalScrollBarEnabled() : true;
    }

    /**
     * 停止刷新
     */
    public void startPullDownRefreshing() {
        setRefreshing(true);
    }

    /**
     * 停止刷新
     */
    public void stopPullDownRefreshing() {
        setRefreshing(false);
    }

    public LVRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public UDView getUserdata() {
        return mRecyclerView != null ? mRecyclerView.getUserdata() : null;
    }

    @Override
    public RecyclerView.Adapter getLVAdapter() {
        return mRecyclerView != null ? mRecyclerView.getLVAdapter() : null;
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {

    }
}
