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
 * 字体大小
 *
 * @author song
 * @date 16/3/16
 */
@LuaViewLib(revisions = {"20170306已对标"})
@Deprecated
public class UDFontSize extends BaseLuaTable {
    public static final int FONTSIZE_MICRO = 12;
    public static final int FONTSIZE_SMALL = 14;
    public static final int FONTSIZE_MEDIUM = 18;
    public static final int FONTSIZE_LARGE = 22;

    public UDFontSize(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        this.set("MICRO", FONTSIZE_MICRO);
        this.set("SMALL", FONTSIZE_SMALL);
        this.set("MEDIUM", FONTSIZE_MEDIUM);
        this.set("LARGE", FONTSIZE_LARGE);
    }

}
