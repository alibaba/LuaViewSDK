/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.ToastUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class UDToast extends BaseUserdata {

    public UDToast(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
        init(varargs);
    }

    /**
     * 初始化数据
     */
    private void init(Varargs varargs) {
        final CharSequence text = LuaUtil.getText(varargs, 1);
        show(text);
    }

    /**
     * toast a message
     *
     * @param toastMessage
     * @return
     */
    public UDToast show(CharSequence toastMessage) {
        if (toastMessage != null) {
            ToastUtil.showToast(getContext(), toastMessage);
        }
        return this;
    }

}
