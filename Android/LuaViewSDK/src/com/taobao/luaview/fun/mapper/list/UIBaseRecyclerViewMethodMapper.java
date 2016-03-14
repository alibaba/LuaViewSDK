package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseListOrRecyclerView;
import com.taobao.luaview.userdata.list.UDBaseRecyclerView;

import org.luaj.vm2.Varargs;

/**
 * RecyclerView的方法映射
 * @author song
 */
@LuaViewLib
public abstract class UIBaseRecyclerViewMethodMapper<U extends UDBaseRecyclerView> extends UIBaseListOrRecyclerViewMethodMapper<U> {

    public abstract UDBaseRecyclerView getUDBaseRecyclerView(Varargs varargs);


    @Override
    public UDBaseListOrRecyclerView getUDBaseListOrRecyclerView(Varargs varargs) {
        return getUD(varargs);
    }
}