package com.taobao.luaview.global;

import android.content.Context;

import com.taobao.luaview.util.AndroidUtil;

/**
 * 常量类
 *
 * @author song
 * @date 15/10/26
 */
public class Constants {
    public static float sScale = -1;
    public static final String PARAM_URI = "uri";

    public static final String PUBLIC_KEY_PATH = "luaview/luaview_rsa_public_key.der";
    public static final String PUBLIC_KEY_PATH_MD5 = "luaview/luaview_rsa_public_key.der-md5";
    public static final String PUBLIC_KEY_PATH_PK = "luaview/luaview_rsa_public_key.der-pk";
    public static final String PUBLIC_KEY_PATH_CIPHER = "luaview/luaview_rsa_public_key.der-cipher";
    public static final String PUBLIC_KEY_TAG = "tag_public_key";

    public static void init(Context context) {
        sScale = AndroidUtil.getDensity(context);
    }
}