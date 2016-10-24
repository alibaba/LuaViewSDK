package com.taobao.luaview.userdata.list;

import com.taobao.luaview.view.LVRecyclerView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * 容器类-RecyclerView
 *
 * @author song
 * @date 15/8/20
 */
public class UDRecyclerView<T extends LVRecyclerView> extends UDBaseRecyclerView<T> {

    public UDRecyclerView(T view, Globals globals, LuaValue metaTable, Varargs initParams) {
        super(view, globals, metaTable, initParams);
    }

    @Override
    public LVRecyclerView getLVRecyclerView() {
        return getView();
    }
}
