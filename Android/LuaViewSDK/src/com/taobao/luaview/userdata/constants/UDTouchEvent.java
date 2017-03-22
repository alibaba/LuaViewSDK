/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.constants;

import android.view.MotionEvent;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Canvas.Paint 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标", "ios有更多的属性"})
public class UDTouchEvent extends BaseLuaTable {

    public UDTouchEvent(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        set("DOWN", MotionEvent.ACTION_DOWN);//0
        set("UP", MotionEvent.ACTION_UP);//1
        set("MOVE", MotionEvent.ACTION_MOVE);//2
        set("CANCEL", MotionEvent.ACTION_CANCEL);//3
        set("OUTSIDE", MotionEvent.ACTION_OUTSIDE);//4
    }

}
