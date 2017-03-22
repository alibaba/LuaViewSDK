/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import com.taobao.luaview.userdata.base.BaseUserdata;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Unicode 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
public class UDUnicode extends BaseUserdata {

    private String mCharSequence;

    public UDUnicode(Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable);
        init(varargs);
    }

    private void init(Varargs varargs) {
        final StringBuffer sb = new StringBuffer();
        if (varargs != null) {
            for (int i = 0; i < varargs.narg(); i++) {
                sb.append(varargs.tochar(i + 1));
            }
        }
        mCharSequence = sb.toString();
    }

    public LuaValue getUnicode() {
        return valueOf(mCharSequence);
    }

}
