/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

/**
 * LuaView版本
 *
 * @author song
 */
public class SdkVersion {
    public static final String V_050 = "0.5.0";

    public static final String V_051 = "0.5.1";

    public static String getCurrent() {
        return V_051;
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