package com.taobao.luaview.userdata.ui;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.util.ToastUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class UDToast extends BaseUserdata {

    public UDToast(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
        init();
    }

    /**
     * 初始化数据
     */
    private void init() {
        final CharSequence text = LuaViewUtil.getText(this.mVarargs.optvalue(1, NIL));
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
