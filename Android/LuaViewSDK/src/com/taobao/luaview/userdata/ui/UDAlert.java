/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.content.DialogInterface;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.AlertUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.List;

/**
 * Alert 数据封装
 * @author song 
 */
public class UDAlert extends BaseUserdata {
    //dialog属性
    private CharSequence mTitle;
    private CharSequence mContent;
    private List<CharSequence> mButtonTexts;
    private List<LuaFunction> mButtonCallbacks;

    public UDAlert(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
        init();
    }

    /**
     * 初始化数据
     */
    private void init() {
        mTitle = LuaViewUtil.getText(getInitParam1());
        mContent = LuaViewUtil.getText(getInitParam2());
        mButtonTexts = new ArrayList<CharSequence>();
        mButtonCallbacks = new ArrayList<LuaFunction>();
        for (int i = 3; i <= getInitParamsCount(); i++) {
            if (this.initParams.isfunction(i)) {
                mButtonCallbacks.add(this.initParams.optfunction(i, null));
            } else {
                mButtonTexts.add(LuaViewUtil.getText(this.initParams.optvalue(i, NIL)));
            }
        }
        showDialog();
    }

    /**
     * 创建dialog
     */
    private void showDialog() {
        int buttonCount = Math.max(mButtonTexts.size(), mButtonCallbacks.size());
        switch (buttonCount) {
            case 0:
            case 1:
                showOneButtonDialog();
                break;
            case 2:
                showTwoButtonDialog();
                break;
        }
    }

    /**
     * 创建一个按钮的对话框
     */
    private void showOneButtonDialog() {
        final CharSequence ok = mButtonTexts.size() > 0 ? mButtonTexts.get(0) : null;
        AlertUtil.showAlert(getContext(), mTitle, mContent, ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mButtonCallbacks.size() > 0) {
                    LuaUtil.callFunction(mButtonCallbacks.get(0));
                }
            }
        });
    }

    /**
     * 创建两个按钮的对话框
     */
    private void showTwoButtonDialog() {
        final CharSequence ok = mButtonTexts.size() > 0 ? mButtonTexts.get(0) : null;
        final CharSequence cancel = mButtonTexts.size() > 1 ? mButtonTexts.get(1) : null;
        AlertUtil.showAlert(getContext(), mTitle, mContent, ok, cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mButtonCallbacks.size() > 0) {
                    LuaUtil.callFunction(mButtonCallbacks.get(0));
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mButtonCallbacks.size() > 1) {
                    LuaUtil.callFunction(mButtonCallbacks.get(1));
                }
            }
        });
    }
}
