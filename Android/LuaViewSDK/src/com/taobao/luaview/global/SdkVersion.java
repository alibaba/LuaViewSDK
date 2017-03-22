package com.taobao.luaview.global;

/**
 * LuaView版本
 *
 * @author song
 */
public class SdkVersion {
    public static final String V_050 = "0.5.0";

    public static String getCurrent() {
        return V_050;
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