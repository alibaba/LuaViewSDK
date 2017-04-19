/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.indicator;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.indicator.LVCustomViewPagerIndicator;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * 指示器-Custom ViewPagerIndicator
 *
 * @author song
 * @date 15/8/20
 */
public class UDCustomViewPagerIndicator<U extends LVCustomViewPagerIndicator> extends UDView<U> {

    public UDCustomViewPagerIndicator(U view, Globals globals, LuaValue metaTable, Varargs initParams) {
        super(view, globals, metaTable, initParams);
    }


    /**
     * call init
     *
     * @param pos
     * @return
     */
    public LuaValue callCellInit(LuaValue cellData, int pos, int currentItem) {
        return callCellFunction("Init", cellData, pos, currentItem);
    }

    /**
     * call Layout
     *
     * @param pos
     * @return
     */
    public LuaValue callCellLayout(LuaValue cellData, int pos, int currentItem) {
        return callCellFunction("Layout", cellData, pos, currentItem);
    }

    /**
     * 调用 Page的某些方法
     *
     * @param method
     * @param position
     * @return
     */
    private LuaValue callCellFunction(String method, LuaValue cellData, int position, int currentItem) {
        return LuaUtil.callFunction(LuaUtil.getValue(initParams, method), cellData, LuaUtil.toLuaInt(position), LuaUtil.toLuaInt(currentItem));
    }

    /**
     * 设置当前页面
     *
     * @param item
     * @return
     */
    public UDCustomViewPagerIndicator setCurrentItem(final int item) {
        if (item != -1 && getView() != null) {
            getView().setCurrentItem(item);
        }
        return this;
    }

    /**
     * 当前页面
     *
     * @return
     */
    public int getCurrentItem() {
        return getView() != null ? getView().getCurrentItem() : 0;
    }
}
