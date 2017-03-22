/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.text.TextUtils;
import android.webkit.WebView;
import com.taobao.luaview.view.LVWebView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Created by tuoli on 10/9/16.
 */

public class UDWebView extends UDView<LVWebView> {

    public UDWebView(LVWebView view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    /**
     * Loads the given URL.
     * @param url
     * @return
     */
    public UDWebView loadUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            WebView view = null;
            if (this.getView() != null && (view = this.getView().getWebView()) != null) {
                view.loadUrl(url);
            }
        }

        return this;
    }

    public boolean canGoBack() {
        WebView view = null;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            return view.canGoBack();
        }

        return false;
    }

    public boolean canGoForward() {
        WebView view = null;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            return view.canGoBack();
        }

        return false;
    }

    public UDWebView goBack() {
        WebView view = null;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.goBack();
        }

        return this;
    }

    public UDWebView goForward() {
        WebView view = null;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.goForward();
        }

        return this;
    }

    public UDWebView reload() {
        WebView view = null;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.reload();
        }

        return this;
    }

    public UDWebView stopLoading() {
        WebView view = null;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.stopLoading();
        }

        return this;
    }

    public boolean isLoading() {
        return this.getView() != null ? this.getView().getLoadingState() : false;
    }

    /**
     * Get the tile of web page
     * @return
     */
    public String title() {
        WebView view = null;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            return view.getTitle();
        }

        return "";
    }

    /**
     * Get the loaded URL
     * @return
     */
    public String url() {
        WebView view = null;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            return view.getUrl();
        }

        return "";
    }

    @Override
    public UDView setCallback(LuaValue callbacks) {
        this.mCallback = callbacks;
        return this;
    }

    /**
     * 设置WebView的enabled state
     * @param enable
     * @return
     */
    @Override
    public UDView setEnabled(boolean enable) {
        WebView view = null;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.setEnabled(enable);
        }
        return this;
    }

    /**
     * 设置SwipeRefreshLayout是否有效,无效则不可下拉
     * @param enable
     * @return
     */
    public UDWebView pullRefreshEnable(boolean enable) {
        final LVWebView view = this.getView();
        if (view != null) {
            view.setEnabled(enable);
        }

        return this;
    }

    public boolean isPullRefreshEnable() {
        return this.getView() != null ? this.getView().isEnabled() : false;
    }
}
