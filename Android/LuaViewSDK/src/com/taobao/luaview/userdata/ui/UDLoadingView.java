package com.taobao.luaview.userdata.ui;


import com.taobao.luaview.view.LVLoadingView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Loading View 数据封装
 *
 * @author song
 */
public class UDLoadingView extends UDViewGroup<LVLoadingView> {

    public UDLoadingView(LVLoadingView view, Globals globals, LuaValue metaTable, Varargs varargs) {
        super(view, globals, metaTable, varargs);
    }

    public UDLoadingView setColor(Integer color) {
        if (color != null) {
            final LVLoadingView view = getView();
            if (view != null) {
                view.setColor(color);
            }
        }
        return this;
    }

    public int getColor() {
        return getView() != null ? getView().getSolidColor() : 0;
    }
}
