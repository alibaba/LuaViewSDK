/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.taobao.android.luaview.R;
import com.taobao.luaview.userdata.list.UDBaseRecyclerView;
import com.taobao.luaview.userdata.list.UDRecyclerView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVRecyclerView;
import com.taobao.luaview.view.recyclerview.LVRecyclerViewAdapter;
import com.taobao.luaview.view.recyclerview.RecyclerViewHelper;
import com.taobao.luaview.view.recyclerview.decoration.DividerGridItemDecoration;
import com.taobao.luaview.view.recyclerview.decoration.DividerItemDecoration;
import com.taobao.luaview.view.recyclerview.layout.LVGridLayoutManager;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView - RecyclerView
 *
 * @author song
 * @date 15/8/20
 */
public class LVRecyclerView extends RecyclerView implements ILVRecyclerView {
    private UDBaseRecyclerView mLuaUserdata;

    //adapter
    private RecyclerView.Adapter mAdapter;
    private LayoutManager mLayoutManager;
    private ItemDecoration mItemDecoration;
    private int mSpacing = 0;//间隔

    public static LVRecyclerView createVerticalView(Globals globals, LuaValue metaTable, Varargs varargs, UDBaseRecyclerView udBaseRecyclerView) {
        final LVRecyclerView lvRecyclerView = (LVRecyclerView) LayoutInflater.from(globals.getContext()).inflate(R.layout.lv_recyclerview_vertical, null);
        return lvRecyclerView.init(globals, metaTable, varargs, udBaseRecyclerView);
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     */
    public LVRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LVRecyclerView init(Globals globals, LuaValue metaTable, Varargs varargs, UDBaseRecyclerView udBaseRecyclerView) {
        LuaViewUtil.setId(this);
        this.mLuaUserdata = udBaseRecyclerView != null ? udBaseRecyclerView : new UDRecyclerView(this, globals, metaTable, varargs);
        init(globals);
        return this;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //改变大小的时候需要更新spanCount & spanSize
        updateMaxSpanCount();
    }

    private void init(Globals globals) {
        mAdapter = new LVRecyclerViewAdapter(globals, mLuaUserdata);
        this.setAdapter(mAdapter);
        mLayoutManager = new LVGridLayoutManager(this);
        this.setLayoutManager(mLayoutManager);
        mLuaUserdata.initOnScrollCallback(this);
        this.setHasFixedSize(true);
        initViewHolderPool();
    }

    /**
     * 初始化ViewHolder缓存池
     */
    private void initViewHolderPool() {
        //设置ViewHolder缓存的数
        final RecycledViewPool pool = getRecycledViewPool();
        if (pool != null) {
            for (int i = 0; i < 100; i++) {
                pool.setMaxRecycledViews(i, 10);
            }
        }
    }

    /**
     * 更新最大间隔
     */
    public void updateMaxSpanCount() {
        if (mLayoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) mLayoutManager).setSpanCount(mLuaUserdata.getMaxSpanCount());
        } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) mLayoutManager).setSpanCount(mLuaUserdata.getMaxSpanCount());
        }
    }

    public int getSpanSize(int position) {
        return mLuaUserdata.getSpanSize(position);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {

    }

    @Override
    public RecyclerView.Adapter getLVAdapter() {
        return mAdapter;
    }

    //-------------------------------------------list view封装---------------------------------------

    public int getFirstVisiblePosition() {
        return RecyclerViewHelper.getFirstVisiblePosition(this);
    }

    public int getLastVisiblePosition() {
        return RecyclerViewHelper.getLastVisiblePosition(this);
    }

    public int getVisibleItemCount() {
        return RecyclerViewHelper.getVisibleItemCount(this);
    }

    public void setMiniSpacing(int spacing) {
        if (mItemDecoration == null || mSpacing != spacing) {
            this.removeItemDecoration(mItemDecoration);
            mSpacing = spacing;
            mItemDecoration = new DividerGridItemDecoration(spacing);
            this.addItemDecoration(mItemDecoration);
        }
    }

    public int getMiniSpacing() {
        return mSpacing;
    }
}
