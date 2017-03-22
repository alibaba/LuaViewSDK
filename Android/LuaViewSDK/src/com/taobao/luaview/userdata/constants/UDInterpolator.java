/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.constants;


import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Interpolator 封装
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标", "ios部分无"})
public class UDInterpolator extends BaseLuaTable {

    public UDInterpolator(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        initInterpolator();
    }

    /**
     * init interpolator
     */
    private void initInterpolator() {
        set("ACCELERATE_DECELERATE", 0);
        set("ACCELERATE", 1);
        set("ANTICIPATE", 2);
        set("ANTICIPATE_OVERSHOOT", 3);
        set("BOUNCE", 4);//TODO IOS 无
        set("CYCLE", 5);//TODO IOS 无
        set("DECELERATE", 6);
        set("LINEAR", 7);
        set("OVERSHOOT", 8);
        //暂时不支持的
//        set("FAST_OUT_LINEAR", 9);
//        set("FAST_OUT_SLOW_IN", 10);
//        set("LINEAR_OUT_SLOW_IN", 11);
//        set("PATH", 12);
    }

    public static Interpolator parse(Integer type, Float cycles) {
        if (type != null) {
            switch (type) {
                case 0:
                    return new AccelerateDecelerateInterpolator();
                case 1:
                    return new AccelerateInterpolator();
                case 2:
                    return new AnticipateInterpolator();
                case 3:
                    return new AnticipateOvershootInterpolator();
                case 4:
                    return new BounceInterpolator();
                case 5:
                    return new CycleInterpolator((cycles != null && cycles > 0) ? cycles : 1f);
                case 6:
                    return new DecelerateInterpolator();
                case 7:
                    return new LinearInterpolator();
                case 8:
                    return new OvershootInterpolator();
                //暂时不支持的
//            case 7: return new FastOutLinearInterplator();
//            case 8: return new FastOutSlowInInterplator();
//            case 10: return new LinearOutSlowInInterplator();
//            case 12: return new PathInterplator();
                default:
                    return new LinearInterpolator();
            }
        } else {
            return new LinearInterpolator();
        }
    }

}
