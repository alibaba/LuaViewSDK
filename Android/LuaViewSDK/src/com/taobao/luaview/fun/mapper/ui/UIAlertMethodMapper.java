package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDAlert;

import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * alert 接口封装
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib(revisions = {"20170306已对标", "跟iOS不统一待统一"})
public class UIAlertMethodMapper<U extends UDAlert> extends BaseMethodMapper<U> {
    private static final String TAG = "UIAlertMethodMapper";

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
}
