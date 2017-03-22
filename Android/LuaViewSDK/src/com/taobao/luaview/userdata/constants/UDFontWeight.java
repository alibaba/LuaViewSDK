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
 * FontWeight 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标", "ios不支持数值"})
public class UDFontWeight extends BaseLuaTable {

    public static final String WEIGHT_NORMAL = "normal";
    public static final String WEIGHT_BOLD = "bold";

    public static final int WEIGHT_NORMAL_INT = 400;
    public static final int WEIGHT_BOLD_INT = 700;

    public UDFontWeight(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        this.set("NORMAL", WEIGHT_NORMAL_INT);
        this.set("BOLD", WEIGHT_BOLD_INT);
    }

    public static int getValue(final String name) {
        if (!TextUtils.isEmpty(name)) {
            if (WEIGHT_NORMAL.equalsIgnoreCase(name)) {
                return WEIGHT_NORMAL_INT;
            } else if (WEIGHT_BOLD.equalsIgnoreCase(name)) {
                return WEIGHT_BOLD_INT;
            }
        }
        return WEIGHT_NORMAL_INT;
    }

}
