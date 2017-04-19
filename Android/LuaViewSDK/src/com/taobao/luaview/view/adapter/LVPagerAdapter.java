/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.taobao.luaview.userdata.base.UDLuaTable;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.userdata.ui.UDViewPager;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.LVViewGroup;

import org.luaj.vm2.Globals;

import java.lang.ref.WeakReference;

/**
 * Pager Adapter
 *
 * @author song
 * @date 15/9/17
 */
public class LVPagerAdapter extends PagerAdapter {
    private UDViewPager mInitProps;
    private Globals mGlobals;

    private SparseArray<WeakReference<View>> mViews;

    public LVPagerAdapter(Globals globals, UDViewPager udListView) {
        this.mGlobals = globals;
        this.mInitProps = udListView;
        this.mViews = new SparseArray<WeakReference<View>>();
    }

    @Override
    public int getCount() {
        return this.mInitProps.getPageCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.mInitProps.getPageTitle(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return newItem(container, position);
    }

    public Object newItem(ViewGroup container, int position) {
        //View封装
        final UDView page = new UDViewGroup(createPageLayout(), mGlobals, null);//TODO 为什么用mLuaUserData.getmetatable()不行
        //对外数据封装，必须使用LuaTable
        final UDLuaTable pageData = new UDLuaTable(page);
        final View pageView = pageData.getView();
        //添加view
        if(container != null && pageView != null) {
            container.addView(pageView);
        }
        //初始化View
        initView(pageData, position);
        //绘制数据
        renderView(pageData, position);
        //add to list
        mViews.put(position, new WeakReference<View>(pageView));
        return pageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        removeItem(container, position, object);
    }

    /**
     * remove item from container
     *
     * @param container
     * @param position
     * @param object
     */
    private void removeItem(ViewGroup container, int position, Object object) {
        if (container != null && object instanceof View) {
            container.removeView((View) object);
        }
    }

    /**
     * 创建一个layout params，并不能改变view的大小
     *
     * @param container
     * @return
     */
    private RelativeLayout.LayoutParams createLayoutParams(ViewGroup container) {
        final RelativeLayout.LayoutParams layoutParams = LuaViewUtil.createRelativeLayoutParamsWM();
        if(container != null) {
            layoutParams.width = container.getMeasuredWidth();
            layoutParams.height = container.getMeasuredHeight();
        }
        return layoutParams;
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

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
