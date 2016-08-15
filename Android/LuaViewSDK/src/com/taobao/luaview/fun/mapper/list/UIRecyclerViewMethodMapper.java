package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseListOrRecyclerView;
import com.taobao.luaview.userdata.list.UDBaseRecyclerView;
import com.taobao.luaview.userdata.list.UDRecyclerView;

import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * RecyclerView的方法映射
 *
 * @author song
 */
@LuaViewLib
public class UIRecyclerViewMethodMapper<U extends UDRecyclerView> extends UIBaseRecyclerViewMethodMapper<U> {

    private static final String TAG = UIRecyclerViewMethodMapper.class.getSimpleName();

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), new String[]{});
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
    public UDBaseRecyclerView getUDBaseRecyclerView(Varargs varargs) {
        return getUD(varargs);
    }

    @Override
    public UDBaseListOrRecyclerView getUDBaseListOrRecyclerView(Varargs varargs) {
        return getUD(varargs);
    }
}