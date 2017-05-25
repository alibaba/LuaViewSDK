/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */
package com.taobao.luaview.global;

import android.content.Context;

import com.taobao.android.luaview.R;
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
    public static final String PAGE_PARAMS = "page_params";

    //Bundle encrypt and decrypt
    public static final String PUBLIC_KEY_PATH = "luaview/luaview_rsa_public_key.der";
    public static final String PUBLIC_KEY_PATH_MD5 = "luaview/luaview_rsa_public_key.der-md5";
    public static final String PUBLIC_KEY_PATH_PK = "luaview/luaview_rsa_public_key.der-pk";
    public static final String PUBLIC_KEY_PATH_CIPHER = "luaview/luaview_rsa_public_key.der-cipher";

    //resources tag
    public static final int RES_LV_TAG_URL = R.id.lv_tag_url;
    public static final int RES_LV_TAG = R.id.lv_tag;
    public static final int RES_LV_TAG_POSITION = R.id.lv_tag_position;
    public static final int RES_LV_TAG_PINNED = R.id.lv_tag_pinned;
    public static final int RES_LV_TAG_INIT = R.id.lv_tag_init;

    public static void init(Context context) {
        sScale = AndroidUtil.getDensity(context);
    }
}