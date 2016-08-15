package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseListView;
import com.taobao.luaview.userdata.list.UDListView;

import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * ListView的方法映射
 *
 * @author song
 */
@LuaViewLib
public class UIListViewMethodMapper<U extends UDListView> extends UIBaseListViewMethodMapper<U> {
    private static final String TAG = UIListViewMethodMapper.class.getSimpleName();

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), new String[]{
        });
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            //TODO
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    @Override
    public UDBaseListView getUDBaseListView(Varargs varargs) {
        return getUD(varargs);
    }
}