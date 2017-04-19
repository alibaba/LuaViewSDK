/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.view.View;
import android.view.ViewGroup;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.UpValue;

/**
 * LuaView数据销毁
 *
 * @author song
 * @date 16/8/22
 * 主要功能描述
 * 修改描述
 * 下午4:32 song XXX
 */
public class DestroyUtil {
    private static final String KEY_DESTROY = "_isDestroy";

    public static void onDestroyClosure(LuaClosure closure) {
        if (closure != null) {
            if (closure.upValues != null) {
                UpValue upValue = null;
                for (int i = 0; i < closure.upValues.length; i++) {
                    upValue = closure.upValues[i];
//                    if (upValue != null && upValue.array != null) {
//                        for (LuaValue value : upValue.array) {//destroy upvalues
//                            if (value instanceof BaseUserdata) {//userdata destory
//                                ((BaseUserdata) value).onDestroy();
//                            } else if (value instanceof LuaTable) {//destroy table
//                                onDestroyTable((LuaTable) value);
//                            }
//                        }
//                    }
                    closure.upValues[i] = null;
                }
                closure.upValues = null;
            }
        }
    }

    public static void onDestroyTable(LuaTable table) {
        if (table != null) {
            LuaValue isDestroy = table.get(KEY_DESTROY);
            if (isDestroy == null || !LuaBoolean.TRUE.eq_b(isDestroy)) {
                table.set(KEY_DESTROY, LuaBoolean.TRUE);//标志位
                LuaValue value = null;
                View view = null;
                for (LuaValue key : table.keys()) {
                    value = table.get(key);
                    if (value instanceof UDView) {
                        view = ((UDView) value).getView();
                        if (view instanceof ViewGroup) {
                            clearViews((ViewGroup) view);
                        } else {
                            ((UDView) value).onDestroy();
                        }
                    } else if (value instanceof BaseUserdata) {
                        ((BaseUserdata) value).onDestroy();
                    } else if (value instanceof LuaTable) {
                        onDestroyTable((LuaTable) value);
                    }
                }
            }
        }
    }


    public static void clearViews(ViewGroup viewGroup) {
        LogUtil.d("onDestory", "clearViews", viewGroup);
        if (viewGroup != null) {
            View child = null;
            UDView udView = null;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);

                if (child instanceof ViewGroup) {
                    clearViews((ViewGroup) child);
                } else if (child instanceof ILVView) {
                    udView = ((ILVView) child).getUserdata();
                    if (udView != null) {
                        udView.onDestroy();
                    }
                }
            }

            if (viewGroup instanceof ILVView) {
                udView = ((ILVView) viewGroup).getUserdata();
                if (udView != null) {
                    udView.onDestroy();
                }
            }
        }
    }
}
