/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.text.InputType;
import android.widget.EditText;

import com.taobao.luaview.userdata.constants.UDFontSize;
import com.taobao.luaview.userdata.ui.UDEditText;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * LuaView-EditText
 *
 * @author song
 * @date 15/8/20
 */
public class LVEditText extends EditText implements ILVView {
    private UDView mLuaUserdata;

    public LVEditText(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDEditText(this, globals, metaTable, varargs);
        this.setTextSize(UDFontSize.FONTSIZE_SMALL);
        this.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
