/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.JsonUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Data 用户数据封装，二进制数据
 *
 * @author song
 * @date 15/9/6
 */
public class UDData extends BaseUserdata {
    private static final int DEFAULT_BUFFER_SIZE = 128;
    public static final String DEFAULT_ENCODE = "utf-8";

    public UDData(Globals globals, LuaValue metatable, Varargs varargs) {
        super(new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE), globals, metatable, varargs);
        init();
    }

    private void init() {
        ByteArrayOutputStream byteArrayBuffer = (ByteArrayOutputStream) userdata();
        if (initParams != null) {
            try {
                for (int i = 0; i < initParams.narg(); i++) {
                    Object obj = initParams.arg(i + 1);
                    String str = String.valueOf(obj);
                    byte[] data = str.getBytes(DEFAULT_ENCODE);
                    byteArrayBuffer.write(data, 0, data.length);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get bytes of this data
     * @return
     */
    public byte[] bytes() {
        if (userdata() instanceof ByteArrayOutputStream) {
            return ((ByteArrayOutputStream) userdata()).toByteArray();
        }
        return null;
    }

    @Override
    public LuaValue add(LuaValue data2) {
        return new UDData(getGlobals(), getmetatable(), null).append(this).append(data2);
    }

    /**
     * 新增一部分数据
     *
     * @param appendData
     * @return
     */
    public UDData append(Object appendData) {
        ByteArrayOutputStream result = (ByteArrayOutputStream) this.userdata();
        if (appendData instanceof UDData) {
            ByteArrayOutputStream buffer2 = (ByteArrayOutputStream) ((UDData) appendData).userdata();
            result.write(buffer2.toByteArray(), 0, buffer2.size());
        } else if (appendData instanceof byte[]) {
            byte[] buffer2 = (byte[]) appendData;
            result.write(buffer2, 0, buffer2.length);
        }
        return this;
    }

    @Override
    public String tojstring() {
        return toString(DEFAULT_ENCODE);
    }


    @Override
    public String toString() {
        return tojstring();
    }

    /**
     * 转成给定的编码字符
     *
     * @param encode
     * @return
     */
    public String toString(String encode) {
        ByteArrayOutputStream buffer = (ByteArrayOutputStream) userdata();
        try {
            return (buffer != null && buffer.size() > 0) ? new String(buffer.toByteArray(), encode != null ? encode : DEFAULT_ENCODE) : "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将string转成方便阅读的Json数据
     *
     * @param encode
     * @return
     */
    public String toJson(String encode) {
        final String jsonString = toString(encode);
        return JsonUtil.isJson(jsonString) ? jsonString : null;
    }

    /**
     * 转成LuaTable
     *
     * @param encode
     * @return
     */
    public LuaValue toTable(String encode) {
        final String jsonString = toString(encode);
        return JsonUtil.toLuaTable(jsonString);
    }
}
