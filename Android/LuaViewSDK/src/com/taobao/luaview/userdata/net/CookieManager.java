package com.taobao.luaview.userdata.net;

import android.text.TextUtils;

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
                connection.setRequestProperty(COOKIE, cookie.toString());
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
            return cookieManager.getCookie(requestUrl);
        }
        return null;
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
                return TextUtils.join(";", cookies);
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
                cookieManager.setCookie(requestUrl, cookie.toString());
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
            for (String cookieHeader : cookiesHeader) {
                if (cookieHeader != null) {
                    List<HttpCookie> cookies = HttpCookie.parse(cookieHeader);
                    if (cookies != null && cookies.size() > 0) {
                        cookieStore.add(null, cookies.get(0));
                    }
                }
            }
        }
    }

}
