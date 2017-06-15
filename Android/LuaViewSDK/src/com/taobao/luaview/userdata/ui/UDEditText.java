/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * Edit Text 数据封装
 *
 * @author song
 */
public class UDEditText extends UDTextView<EditText> {

    private String mType = "text";

    public UDEditText(EditText view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    public UDEditText setInputType(CharSequence text) {
        EditText view = getView();
        if (view != null) {
            if (text.equals("number")) {
                view.setInputType(InputType.TYPE_CLASS_NUMBER);
                mType = "number";
            } else if(text.equals("password")) {
                view.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mType = "password";
            } else if(text.equals("visible_password")) {
                view.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mType = "visible_password";
            } else {
                view.setInputType(InputType.TYPE_CLASS_TEXT);
                mType = "text";
            }
        }
        return this;
    }

    public String getInputType() {
        return mType;
    }

    /**
     * 设置提示文本
     *
     * @param hint
     * @return
     */
    public UDEditText setHint(CharSequence hint) {
        EditText view = getView();
        if (view != null) {
            view.setHint(hint);
        }
        return this;
    }

    /**
     * 获取提示文本
     *
     * @return
     */
    public String getHint() {
        return getView() != null ? String.valueOf(getView().getHint()) : "";
    }

    /**
     * 设置EditText代理
     * BeginEditing --开始编辑
     * EndEditing -- 结束编辑
     * Clear -- 删除
     * Return --返回
     *
     * @return
     */
    public UDEditText setCallback(final LuaValue callback) {
        this.mCallback = callback;
        final EditText view = getView();
        if (view != null) {
            if (this.mCallback != null && this.mCallback.istable()) {
                view.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (mCallback != null && mCallback.istable()) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "BeginEditing", "beginEditing"));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (mCallback != null && mCallback.istable()) {
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "EndEditing", "endEditing"));
                        }
                    }
                });
            }
        }
        return this;
    }
}
