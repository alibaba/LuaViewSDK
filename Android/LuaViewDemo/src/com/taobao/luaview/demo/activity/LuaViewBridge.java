/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.taobao.luaview.util.LogUtil;

import org.luaj.vm2.LuaTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LuaView & Native 接口
 *
 * @author song
 * @date 15/12/30
 * 主要功能描述
 * 修改描述
 * 下午2:31 song XXX
 */
public class LuaViewBridge {
    private Activity mActivity;

    public LuaViewBridge(Activity activity) {
        this.mActivity = activity;
    }

    public void openPage(String pageUri){
        Intent intent = new Intent();
        intent.setData(Uri.parse(pageUri));
        mActivity.startActivity(intent);
    }

    public boolean isLogin(){
        return true;
    }

    public Map<String, String> testMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "map1");
        map.put("key2", "map2");
        map.put("key3", "map3");
        return map;
    }

    public List<String> testList() {
        return Arrays.asList(new String[]{"list1", "list2", "list3"});
    }

    public String testString() {
        return "string";
    }

    public int testInt() {
        return 0;
    }

    public Integer testInt2() {
        return 1;
    }

    public long testLong() {
        return 0L;
    }

    public Long testLong2() {
        return 0L;
    }

    public double testDouble() {
        return 0.0;
    }

    public Double testDouble2() {
        return 0.0;
    }

    public boolean testBoolean() {
        return true;
    }

    public Boolean testBoolean2() {
        return false;
    }

    public void testParams(LuaTable value){
    }
}
