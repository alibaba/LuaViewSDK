/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

/**
 * LuaView虚拟机版本
 *
 * @author song
 */
@Deprecated
public class VmVersion {
    public static final String V_440 = "4.4.0";
    public static final String V_450 = "4.5.0";
    public static final String V_451 = "4.5.1";
    public static final String V_500 = "5.0.0";
    public static final String V_501 = "5.0.1";
    public static final String V_510 = "5.1.0";
    public static final String V_511 = "5.1.1";
    public static final String V_520 = "5.2.0";
    public static final String V_530 = "5.3.0";
    public static final String V_540 = "5.4.0";
    public static final String V_550 = "5.5.0";
    public static final String V_570 = "5.7.0";
    public static final String V_580 = "5.8.0";
    public static final String V_590 = "5.9.0";
    public static final String V_5170 = "5.17.0";

    public static String getCurrent() {
        return V_5170;
    }

    /**
     * 是否比某个版本更新
     *
     * @param compareVersion
     * @return
     */
    public static boolean isHigherThan(String compareVersion) {
        return getCurrent().compareTo(compareVersion) > 0;
    }
}