package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDToast;
import com.taobao.luaview.util.LuaViewUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * notice/toast 接口封装
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib
public class UIToastMethodMapper<U extends UDToast> extends BaseMethodMapper<U> {

    /**
     * show a toast
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue show(U view, Varargs varargs) {
        final CharSequence text = LuaViewUtil.getText(varargs.optvalue(2, NIL));
        return view.show(text);
    }
}
