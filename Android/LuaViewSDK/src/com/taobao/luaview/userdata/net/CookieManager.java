/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.net;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;

import com.taobao.luaview.util.LogUtil;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * 处理Http Cookies
 *
 * @author song
 * @date 16/7/27
 * 主要功能描述
 * 修改描述
 * 下午5:20 song XXX
 */
public class CookieManager {
    private static final String TAG = "CookieManager";
    public static final String COOKIE = "Cookie";
    public static final String COOKIES_HEADER = "Set-Cookie";

    //cookies manager
    private static java.net.CookieManager mCookieManager;

    private static java.net.CookieManager getCookieManager() {
        if (mCookieManager == null) {
            mCookieManager = new java.net.CookieManager(null, CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(mCookieManager);
        }
        return mCookieManager;
    }

    /**
     * clear net cookies
     */
    public static void clearNetCookies() {
        java.net.CookieManager cookieManager = getCookieManager();
        CookieStore cookieStore = cookieManager.getCookieStore();
        if (cookieStore != null) {
            cookieStore.removeAll();
        }
    }

    /**
     * 处理请求的cookie
     *
     * @param connection
     */
    public static void handleRequestCookies(HttpURLConnection connection, String requestUrl) {
        if (connection != null) {
            final StringBuffer cookie = new StringBuffer();
            final String webkitCookie = getWebkitRequestCookies(requestUrl);
            if (webkitCookie != null) {
                cookie.append(webkitCookie);
            }
            if (cookie.length() > 0) {
                final String cookieStr = cookie.toString();
                connection.setRequestProperty(COOKIE, cookieStr);
            }
        }
    }

    /**
     * web kit cookies
     *
     * @param requestUrl
     */
    private static String getWebkitRequestCookies(String requestUrl) {
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        if (cookieManager != null) {
            syncWebkitCookies(cookieManager);
            final String webkitCookie = cookieManager.getCookie(requestUrl);
            LogUtil.d(TAG, "get-webkit", webkitCookie);
            return webkitCookie;
        }
        return null;
    }

    /**
     * synchronous cookies for webkit
     *
     * @param cookieManager
     */
    private static void syncWebkitCookies(android.webkit.CookieManager cookieManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//同步cookie synchronous
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
    }

    /**
     * net cookies
     */
    private static String getNetRequestCookies() {
        final CookieStore cookieStore = getCookieManager().getCookieStore();
        //net cookie
        if (cookieStore != null) {
            final List<HttpCookie> cookies = cookieStore.getCookies();
            if (cookies != null && cookies.size() > 0) {
                //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                final String netCookie = TextUtils.join(";", cookies);
                LogUtil.d(TAG, "get-net", netCookie);
                return netCookie;
            }
        }
        return null;
    }


    /**
     * cookies from response
     * TODO 将cookie信息同步到webview cookie里
     *
     * @param connection
     */
    public static void handleResponseCookies(HttpURLConnection connection, String url) {
        if (connection != null) {
            final Map<String, List<String>> headerFields = connection.getHeaderFields();
            if (headerFields != null && headerFields.containsKey(COOKIES_HEADER)) {
                final List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                //write webkit cookie
//                updateWebkitResponseCookies(cookiesHeader, url);
            }
        }
    }

    /**
     * @param cookiesHeader
     */
    private static String updateWebkitResponseCookies(List<String> cookiesHeader, String requestUrl) {
        //write webkit cookie
        final CookieStore cookieStore = getCookieManager().getCookieStore();
        if (cookieStore != null && cookiesHeader != null) {

            StringBuffer cookie = new StringBuffer();
            for (String cookieHeader : cookiesHeader) {
                cookie.append(cookieHeader).append(";");
            }
            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            if (cookieManager != null && cookie.length() > 0) {
                String cookieStr = cookie.toString();
                LogUtil.d(TAG, "write-webkit", cookieStr);
                cookieManager.setCookie(requestUrl, cookieStr);
            }
        }
        return null;
    }

    /**
     * write net cookies
     *
     * @param cookiesHeader
     */
    private static void updateNetResponseCookies(List<String> cookiesHeader) {
        //write net cookie
        final CookieStore cookieStore = getCookieManager().getCookieStore();
        if (cookieStore != null && cookiesHeader != null) {
            HttpCookie cookieStr = null;
            for (String cookieHeader : cookiesHeader) {
                if (cookieHeader != null) {
                    List<HttpCookie> cookies = HttpCookie.parse(cookieHeader);
                    if (cookies != null && cookies.size() > 0) {
                        cookieStr = cookies.get(0);
                        LogUtil.d(TAG, "write-net", cookieStr);
                        cookieStore.add(null, cookieStr);
                    }
                }
            }
        }
    }

}
