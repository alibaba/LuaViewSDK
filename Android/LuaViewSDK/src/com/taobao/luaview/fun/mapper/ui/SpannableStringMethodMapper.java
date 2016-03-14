package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDSpannableString;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * SpannableString 接口封装
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib
public class SpannableStringMethodMapper<U extends UDSpannableString> extends BaseMethodMapper<U> {


    /**
     * 新增一段内容
     *
     * @param ssb
     * @param varargs
     * @return
     */
    public LuaValue append(U ssb, Varargs varargs) {
        LuaValue ssb2 = varargs.optvalue(2, null);
        return ssb.append(ssb2);
    }

}
