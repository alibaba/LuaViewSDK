package com.taobao.luaview.fun.mapper.net;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.net.UDHttp;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * http 接口封装
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib
public class HttpMethodMapper<U extends UDHttp> extends BaseMethodMapper<U> {


    /**
     * 设置请求url
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue url(U http, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setUrl(http, varargs);
        } else {
            return getUrl(http, varargs);
        }
    }

    public LuaValue setUrl(U http, Varargs varargs) {
        final String url = varargs.optjstring(2, null);
        return http.setUrl(url);
    }

    public LuaValue getUrl(U http, Varargs varargs) {
        return valueOf(http.getUrl());
    }

    /**
     * 请求方法
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue method(U http, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setMethod(http, varargs);
        } else {
            return getMethod(http, varargs);
        }
    }

    public LuaValue setMethod(U http, Varargs varargs) {
        final String method = varargs.optjstring(2, UDHttp.METHOD_POST);
        return http.setMethod(method);
    }

    public LuaValue getMethod(U http, Varargs varargs) {
        return valueOf(http.getMethod());
    }

    /**
     * 重试次数
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue retryTimes(U http, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setRetryTimes(http, varargs);
        } else {
            return getRetryTimes(http, varargs);
        }
    }

    public LuaValue setRetryTimes(U http, Varargs varargs) {
        final int retryTimes = varargs.optint(2, UDHttp.DEFAULT_RETRY_TIMES);
        return http.setRetryTimes(retryTimes);
    }

    public LuaValue getRetryTimes(U http, Varargs varargs) {
        return valueOf(http.getRetryTimes());
    }

    /**
     * 延时
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue timeout(U http, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setTimeout(http, varargs);
        } else {
            return getTimeout(http, varargs);
        }
    }

    public LuaValue setTimeout(U http, Varargs varargs) {
        final int timeout = varargs.optint(2, UDHttp.DEFAULT_TIMEOUT);
        return http.setTimeout(timeout);
    }

    public LuaValue getTimeout(U http, Varargs varargs) {
        return valueOf(http.getTimeout());
    }

    /**
     * 参数
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue params(U http, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setParams(http, varargs);
        } else {
            return getParams(http, varargs);
        }
    }

    public LuaValue setParams(U http, Varargs varargs) {
        final LuaTable params = http.opttable(2, null);
        return http.setParams(params);
    }

    public LuaValue getParams(U http, Varargs varargs) {
        return http.getParams();
    }

    /**
     * 回调
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue callback(U http, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCallback(http, varargs);
        } else {
            return getCallback(http, varargs);
        }
    }

    public LuaValue setCallback(U http, Varargs varargs) {
        final LuaFunction callback = varargs.optfunction(2, null);
        return http.setCallback(callback);
    }

    public LuaValue getCallback(U http, Varargs varargs) {
        return http.getCallback();
    }

    /**
     * 请求
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue request(U http, Varargs varargs) {
        return http.request();
    }

    /**
     * 取消
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue cancel(U http, Varargs varargs) {
        return http.cancel();
    }

    /**
     * get请求
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue get(U http, Varargs varargs) {
        final String url = LuaUtil.getString(varargs, 2);
        final LuaTable params = LuaUtil.getTable(varargs, 3, 2);
        final LuaFunction callback = LuaUtil.getFunction(varargs, 4, 3, 2);
        return http.get(url, params, callback);
    }

    /**
     * pos 请求
     *
     * @param http
     * @param varargs
     * @return
     */
    public LuaValue post(U http, Varargs varargs) {
        final String url = LuaUtil.getString(varargs, 2);
        final LuaTable params = LuaUtil.getTable(varargs, 3, 2);
        final LuaFunction callback = LuaUtil.getFunction(varargs, 4, 3, 2);
        return http.post(url, params, callback);
    }
}
