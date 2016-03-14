package com.taobao.luaview.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;

/**
 * 获取系统一些属性
 *
 * @author song
 * @date 15/9/8
 */
public class AndroidUtil {

    /**
     * 系统版本号
     *
     * @return
     */
    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 系统版本号
     *
     * @return
     */
    public static String getSdkVersion() {
        return Build.VERSION.SDK;
    }

    /**
     * 系统版本号(int)
     *
     * @return
     */
    public static int getSdkVersionInt() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 手机型号
     *
     * @return
     */
    public static String getOsModel() {
        return Build.MODEL;
    }

    /**
     * get density of screen
     *
     * @param context Context
     * @return
     */
    public static float getDensity(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().density;
    }

    /**
     * get screen width
     *
     * @param context Context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * get screen width
     *
     * @param context Context
     * @return
     */
    public static int getScreenWidthInDp(Context context) {
        return (int) DimenUtil.pxToDpi(context.getApplicationContext().getResources().getDisplayMetrics().widthPixels);
    }

    /**
     * get screen height
     *
     * @param context Context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * get screen height
     *
     * @param context Context
     * @return
     */
    public static int getScreenHeightInDp(Context context) {
        return (int) DimenUtil.pxToDpi(context.getApplicationContext().getResources().getDisplayMetrics().heightPixels);
    }


    /**
     * get action bar height
     *
     * @param context
     * @return
     */
    public static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        if (context instanceof Activity && ((Activity) context).getActionBar() != null) {
            actionBarHeight = ((Activity) context).getActionBar().getHeight();
        }

        if (actionBarHeight == 0) {
            final TypedValue tv = new TypedValue();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && (context.getTheme() != null && context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
            }
        }
        return actionBarHeight;
    }

    /**
     * get actionbar height
     *
     * @param context
     * @return
     */
    public static int getActionBarHeightInDp(Context context) {
        return (int) DimenUtil.pxToDpi(getActionBarHeight(context));
    }
}
