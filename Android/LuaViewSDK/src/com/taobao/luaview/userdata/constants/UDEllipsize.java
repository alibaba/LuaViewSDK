/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.constants;

import android.text.TextUtils;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Ellipse 文字处理
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UDEllipsize extends BaseLuaTable {

    public UDEllipsize(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        set("START", TextUtils.TruncateAt.START.name());
        set("MIDDLE", TextUtils.TruncateAt.MIDDLE.name());
        set("END", TextUtils.TruncateAt.END.name());
        set("MARQUEE", TextUtils.TruncateAt.MARQUEE.name());
    }

    /**
     * parse ellipsize
     *
     * @param ellipsizeName
     * @return
     */
    public static TextUtils.TruncateAt parse(String ellipsizeName) {
        return parse(ellipsizeName, TextUtils.TruncateAt.END);
    }

    public static TextUtils.TruncateAt parse(String ellipsizeName, TextUtils.TruncateAt defaultValue) {
        try {
            return TextUtils.TruncateAt.valueOf(ellipsizeName);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
