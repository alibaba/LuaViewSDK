/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.taobao.luaview.userdata.base.UDLuaTable;
import com.taobao.luaview.userdata.list.UDBaseListView;
import com.taobao.luaview.userdata.list.UDListView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.adapter.LVListViewAdapter;
import com.taobao.luaview.view.interfaces.ILVListView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView - ListView
 *
 * @author song
 * @date 15/8/20
 */
public class LVListView extends ListView implements ILVListView {
    private UDBaseListView mLuaUserdata;

    //adapter
    private LVListViewAdapter mAdapter;

    //header & footer container
    private LVViewGroup mHeaderContainer;
    private LVViewGroup mFooterContainer;

    public LVListView(Globals globals, LuaValue metaTable, Varargs varargs, UDBaseListView udBaseListView) {
        super(globals.getContext());
        this.mLuaUserdata = udBaseListView != null ? udBaseListView : new UDListView(this, globals, metaTable, varargs);
        init(globals);
    }

    private void init(Globals globals) {
        globals.saveContainer(this);
        initData(globals);
        globals.restoreContainer();
    }

    /**
     * 初始化子元素
     */
    private void initData(Globals globals) {
//        initHeaderContainer();
//        initFooterContainer();
        mAdapter = new LVListViewAdapter(globals, mLuaUserdata);
        this.setAdapter(mAdapter);
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UDLuaTable cellData = (UDLuaTable) view.getTag();
                final int row = position - LVListView.this.getHeaderViewsCount();
                mAdapter.onCellClicked(cellData, row);
            }
        });
        this.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final UDLuaTable cellData = (UDLuaTable) view.getTag();
                final int row = position - LVListView.this.getHeaderViewsCount();
                return mAdapter.onCellLongClicked(cellData, row);
            }
        });
        this.setSelector(android.R.color.transparent);
        this.setDivider(new ColorDrawable(Color.TRANSPARENT));//设置divider颜色透明
        mLuaUserdata.initOnScrollCallback(this);
    }


    private void initHeaderContainer() {
        mHeaderContainer = new LVViewGroup(mLuaUserdata.getGlobals(), mLuaUserdata.getmetatable(), null);
        mHeaderContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.addHeaderView(mHeaderContainer);
        this.setHeaderDividersEnabled(false);
    }

    private void initFooterContainer() {
        mFooterContainer = new LVViewGroup(mLuaUserdata.getGlobals(), mLuaUserdata.getmetatable(), null);
        mFooterContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.addFooterView(mFooterContainer);
        this.setFooterDividersEnabled(false);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {

    }

    @Override
    public BaseAdapter getLVAdapter() {
        return mAdapter;
    }

    @Override
    public void addHeader(View view) {
        if (mHeaderContainer == null) {
            initHeaderContainer();
        }
        LuaViewUtil.addView(mHeaderContainer, view, null);
    }

    @Override
    public void removeHeader() {
        if (mHeaderContainer != null) {
            mHeaderContainer.removeAllViews();
        }
    }

    @Override
    public void addFooter(View view) {
        if (mFooterContainer != null) {
            initFooterContainer();
        }
        LuaViewUtil.addView(mFooterContainer, view, null);
    }

    @Override
    public void removeFooter() {
        if (mFooterContainer != null) {
            mFooterContainer.removeAllViews();
        }
    }
}
