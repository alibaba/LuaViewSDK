/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taobao.luaview.fun.mapper.ui.UIViewGroupMethodMapper;
import com.taobao.luaview.global.LuaViewManager;
import com.taobao.luaview.userdata.base.UDLuaTable;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.userdata.ui.UDViewPager;
import com.taobao.luaview.view.LVViewGroup;

import org.luaj.vm2.Globals;

/**
 * ViewPager 的 Fragment
 *
 * @author song
 * @date 15/9/18
 */
@SuppressLint("ValidFragment")
public class LVPageFragment extends Fragment {
    private Globals mGlobals;
    private UDViewPager mInitProps;
    private int mPosInViewPager;

    public LVPageFragment(Globals globals, UDViewPager mInitProps, int posInViewPager) {
        this.mGlobals = globals;
        this.mInitProps = mInitProps;
        this.mPosInViewPager = posInViewPager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View封装
        final UDView page = new UDViewGroup(createPageLayout(), mGlobals, null);//TODO 为什么用mLuaUserData.getmetatable()不行
        //对外数据封装，必须使用LuaTable
        final UDLuaTable pageData = new UDLuaTable(page);
        final View pageView = pageData.getView();
        //初始化View
        initView(pageData, mPosInViewPager);
        //绘制数据
        renderView(pageData, mPosInViewPager);
        return pageView;
    }

    /**
     * 调用LuaView的Init方法进行Cell的初始化
     *
     * @param position
     * @return
     */
    private void initView(UDLuaTable page, int position) {
        this.mGlobals.saveContainer(page.getLVViewGroup());
        this.mInitProps.callPageInit(page, position);
        this.mGlobals.restoreContainer();
    }

    /**
     * 调用LuaView的Layout方法进行数据填充
     *
     * @param page
     * @param position
     */
    private void renderView(UDLuaTable page, int position) {
        this.mGlobals.saveContainer(page.getLVViewGroup());
        this.mInitProps.callPageLayout(page, position);
        this.mGlobals.restoreContainer();
    }

    /**
     * 创建 cell 的布局
     *
     * @return
     */
    private LVViewGroup createPageLayout() {
        return new LVViewGroup(mGlobals, mInitProps.getmetatable(), null);
    }
}
