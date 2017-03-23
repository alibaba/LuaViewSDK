/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.constants;

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
@LuaViewLib(revisions = {"20170306已对标", "EOFILL、FILLSTROKE、EOFILLSTROKE IOS有，Android无"})
public class UDPaintStyle extends BaseLuaTable {

    public UDPaintStyle(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        set("FILL", 0);//Paint.Style.FILL
        set("STROKE", 1);//Paint.Style.STROKE
        set("FILLSTROKE", 2);//Paint.Style.FILL_AND_STROKE
    }
}
