package com.taobao.luaview.userdata.kit;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.JsonUtil;

import org.apache.http.util.ByteArrayBuffer;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

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
        super(new ByteArrayBuffer(DEFAULT_BUFFER_SIZE), globals, metatable, varargs);
        init();
    }

    private void init() {
        ByteArrayBuffer byteArrayBuffer = (ByteArrayBuffer) userdata();
        if (mVarargs != null) {
            try {
                for (int i = 0; i < mVarargs.narg(); i++) {
                    Object obj = mVarargs.arg(i + 1);
                    String str = String.valueOf(obj);
                    byte[] data = str.getBytes(DEFAULT_ENCODE);
                    byteArrayBuffer.append(data, 0, data.length);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
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
        ByteArrayBuffer result = (ByteArrayBuffer) this.userdata();
        if (appendData instanceof UDData) {
            ByteArrayBuffer buffer2 = (ByteArrayBuffer) ((UDData) appendData).userdata();
            result.append(buffer2.toByteArray(), 0, buffer2.length());
        } else if (appendData instanceof byte[]) {
            byte[] buffer2 = (byte[]) appendData;
            result.append(buffer2, 0, buffer2.length);
        }
        return this;
    }

    @Override
    public String tojstring() {
        return toString(DEFAULT_ENCODE);
    }

    /**
     * 转成给定的编码字符
     *
     * @param encode
     * @return
     */
    public String toString(String encode) {
        ByteArrayBuffer buffer = (ByteArrayBuffer) userdata();
        try {
            return (buffer != null && buffer.length() > 0) ? new String(buffer.toByteArray(), encode != null ? encode : DEFAULT_ENCODE) : "";
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
