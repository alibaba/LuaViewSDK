package com.taobao.luaview.global;

/**
 * LuaView虚拟机版本
 *
 * @author song
 */
public class VmVersion {
    public static final String V_440 = "4.4.0";
    public static final String V_450 = "4.5.0";
    public static final String V_451 = "4.5.1";
    public static final String V_500 = "5.0.0";

    public static String getCurrent() {
        return V_500;
    }

    /**
     * 是否比某个版本更新
     * @param compareVersion
     * @return
     */
    public static boolean isHigherThan(String compareVersion) {
        return getCurrent().compareTo(compareVersion) > 0;
    }
}