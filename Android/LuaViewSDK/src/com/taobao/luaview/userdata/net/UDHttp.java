/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.net;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.IOUtil;
import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Http 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
public class UDHttp extends BaseUserdata {
    private LuaFunction mCallback;

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final int DEFAULT_RETRY_TIMES = 3;
    public static final int DEFAULT_TIMEOUT = 30;

    private String mMethod = METHOD_POST;
    private String mUrl;
    private LuaTable mParams;
    private int mRetryTimes = DEFAULT_RETRY_TIMES;
    private int mTimeout = DEFAULT_TIMEOUT;

    //令牌
    private Future<?> mFuture;

    private static ExecutorService mExecutor;

    private static synchronized ExecutorService getExecutor() {
        if (mExecutor == null) {
            mExecutor = Executors.newFixedThreadPool(5);
        }
        return mExecutor;
    }


    public UDHttp(Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable, varargs);
        init();
    }

    /**
     * 初始化mtop调用
     */
    private void init() {
        initVarargs();
    }

    /**
     * 初始化数据
     */
    private void initVarargs() {
        final LuaValue param1 = getInitParam1();
        final LuaFunction callback = LuaUtil.getFunction(initParams, 2);
        final String method = LuaUtil.getString(param1, "method");
        final LuaTable params = LuaUtil.getTable(param1, "params");
        setMethod(method);
        setParams(params);
        setCallback(callback);
        disableConnectionReuseIfNecessary();
    }

    /**
     * 请求 url
     *
     * @param url
     * @return
     */
    public UDHttp setUrl(String url) {
        this.mUrl = url;
        return this;
    }

    public String getUrl() {
        return this.mUrl;
    }

    /**
     * 请求方法 post/get/put/delete等
     *
     * @param mMethod
     * @return
     */
    public UDHttp setMethod(String mMethod) {
        this.mMethod = mMethod;
        return this;
    }

    public String getMethod() {
        return this.mMethod;
    }

    /**
     * 请求参数，使用table构建
     *
     * @param mParams
     * @return
     */
    public UDHttp setParams(LuaTable mParams) {
        this.mParams = mParams;
        return this;
    }

    public LuaValue getParams() {
        return this.mParams != null ? this.mParams : NIL;
    }

    /**
     * 重试次数
     *
     * @param retryTimes
     * @return
     */
    public UDHttp setRetryTimes(int retryTimes) {
        this.mRetryTimes = retryTimes;
        return this;
    }

    public int getRetryTimes() {
        return this.mRetryTimes;
    }

    /**
     * 请求超时时长
     *
     * @param timeOut
     * @return
     */
    public UDHttp setTimeout(int timeOut) {
        this.mTimeout = timeOut;
        return this;
    }

    public int getTimeout() {
        return this.mTimeout;
    }

    /**
     * 清空net cookies
     */
    public UDHttp clearNetCookies() {
        CookieManager.clearNetCookies();
        return this;
    }

    /**
     * 回调
     *
     * @param luaFunction
     */
    public UDHttp setCallback(final LuaFunction luaFunction) {
        this.mCallback = luaFunction;
        return this;
    }

    public LuaFunction getCallback() {
        return this.mCallback;
    }

    /**
     * 发送get请求
     *
     * @return
     */
    public UDHttp get(String url, LuaTable params, LuaFunction callback) {
        setMethod(METHOD_GET);
        if (url != null) {
            setUrl(url);
        }
        if (params != null) {
            setParams(params);
        }
        if (callback != null) {
            setCallback(callback);
        }
        return request();
    }

    public UDHttp post(String url, LuaTable params, LuaFunction callback) {
        setMethod(METHOD_POST);
        if (url != null) {
            setUrl(url);
        }
        if (params != null) {
            setParams(params);
        }
        if (callback != null) {
            setCallback(callback);
        }
        return request();
    }

    /**
     * 请求mtop
     */
    public synchronized UDHttp request() {
        if (mFuture == null) {
            mFuture = getExecutor().submit(buildRunnable());
        }
        return this;
    }

    public synchronized UDHttp cancel() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
        return this;
    }

    /**
     * 调用回调，注意：需要在主线程执行该操作
     *
     * @param result
     */
    private void callCallback(final LuaValue result) {
        LuaViewUtil.runOnUiThread(getContext(), new Runnable() {
            @Override
            public void run() {
                LuaUtil.callFunction(mCallback, result);
            }
        });
    }

    /**
     * build a runnable to execute http request
     * TODO retry times重试次数设置。
     *
     * @return
     */
    private Runnable buildRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;

                final UDHttpResponse udHttpResponse = new UDHttpResponse(getGlobals(), getmetatable(), null);
                try {
                    final URL url = new URL(mUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setInstanceFollowRedirects(true);//支持301重定向
                    HttpURLConnection.setFollowRedirects(true);//支持重定向

                    //method
                    connection.setRequestMethod(mMethod);
                    //timeout
                    connection.setConnectTimeout(mTimeout * 1000);

                    CookieSyncManager.createInstance(getContext());
                    //请求的cookie
                    CookieManager.handleRequestCookies(connection, mUrl);

                    if (mParams != null) {
                        for (LuaValue key : mParams.keys()) {
                            final LuaValue value = mParams.get(key);
                            final String requestKey = key != null ? key.optjstring(null) : null;
                            final String requestValue = value != null ? value.optjstring(null) : null;
                            if (!TextUtils.isEmpty(requestKey) && !TextUtils.isEmpty(requestValue)) {
                                connection.setRequestProperty(requestKey, requestValue);
                            }
                        }
                    }

                    //连接服务器
                    connection.connect();

                    //301重定向&重定向cookie
                    connection = handle301Redirect(connection);

                    //code
                    udHttpResponse.setStatusCode(connection.getResponseCode());

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        LogUtil.e("[Server Returned HTTP] ", connection.getResponseCode(), connection.getResponseMessage());

                        //msg
                        udHttpResponse.setResponseMsg(connection.getResponseMessage());
                        return;
                    }

                    input = connection.getInputStream();

                    final byte[] fileData = IOUtil.toBytes(input, getContentCharset(connection));

                    //data
                    udHttpResponse.setData(fileData);

                    //header
                    udHttpResponse.setHeaders(connection.getHeaderFields());

                    //response cookie
//                    CookieManager.handleResponseCookies(connection, mUrl);
                } catch (Exception e) {
                    LogUtil.e("[Http error] ", e);
                    e.printStackTrace();
                    udHttpResponse.setResponseMsg(e.toString());
                } finally {
                    try {
                        if (output != null) {
                            output.flush();
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException ignored) {
                        ignored.printStackTrace();
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }

                    //调用回调
                    callCallback(udHttpResponse);
                }
            }
        };
    }

    @NonNull
    private HttpURLConnection handle301Redirect(HttpURLConnection connection) throws IOException {
        if (connection != null) {
            boolean redirect = false;

            // normally, 3xx is redirect
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            if (redirect) {
                // get redirect url from "location" header field
                String newUrl = connection.getHeaderField("Location");

                // get the cookie if need, for login
                String cookies = connection.getHeaderField(com.taobao.luaview.userdata.net.CookieManager.COOKIES_HEADER);

                // open the new connnection again
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
                connection.setRequestProperty(com.taobao.luaview.userdata.net.CookieManager.COOKIE, cookies);

                //连接服务器
                connection.connect();

                //重复301重定向
                connection = handle301Redirect(connection);
            }
        }
        return connection;
    }

    private String getContentCharset(HttpURLConnection connection) {
        if (connection != null) {
            final String contentType = connection.getContentType();
            if (contentType != null) {
                final String[] values = contentType.split(";"); // values.length should be 2
                String charset = null;
                if (values != null) {
                    for (String value : values) {
                        value = value.trim();
                        if (value != null && value.toLowerCase().startsWith("charset=")) {
                            charset = value.substring("charset=".length());
                        }
                    }
                }
                return charset;
            }
        }
        return null;
    }

    private void disableConnectionReuseIfNecessary() {
        // Work around pre-Froyo bugs in HTTP connection reuse.
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}
