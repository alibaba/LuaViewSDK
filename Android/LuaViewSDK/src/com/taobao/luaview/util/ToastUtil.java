/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * toast a message
 *
 * @author song
 */
public class ToastUtil {
    private static CharSequence oldMsg;
    private static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static int LENGTH = Toast.LENGTH_SHORT;
    private static Handler handler;

    //--toast view--
    private static View oldView;
    private static Toast viewToast = null;
    private static long oneTimeView = 0;
    private static long twoTimeView = 0;


    /**
     * show a toast directly
     *
     * @param context
     * @param msg
     */
    public static void showToast(final Context context, final CharSequence msg) {
        if (context == null || msg == null || msg.length() == 0)
            return;
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context, msg, LENGTH);
                    toast.show();
                    oneTime = System.currentTimeMillis();
                } else {
                    twoTime = System.currentTimeMillis();
                    if (msg.equals(oldMsg)) {//only show msg when time elapse or show a different msg
                        if (twoTime - oneTime > LENGTH) {
                            toast.show();
                        }
                    } else {
                        oldMsg = msg;
                        toast.setText(msg);
                        toast.show();
                    }
                }
                oneTime = twoTime;
            }
        });
    }

    /**
     * show toast
     */
    public static void showToast(final Context context, final View view) {
        showToast(context, view, Toast.LENGTH_SHORT, -1, -1);
    }

    /**
     * show toast at position
     *
     * @param context
     * @param view
     * @param x
     * @param y
     */
    public static void showToast(final Context context, final View view, final Integer duration, final Integer x, final Integer y) {
        if (context == null || view == null)
            return;
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (viewToast == null) {
                    viewToast = new Toast(context);
                    if (duration != null) {
                        viewToast.setDuration(duration);
                    }

                    if (x != null && y != null) {
                        viewToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, x, y);
                    }

                    viewToast.setView(view);
                    oldView = view;

                    viewToast.show();
                    oneTimeView = System.currentTimeMillis();
                } else {
                    twoTimeView = System.currentTimeMillis();
                    if (view.equals(oldView)) {//only show msg when time elapse or show a different msg
                        if (twoTimeView - oneTimeView > LENGTH) {
                            viewToast.show();
                        }
                    } else {
                        oldView = view;
                        viewToast.setView(view);
                        viewToast.show();
                    }
                }
                oneTimeView = twoTimeView;
            }
        });
    }

    /**
     * show a toast using resource Globals.getId()
     *
     * @param context
     * @param resId
     */
    public static void showToast(final Context context, final int resId) {
        showToast(context, resId, false);
    }

    /**
     * show a toast using resource Globals.getId()
     *
     * @param context
     * @param resId
     * @param isLong
     */
    public static void showToast(final Context context, final int resId, final boolean isLong) {
        if (isLong) {
            LENGTH = Toast.LENGTH_LONG;
        } else {
            LENGTH = Toast.LENGTH_SHORT;
        }
        showToast(context, context.getString(resId));
    }
}
