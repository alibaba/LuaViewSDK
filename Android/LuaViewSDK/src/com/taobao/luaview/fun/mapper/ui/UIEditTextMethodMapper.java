/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDEditText;
import com.taobao.luaview.util.LuaViewUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;


/**
 * EditText 接口封装
 * @author song
 * @param <U>
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UIEditTextMethodMapper<U extends UDEditText> extends UITextViewMethodMapper<U> {

    private static final String TAG = "UIEditTextMethodMapper";
    private static final String[] sMethods = new String[]{
            "hint",//0
            "placeholder"//1
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return hint(target, varargs);
            case 1:
                return placeholder(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    /**
     * 获取placeHolder内容
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue hint(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setHint(view, varargs);
        } else {
            return getHint(view, varargs);
        }
    }

    public LuaValue setHint(U view, Varargs varargs) {
        final CharSequence text = LuaViewUtil.getText(varargs.optvalue(2, NIL));
        return view.setHint(text);
    }

    public LuaValue getHint(U view, Varargs varargs) {
        return valueOf(view.getHint());
    }

    /**
     * 获取placeHolder内容
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue placeholder(U view, Varargs varargs) {
        return hint(view, varargs);
    }

    public LuaValue setPlaceholder(U view, Varargs varargs) {
        return setHint(view, varargs);
    }

    public LuaValue getPlaceholder(U view, Varargs varargs) {
        return getHint(view, varargs);
    }
}