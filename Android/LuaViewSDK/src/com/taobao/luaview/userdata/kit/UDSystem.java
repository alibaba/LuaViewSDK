/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.SdkVersion;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.AndroidUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.NetworkUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * System 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UDSystem extends BaseLuaTable {

    public UDSystem(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        set("ios", new ios());//是否ios
        set("android", new android());//是否android
        set("vmVersion", new vmVersion());//LuaView早期的版本系统 @Deprecated
        set("sdkVersion", new sdkVersion());//LuaView版本
        set("osVersion", new osVersion());//系统版本
        set("platform", new platform());//平台信息
        set("scale", new scale());//屏幕分辨率
        set("device", new device());//设备信息
        set("screenSize", new screenSize());//屏幕尺寸
        set("network", new network());//获取网络
        set("gc", new gc());
        set("keepScreenOn", new keepScreenOn());
    }

    class ios extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return LuaBoolean.FALSE;
        }
    }

    class android extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return LuaBoolean.TRUE;
        }
    }

    @Deprecated
    class vmVersion extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return valueOf(VmVersion.getCurrent());
        }
    }

    class sdkVersion extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return valueOf(SdkVersion.getCurrent());
        }
    }

    class osVersion extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return valueOf(AndroidUtil.getOsVersion());
        }
    }

    class platform extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return valueOf(AndroidUtil.getOsModel());
        }
    }

    class scale extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return valueOf(AndroidUtil.getDensity(getContext()));
        }
    }

    class device extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaTable table = new LuaTable();
            table.set("device", AndroidUtil.getDevice());
            table.set("brand", AndroidUtil.getBrand());
            table.set("product", AndroidUtil.getProduct());
            table.set("manufacturer", AndroidUtil.getManufacturer());

            //screen size
            int[] screenSize = AndroidUtil.getWindowSizeInDp(getContext());
            table.set("window_width", screenSize[0]);
            table.set("window_height", screenSize[1]);

            //action bar height
            int actionBarHeight = AndroidUtil.getActionBarHeightInDp(getContext());
            table.set("nav_height", actionBarHeight);
            int bottomNavHeight = AndroidUtil.getNavigationBarHeightInDp(getContext());
            table.set("bottom_nav_height", bottomNavHeight);
            int statusBarHeight = AndroidUtil.getStatusBarHeightInDp(getContext());
            table.set("status_bar_height", statusBarHeight);
            return table;
        }
    }


    /**
     * 系统屏幕大小
     */
    class screenSize extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return varargsOf(new LuaValue[]{valueOf(DimenUtil.pxToDpi(AndroidUtil.getScreenWidth(getContext()))), valueOf(DimenUtil.pxToDpi(AndroidUtil.getScreenHeight(getContext())))});
        }
    }

    /**
     * 系统垃圾回收
     */
    class gc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            java.lang.System.gc();
            return UDSystem.this;
        }
    }

    /**
     * 获取网络状态
     */
    class network extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            return valueOf(NetworkUtil.getCurrentTypeStr(getContext()));
        }
    }

    /**
     * 屏幕常亮开启
     */
    class keepScreenOn extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (getGlobals() != null && getGlobals().getRenderTarget() != null) {
                final Boolean keepScreenOn = LuaUtil.getBoolean(varargs, 2);
                getGlobals().getRenderTarget().setKeepScreenOn(keepScreenOn != null ? keepScreenOn : true);
            }
            return LuaValue.NIL;
        }
    }
}
