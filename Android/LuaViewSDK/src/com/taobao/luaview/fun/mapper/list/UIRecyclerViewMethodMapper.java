package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseListOrRecyclerView;
import com.taobao.luaview.userdata.list.UDBaseRecyclerView;
import com.taobao.luaview.userdata.list.UDRecyclerView;

import org.luaj.vm2.Varargs;

/**
 * RecyclerView的方法映射
 * @author song
 */
@LuaViewLib
public class UIRecyclerViewMethodMapper<U extends UDRecyclerView> extends UIBaseRecyclerViewMethodMapper<U> {


    @Override
    public UDBaseRecyclerView getUDBaseRecyclerView(Varargs varargs) {
        return getUD(varargs);
    }

    @Override
    public UDBaseListOrRecyclerView getUDBaseListOrRecyclerView(Varargs varargs) {
        return getUD(varargs);
    }
}