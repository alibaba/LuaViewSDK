package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseListView;
import com.taobao.luaview.userdata.list.UDListView;

import org.luaj.vm2.Varargs;

/**
 * ListView的方法映射
 * @author song
 */
@LuaViewLib
public class UIListViewMethodMapper<U extends UDListView> extends UIBaseListViewMethodMapper<U> {


    @Override
    public UDBaseListView getUDBaseListView(Varargs varargs) {
        return getUD(varargs);
    }
}