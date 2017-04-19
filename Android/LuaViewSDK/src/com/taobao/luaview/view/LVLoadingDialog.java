/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.taobao.android.luaview.R;


/**
 * 加载对话框
 *
 * @author song
 * @date 15/9/16
 */
public class LVLoadingDialog extends AlertDialog {
    private static LVLoadingDialog mLoadingDialog;

    public LVLoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lv_dialog_progress_default);
    }

    public static void startAnimating(Context context) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LVLoadingDialog(context);
        }
        mLoadingDialog.show();
    }

    public static void stopAnimating() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
        mLoadingDialog = null;
    }

    public static boolean isAnimating() {
        return mLoadingDialog != null && mLoadingDialog.isShowing();
    }
}
