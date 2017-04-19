/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.taobao.luaview.global.LuaView;

/**
 * LuaView Dialog
 *
 * @author song
 * @date 15/12/30
 */
public class LuaViewDialog extends AlertDialog {
    private LuaView mLuaView;

    /**
     * create a transparent without title Dialog
     *
     * @param context
     * @return
     */
    public static LuaViewDialog create(Context context) {
        return new LuaViewDialog(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    /**
     * create a fullscreen transparent without title Dialog
     *
     * @param context
     * @return
     */
    public static LuaViewDialog createFullScreen(Context context) {
        return new LuaViewDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }


    public LuaViewDialog(Context context) {
        super(context);
        mLuaView = LuaView.create(context);
    }

    /**
     * create dialog with theme
     *
     * @param context
     * @param theme
     */
    public LuaViewDialog(Context context, int theme) {
        super(context, theme);
        mLuaView = LuaView.create(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLuaView);
    }

    public LuaView getLuaView() {
        return mLuaView;
    }

    //-----------------------------------------load-------------------------------------------------

    public LuaViewDialog load(final String url) {
        if (mLuaView != null) {
            mLuaView.load(url);
        }
        return this;
    }

    public LuaViewDialog loadScript(final String script) {
        if (mLuaView != null) {
            mLuaView.loadScript(script);
        }
        return this;
    }

}
