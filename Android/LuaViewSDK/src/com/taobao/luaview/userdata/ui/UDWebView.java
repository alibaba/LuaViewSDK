package com.taobao.luaview.userdata.ui;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVWebView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Created by tuoli on 10/9/16.
 */

public class UDWebView extends UDView<LVWebView> {

    private boolean mIsLoading;

    public UDWebView(LVWebView view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
        init();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void init() {
        final LVWebView view = this.getView();
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;        // 使其不要在外部浏览器中响应
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        mIsLoading = true;
                        if (mCallback != null && mCallback.istable()) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "onPageStarted"));
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        mIsLoading = false;
                        if (mCallback != null && mCallback.istable()) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "onPageFinished"));
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        mIsLoading = false;
                        if (mCallback != null && mCallback.istable()) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "onReceivedError"),
                                    valueOf(error.getErrorCode()),
                                    valueOf(String.valueOf(error.getDescription())),
                                    valueOf(String.valueOf(request.getUrl())));
                        }
                    }
                });
            } else {
                view.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;        // 使其不要在外部浏览器中响应
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        mIsLoading = true;
                        if (mCallback != null && mCallback.istable()) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "onPageStarted"));
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        mIsLoading = false;
                        if (mCallback != null && mCallback.istable()) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "onPageFinished"));
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        mIsLoading = false;
                        if (mCallback != null && mCallback.istable()) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "onReceivedError"), errorCode, description, failingUrl);
                        }
                    }
                });
            }
        }
    }

    /**
     * Loads the given URL.
     * @param url
     * @return
     */
    public UDWebView loadUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            final LVWebView view = this.getView();
            if (view != null) {
                view.loadUrl(url);
            }
        }

        return this;
    }

    public boolean canGoBack() {
        return getView() != null ? getView().canGoBack() : false;
    }

    public boolean canGoForward() {
        return getView() != null ? getView().canGoForward() : false;
    }

    public UDWebView goBack() {
        final LVWebView view = this.getView();
        if (view != null)
            view.goBack();

        return this;
    }

    public UDWebView goForward() {
        final LVWebView view = this.getView();
        if (view != null)
            view.goForward();

        return this;
    }

    public UDWebView reload() {
        final LVWebView view = this.getView();
        if (view != null)
            view.reload();

        return this;
    }

    public UDWebView stopLoading() {
        final LVWebView view = this.getView();
        if (view != null)
            view.stopLoading();

        return this;
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    /**
     * Get the tile of web page
     * @return
     */
    public String title() {
        return getView() != null ? getView().getTitle() : "";
    }

    /**
     * Get the loaded URL
     * @return
     */
    public String url() {
        return getView() != null ? getView().getUrl() : "";
    }

    @Override
    public UDView setCallback(LuaValue callbacks) {
        this.mCallback = callbacks;
        return this;
    }
}
