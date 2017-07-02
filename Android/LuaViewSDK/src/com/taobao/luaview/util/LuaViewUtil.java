/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.taobao.luaview.userdata.kit.UDUnicode;
import com.taobao.luaview.userdata.ui.UDSpannableString;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * LuaView相关的一些工具类
 *
 * @author song
 * @date 15/9/21
 */
public class LuaViewUtil {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * set id
     *
     * @param view
     */
    public static void setId(View view) {
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                try {//samsung SM-N9009(4.3) crash here, so protected
                    view.setId(View.generateViewId());
                } catch (Exception e) {
                    view.setId(generateViewId());
                }
            } else {
                view.setId(generateViewId());
            }
        }
    }

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    private static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * get text of given value
     *
     * @param inputValue
     * @return
     */
    public static CharSequence getText(LuaValue inputValue) {
        final LuaValue result = (inputValue != null && !inputValue.isnil()) ? inputValue : LuaValue.NIL;
        if (result instanceof UDSpannableString) {
            return ((UDSpannableString) result).getSpannableStringBuilder();
        } else if (result instanceof UDUnicode) {
            return result.toString();
        } else {
            return result.optjstring("");
        }
    }

    /**
     * 获得actionbar
     *
     * @param globals
     * @return
     */
    public static ActionBar getActionBar(Globals globals) {
        if (globals != null && globals.getContext() instanceof Activity) {
            return ((Activity) (globals.getContext())).getActionBar();
        }
        return null;
    }

//    public static android.support.v7.app.ActionBar getSupportActionBar(Globals globals) {
//        if (globals != null && globals.getContext() instanceof AppCompatActivity) {
//            return ((AppCompatActivity) (globals.getContext())).getSupportActionBar();
//        }
//        return null;
//    }


    /**
     * 设置背景
     *
     * @param view
     * @param drawable
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setBackground(View view, Drawable drawable) {
        if (view != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    //--------------------------------------------add view------------------------------------------

    /**
     * add target view to parent
     *
     * @param parent
     * @param target
     * @param varargs
     */
    public static void addView(ViewGroup parent, View target, Varargs varargs) {
        if (parent != null && target != null && parent != target) {//不能自己加自己
            final ViewGroup.LayoutParams layoutParams = LuaViewUtil.getOrCreateLayoutParams(target);
            parent.addView(LuaViewUtil.removeFromParent(target), layoutParams);
        }
    }

    /**
     * add target view to parent
     *
     * @param parent
     * @param target
     * @param varargs
     */
    public static void addView(ViewGroup parent, View target, int pos, Varargs varargs) {
        if (parent != null && target != null && parent != target) {//不能自己加自己
            final ViewGroup.LayoutParams layoutParams = LuaViewUtil.getOrCreateLayoutParams(target);
            parent.addView(LuaViewUtil.removeFromParent(target), pos, layoutParams);
        }
    }

    //--------------------------------------------remove--------------------------------------------

    /**
     * remove all views
     *
     * @param viewGroup
     */
    public static void removeAllViews(ViewGroup viewGroup) {
        if (viewGroup != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (viewGroup.isInLayout()) {
                    viewGroup.removeAllViewsInLayout();
                } else {
                    viewGroup.removeAllViews();
                }
            } else {
                viewGroup.removeAllViews();
            }
        }
    }

    /**
     * remove a view
     *
     * @param parent
     * @param view
     */
    public static void removeView(ViewGroup parent, View view) {
        //这里不使用post来做，这样代码更可控，而是改为将refresh下拉动作延后一帧处理，见@link
        //这里调用removeViewInLayout方法，可以在onLayout的时候调用，否则会产生问题
        if (parent != null && view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (parent.isInLayout()) {
                    parent.removeViewInLayout(view);
                } else {
                    parent.removeView(view);
                }
            } else {
                parent.removeView(view);
            }
        }
    }

    /**
     * 从父容器中移除该view
     *
     * @param view
     * @return
     */
    public static View removeFromParent(View view) {
        if (view != null && view.getParent() instanceof ViewGroup) {
            removeView((ViewGroup) view.getParent(), view);
        }
        return view;
    }

    //------------------------------------------layout params---------------------------------------

    /**
     * copy a layout params
     *
     * @param view
     * @return
     */
    public static ViewGroup.LayoutParams getOrCreateLayoutParams(View view) {
        if (view != null && view.getLayoutParams() != null) {//TODO 是否需要创建一个新的layout params
            return view.getLayoutParams();
        }
        return createRelativeLayoutParamsWW();
    }

    /**
     * create layout params WRAP_CONTENT, WRAP_CONTENT
     *
     * @return
     */
    public static RelativeLayout.LayoutParams createRelativeLayoutParamsWW() {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * create layout params MATCH_PARENT, MATCH_PARENT
     *
     * @return
     */
    public static RelativeLayout.LayoutParams createRelativeLayoutParamsMM() {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * create layout params MATCH_PARENT, WRAP_CONTENT
     *
     * @return
     */
    public static RelativeLayout.LayoutParams createRelativeLayoutParamsWM() {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * create layout params WRAP_CONTENT, MATCH_PARENT
     *
     * @return
     */
    public static RelativeLayout.LayoutParams createRelativeLayoutParamsMW() {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //------------------------------------------run on ui-------------------------------------------

    /**
     * run on ui thread
     *
     * @param view
     * @param runnable
     */
    public static void runOnUiThread(final View view, final Runnable runnable) {
        if (view != null) {
            if (view.getContext() instanceof Activity) {
                ((Activity) view.getContext()).runOnUiThread(runnable);
            } else {
                view.post(runnable);
            }
        }
    }

    /**
     * run on ui thread
     *
     * @param context
     * @param runnable
     */
    public static void runOnUiThread(final Context context, final Runnable runnable) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(runnable);
        } else {
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }
}
