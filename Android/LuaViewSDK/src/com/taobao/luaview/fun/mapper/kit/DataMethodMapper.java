package com.taobao.luaview.fun.mapper.kit;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.kit.UDData;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Data 接口封装，二进制数据处理
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib
public class DataMethodMapper<U extends UDData> extends BaseMethodMapper<U> {

    /**
     * 新增一部分数据，防止新建一个对象
     *
     * @param data
     * @param varargs
     * @return
     */
    public LuaValue append(U data, Varargs varargs) {
        return data.append(varargs.arg(2));
    }


    /**
     * 将二进制数据转成String，使用给定的编码，默认utf-8
     *
     * @param data
     * @param varargs
     * @return
     */
    public LuaValue toString(U data, Varargs varargs) {
        final String encode = varargs.optjstring(2, UDData.DEFAULT_ENCODE);
        return valueOf(data.toString(encode));
    }

    /**
     * 将二进制数据转成Json，使用给定的编码，默认utf-8
     *
     * @param data
     * @param varargs
     * @return
     */
    public LuaValue toJson(U data, Varargs varargs) {
        final String encode = varargs.optjstring(2, UDData.DEFAULT_ENCODE);
        final String json = data.toJson(encode);
        return json != null ? valueOf(json) : NIL;
    }

    /**
     * 将二进制数据转成table，使用给定的编码，默认utf-8
     *
     * @param data
     * @param varargs
     * @return
     */
    public LuaValue toTable(U data, Varargs varargs) {
        final String encode = varargs.optjstring(2, UDData.DEFAULT_ENCODE);
        return data.toTable(encode);
    }
}
