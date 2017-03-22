/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.net;

import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.userdata.kit.UDData;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.List;
import java.util.Map;

/**
 * Http 返回数据
 * @author song 
 */
public class UDHttpResponse extends BaseLuaTable {
    private byte[] mData;
    private int mStatusCode = -1;
    private String mResponseMsg;
    private Map<String, List<String>> mHeaders;

    public UDHttpResponse(Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable, varargs);
        init();
    }

    private void init() {
        set("data", new data());
        set("code", new code());
        set("header", new header());
        set("message", new message());//TODO iOS无
    }

    public UDHttpResponse setData(byte[] mData) {
        this.mData = mData;
        return this;
    }

    public LuaValue getData() {
        return mData != null ? new UDData(getGlobals(), getmetatable(), null).append(mData) : LuaValue.NIL;
    }

    public UDHttpResponse setResponseMsg(String message) {
        this.mResponseMsg = message;
        return this;
    }

    public String getResponseMsg() {
        return mResponseMsg;
    }

    public UDHttpResponse setStatusCode(int statusCode) {
        this.mStatusCode = statusCode;
        return this;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public UDHttpResponse setHeaders(Map<String, List<String>> headers) {
        this.mHeaders = headers;
        return this;
    }

    public LuaValue getHeaders(String name) {
        if (mHeaders != null) {
            if (name != null) {//获取某一个header
                return LuaUtil.toTable(mHeaders.get(name));
            } else {//获取所有的header
                return LuaUtil.toTable(mHeaders);
            }
        }
        return NIL;
    }

    /**
     * convert response to LuaTable
     *
     * @return
     */
    public LuaTable toTable() {
        LuaTable result = new LuaTable();
        result.set("data", new UDData(getGlobals(), getmetatable(), null).append(mData));
        result.set("code", LuaValue.valueOf(mStatusCode));
        result.set("header", LuaUtil.toTable(mHeaders));
        result.set("message", LuaValue.valueOf(mResponseMsg));
        return result;
    }

    //-----------------------------------------functions--------------------------------------------

    /**
     * 获取数据
     */
    class data extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return getData();
        }
    }

    /**
     * 错误码
     */
    class code extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return valueOf(mStatusCode);
        }
    }

    /**
     * 请求头
     */
    class header extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return getHeaders(args.narg() > 1 ? args.optjstring(2, null) : null);
        }
    }

    /**
     * 错误信息
     */
    class message extends VarArgFunction {
        public Varargs invoke(Varargs args) {
            return valueOf(mResponseMsg);
        }
    }
}