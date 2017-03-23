/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Alert
 * @author song
 */
public class AlertUtil {

    /**
     * show an system default alert dialog with given title, msg, ok, cancel, listeners
     *
     * @param context
     * @param title
     * @param msg
     * @param ok
     * @param cancel
     * @param lOk
     * @param lCancel
     */
    public static void showAlert(Context context, int title, int msg, int ok, int cancel, DialogInterface.OnClickListener lOk, DialogInterface.OnClickListener lCancel) {
        AlertDialog dialog = buildAlert(context, title, msg, ok, cancel, lOk, lCancel);
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * show an system default alert dialog with given title, msg, ok, cancal, listeners
     *
     * @param context
     * @param title
     * @param msg
     * @param ok
     * @param cancel
     * @param lOk
     * @param lCancel
     */
    public static void showAlert(Context context, CharSequence title, CharSequence msg, CharSequence ok, CharSequence cancel, DialogInterface.OnClickListener lOk, DialogInterface.OnClickListener lCancel) {
        AlertDialog dialog = buildAlert(context, title, msg, ok, cancel, lOk, lCancel);
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * show an system default alert dialog with given title, msg, ok, cancal, listeners
     *
     * @param context
     * @param title
     * @param msg
     * @param ok
     * @param lOk
     */
    public static void showAlert(Context context, CharSequence title, CharSequence msg, CharSequence ok, DialogInterface.OnClickListener lOk) {
        AlertDialog dialog = buildAlert(context, title, msg, ok, null, lOk, null);
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * build a alert dialog
     *
     * @param context
     * @param title
     * @param msg
     * @param ok
     * @param cancel
     * @param lOk
     * @param lCancel
     * @return
     */
    public static AlertDialog buildAlert(Context context, Integer title, Integer msg, Integer ok, Integer cancel, DialogInterface.OnClickListener lOk, DialogInterface.OnClickListener lCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (msg != null) builder.setMessage(msg);
        if (ok != null) builder.setPositiveButton(ok, lOk);
        if (cancel != null) builder.setNegativeButton(cancel, lCancel);
        return builder.create();
    }

    /**
     * build a alert dialog
     *
     * @param context
     * @param title
     * @param msg
     * @param ok
     * @param cancel
     * @param lOk
     * @param lCancel
     * @return
     */
    public static AlertDialog buildAlert(Context context, CharSequence title, CharSequence msg, CharSequence ok, CharSequence cancel, DialogInterface.OnClickListener lOk, DialogInterface.OnClickListener lCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (msg != null) builder.setMessage(msg);
        if (ok != null) builder.setPositiveButton(ok, lOk);
        if (cancel != null) builder.setNegativeButton(cancel, lCancel);
        return builder.create();
    }

}
