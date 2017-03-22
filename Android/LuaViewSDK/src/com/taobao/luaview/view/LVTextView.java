/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.view.Gravity;

import com.taobao.luaview.userdata.constants.UDFontSize;
import com.taobao.luaview.userdata.ui.UDTextView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.foreground.ForegroundTextView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * LuaView-TextView
 *
 * @author song
 * @date 15/8/20
 */
public class LVTextView extends ForegroundTextView implements ILVView {
    private UDView mLuaUserdata;

    public LVTextView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDTextView(this, globals, metaTable, varargs);
        this.setIncludeFontPadding(false);//设置默认TextView不包含字体的padding，否则adjustSize的时候拿到的高度有问题
        this.setGravity(Gravity.CENTER_VERTICAL);//默认竖直居中
        this.setLines(1);//默认一行
        this.setTextSize(UDFontSize.FONTSIZE_SMALL);
//        this.setEllipsize(TextUtils.TruncateAt.END);//默认在最后有3个点
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
