/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.indicator;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.taobao.android.luaview.R;
import com.taobao.luaview.global.Constants;
import com.taobao.luaview.userdata.base.UDLuaTable;
import com.taobao.luaview.userdata.indicator.UDCustomViewPagerIndicator;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.LVViewGroup;
import com.taobao.luaview.view.indicator.circle.IcsLinearLayout;
import com.taobao.luaview.view.indicator.circle.PageIndicator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * LuaView-ViewPagerIndicator
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class LVCustomViewPagerIndicator extends HorizontalScrollView implements ILVView, PageIndicator {
    private LuaValue mInitParams;
    private UDCustomViewPagerIndicator mLuaUserdata;

    //数据
    private final IcsLinearLayout mLayout;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private Runnable mSelector;
    private int mSelectedIndex;

    public LVCustomViewPagerIndicator(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mInitParams = varargs != null ? varargs.arg1() : null;
        this.mLuaUserdata = new UDCustomViewPagerIndicator(this, globals, metaTable, this.mInitParams);
        this.mLayout = new IcsLinearLayout(globals.getContext(), R.attr.lv_vpiIconPageIndicatorStyle);
        init();
    }

    private void init() {
        this.setHorizontalScrollBarEnabled(false);
        super.addView(mLayout, LuaViewUtil.createRelativeLayoutParamsMM());
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    /**
     * 移动到某个位置
     *
     * @param position
     */
    private void animateTo(final int position) {
        final View iconView = mLayout.getChildAt(position);
        if (mSelector != null) {
            removeCallbacks(mSelector);
        }
        mSelector = new Runnable() {
            public void run() {
                final int scrollPos = iconView.getLeft() - (getWidth() - iconView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mSelector = null;
            }
        };
        post(mSelector);
    }


    /**
     * 创建一个item
     *
     * @param pos
     * @return
     */
    private View createAndRenderView(int pos) {
        final int currentItem = mViewPager.getCurrentItem();
        final LuaValue cellData = createView(pos, currentItem);
        if (cellData instanceof UDLuaTable) {
            mLuaUserdata.callCellLayout(cellData, pos, currentItem);//绘制
            return ((UDLuaTable) cellData).getView();
        }
        return null;
    }

    /**
     * create view
     *
     * @param pos
     * @return
     */
    private LuaValue createView(int pos, int currentItem) {
        Globals globals = this.mLuaUserdata.getGlobals();
        //View封装
        final LVViewGroup container = createCellLayout();
        final UDViewGroup cell = new UDViewGroup(container, globals, null);
        //对外数据封装，必须使用LuaTable
        final UDLuaTable cellData = new UDLuaTable(cell);

        //call init

        globals.saveContainer(container);
        this.mLuaUserdata.callCellInit(cellData, pos, currentItem);//初始化
        globals.restoreContainer();

        //set tag
        View view = cellData.getView();
        if (view != null) {
            view.setTag(Constants.RES_LV_TAG, cellData);
        }
        return cellData;
    }


    /**
     * 创建 cell 的布局
     *
     * @return
     */
    private LVViewGroup createCellLayout() {
        return new LVViewGroup(mLuaUserdata.getGlobals(), mInitParams.getmetatable(), null);
    }

    //-----------------------------------------page indicator方法------------------------------------

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mSelector != null) {
            post(mSelector);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSelector != null) {
            removeCallbacks(mSelector);
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        view.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mSelectedIndex = item;
        mViewPager.setCurrentItem(item);

        int tabCount = mLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            View child = mLayout.getChildAt(i);
            boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateTo(item);
            }

            //TODO 这里需要优化一下，实现的太低效，有bug
            Object obj = child.getTag(Constants.RES_LV_TAG);
            if (obj instanceof LuaValue) {
                LuaValue cellData = (LuaValue) obj;
                mLuaUserdata.callCellLayout(cellData, i, item);//绘制
            }
        }
    }

    public int getCurrentItem() {
        return mViewPager != null ? mViewPager.getCurrentItem() : 0;
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mListener = onPageChangeListener;
    }

    @Override
    public void notifyDataSetChanged() {
        mLayout.removeAllViews();
        PagerAdapter adapter = mViewPager.getAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View view = createAndRenderView(i);
            mLayout.addView(view);
        }
        if (mSelectedIndex > count) {
            mSelectedIndex = count - 1;
        }
        setCurrentItem(mSelectedIndex);
        requestLayout();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }
}
