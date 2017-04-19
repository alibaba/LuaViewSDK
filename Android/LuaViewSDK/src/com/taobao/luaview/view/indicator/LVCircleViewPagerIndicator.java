/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.indicator;

import com.taobao.luaview.userdata.indicator.UDCircleViewPagerIndicator;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.indicator.circle.CirclePageIndicator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * LuaView-CircleViewPagerIndicator
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class LVCircleViewPagerIndicator extends CirclePageIndicator implements ILVView {
    private UDCircleViewPagerIndicator mLuaUserdata;

    public LVCircleViewPagerIndicator(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDCircleViewPagerIndicator(this, globals, metaTable, varargs);
        this.setPadding(0, 2, 0, 0);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
