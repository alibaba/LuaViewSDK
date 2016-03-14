package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDEditText;
import com.taobao.luaview.util.LuaViewUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * EditText 接口封装
 * @author song
 * @param <U>
 */
@LuaViewLib
public class UIEditTextMethodMapper<U extends UDEditText> extends UITextViewMethodMapper<U> {


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