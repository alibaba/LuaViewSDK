/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.taobao.luaview.fun.mapper.ui.UIViewGroupMethodMapper;
import com.taobao.luaview.global.LuaViewManager;
import com.taobao.luaview.userdata.base.UDLuaTable;
import com.taobao.luaview.userdata.list.UDBaseListView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.view.LVViewGroup;
import com.taobao.luaview.view.foreground.ForegroundDelegate;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * LVListView 的适配器
 *
 * @author song
 * @date 15/8/31
 */
public class LVListViewAdapter extends BaseAdapter {
    private static final String KEY_VIEW_TYPE = "_lv_key_view_type";
    private UDBaseListView mLuaUserData;
    private Globals mGlobals;

    public LVListViewAdapter(Globals globals, UDBaseListView udListView) {
        this.mGlobals = globals;
        this.mLuaUserData = udListView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * LuaView中不需要该方法，数据都是在获取的时候设置的，并不需要该方法
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * LuaView中不需要该方法
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * TODO 最好有方法返回所有count，而不是分section返回
     *
     * @re
     */
    @Override
    public int getCount() {
        return this.mLuaUserData.getTotalCount();
    }


    /**
     * TODO 根据位置获得view type
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mLuaUserData.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return this.mLuaUserData.getViewTypeCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        LogUtil.d("yesong", position, convertView, getItemViewType(position), getViewTypeCount());
        //数据封装
        UDLuaTable cellData = null;
        final int viewType = getItemViewType(position);
        final boolean hasCellSize = this.mLuaUserData.hasCellSize(position);
        if (convertView == null || ((UDLuaTable) convertView.getTag()).get(KEY_VIEW_TYPE) != LuaValue.valueOf(viewType)) {//在内部创建好Cell
            UDView layout = new UDViewGroup(createLayout(), mGlobals, null);//TODO 为什么用mLuaUserData.getmetatable()不行
            //对外数据封装，必须使用LuaTable
            cellData = new UDLuaTable(layout);
            //View封装
            if (hasCellSize) {//有Size的定义
                final LVViewGroup cellView = createLayout();
                View tmp = layout.getView();
                if(tmp != null) {
                    cellView.addView(tmp);
                }
                convertView = cellView;
            } else {
                convertView = layout.getView();
            }
            initView(cellData, position);
            convertView.setTag(cellData);
        } else {
            cellData = (UDLuaTable) convertView.getTag();
        }

        //更新position
        cellData.set(KEY_VIEW_TYPE, viewType);
        if (hasCellSize) {//有Size的定义，每次更新size
            initCellSize(cellData, position);//TODO 需要动态更新View的Size，需要在这里调用，否则移动到初始化的时候。这个暂时先去掉，会有问题，复用有问题
        }

        //绘制数据
        renderView(cellData, position);

        return convertView;
    }


    //--------------------------------------public methods------------------------------------------

    /**
     * 给view设置点击动作
     *
     * @param cell
     * @param position
     */
    public void onCellClicked(UDLuaTable cell, int position) {
        this.mLuaUserData.onCellClicked(cell, position);
    }

    /**
     * 给view设置长按动作
     *
     * @param cell
     * @param position
     */
    public boolean onCellLongClicked(UDLuaTable cell, int position) {
        return this.mLuaUserData.onCellLongClicked(cell, position);
    }
    //-------------------------------------private methods------------------------------------------

    /**
     * 创建 cell 的布局
     *
     * @return
     */
    private LVViewGroup createLayout() {
        return new LVViewGroup(mGlobals, mLuaUserData.getmetatable(), null);
    }

    /**
     * 调用LuaView的Init方法进行Cell的初始化
     *
     * @param position
     * @return
     */
    private void initCellSize(UDLuaTable cell, int position) {
        final View view = cell.getView();
        if (view != null) {
            int[] size = mLuaUserData.callCellSize(cell, position);

            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            layoutParams.width = size[0];
            layoutParams.height = size[1];
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 调用LuaView的Init方法进行Cell的初始化
     *
     * @param position
     * @return
     */
    private void initView(UDLuaTable cell, int position) {
        this.mGlobals.saveContainer(cell.getLVViewGroup());
        this.mLuaUserData.callCellInit(cell, position);
        this.mGlobals.restoreContainer();
    }

    /**
     * 调用LuaView的Layout方法进行数据填充
     *
     * @param cell
     * @param position
     */
    private void renderView(UDLuaTable cell, int position) {
        this.mGlobals.saveContainer(cell.getLVViewGroup());
        this.mLuaUserData.callCellLayout(cell, position);
        this.mGlobals.restoreContainer();
    }
}
