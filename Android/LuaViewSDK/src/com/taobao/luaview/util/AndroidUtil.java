/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;

/**
 * 获取系统一些属性
 *
 * @author song
 * @date 15/9/8
 */
public class AndroidUtil {

    /**
     * 获取可用的内存大小(in k)
     *
     * @param context
     * @return
     */
    public static Long getAvailMemorySize(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi != null ? mi.availMem : null;
    }

    /**
     * 获取所有的内存大小(in k)
     *
     * @param context
     * @return
     */
    @TargetApi(16)
    public static Long getTotalMemorySize(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return mi != null ? mi.totalMem : null;
        } else {
            return null;
        }
    }

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
     * 系统品牌
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getProduct() {
        return Build.PRODUCT;
    }

    public static String getDevice() {
        return Build.DEVICE;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
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
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        final DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    public static int[] getScreenSizeInDp(Context context) {
        final int[] size = getScreenSize(context);
        return new int[]{(int) DimenUtil.pxToDpi(size[0]), (int) DimenUtil.pxToDpi(size[1])};
    }

    /**
     * 获取window的size
     *
     * @param context
     * @return
     */
    public static int[] getWindowSize(Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point point = new Point();
            display.getSize(point);
            return new int[]{point.x, point.y};
        } else {
            return new int[]{display.getWidth(), display.getHeight()};
        }
    }

    /**
     * 获取window的size
     *
     * @param context
     * @return
     */
    public static int[] getWindowSizeInDp(Context context) {
        final int[] size = getWindowSize(context);
        return new int[]{(int) DimenUtil.pxToDpi(size[0]), (int) DimenUtil.pxToDpi(size[1])};
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


    /**
     * 获取系统的Navigation bar height
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeightInDp(Context context) {
        Point size = getNavigationBarSize(context);
        return (int) DimenUtil.pxToDpi(size != null ? size.y : 0);
    }

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return size;
    }

    /**
     * get status bar height in dp
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeightInDp(Context context) {
        return (int) DimenUtil.pxToDpi(getStatusBarHeight(context));
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
