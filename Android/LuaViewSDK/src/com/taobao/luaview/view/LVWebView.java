/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDWebView;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVNativeViewProvider;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import static org.luaj.vm2.LuaValue.valueOf;

/**
 * Created by tuoli on 10/9/16.
 */
public class LVWebView extends SwipeRefreshLayout implements ILVNativeViewProvider, ILVView {

    protected UDView mLuaUserdata;
    protected WebView mWebView;
    protected boolean mIsLoading;

    public LVWebView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDWebView(this, globals, metaTable, varargs);
        init(globals.getContext());
    }

    private void init(Context context) {
        this.mWebView = new WebView(context);
        this.addView(this.mWebView, LuaViewUtil.createRelativeLayoutParamsMM());
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.getSettings().setSavePassword(false);
        this.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        this.setEnabled(false);         // 默认关闭下拉刷新功能
        setupDefaultWebViewClient();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setupDefaultWebViewClient() {
        if (mWebView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;        // 使其不要在外部浏览器中响应
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        mIsLoading = true;
                        if (LuaUtil.isTable(mLuaUserdata.getCallback())) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mLuaUserdata.getCallback(), "onPageStarted"));
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        mIsLoading = false;
                        if (LuaUtil.isTable(mLuaUserdata.getCallback())) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mLuaUserdata.getCallback(), "onPageFinished"));
                        }

                        if (LVWebView.this.isRefreshing()) {
                            LVWebView.this.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        mIsLoading = false;
                        if (LuaUtil.isTable(mLuaUserdata.getCallback())) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mLuaUserdata.getCallback(), "onReceivedError"),
                                    valueOf(error.getErrorCode()),
                                    valueOf(String.valueOf(error.getDescription())),
                                    valueOf(String.valueOf(request.getUrl())));
                        }

                        if (LVWebView.this.isRefreshing()) {
                            LVWebView.this.setRefreshing(false);
                        }
                    }
                });
            } else {
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;        // 使其不要在外部浏览器中响应
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        mIsLoading = true;
                        if (LuaUtil.isTable(mLuaUserdata.getCallback())) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mLuaUserdata.getCallback(), "onPageStarted"));
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        mIsLoading = false;
                        if (LuaUtil.isTable(mLuaUserdata.getCallback())) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mLuaUserdata.getCallback(), "onPageFinished"));
                        }

                        if (LVWebView.this.isRefreshing()) {
                            LVWebView.this.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        mIsLoading = false;
                        if (LuaUtil.isTable(mLuaUserdata.getCallback())) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mLuaUserdata.getCallback(), "onReceivedError"), errorCode, description, failingUrl);
                        }

                        if (LVWebView.this.isRefreshing()) {
                            LVWebView.this.setRefreshing(false);
                        }
                    }
                });
            }
        }
    }

    public WebView getWebView() {
        return mWebView;
    }

    public boolean getLoadingState() {
        return mIsLoading;
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public View getNativeView() {
        return this.getWebView();
    }
}
