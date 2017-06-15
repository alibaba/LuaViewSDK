/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.demo.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.taobao.luaview.global.Constants;
import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.scriptbundle.LuaScriptManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends ListActivity {
    //测试标准语法
    public static boolean useStandardSyntax = true;

    //非标准语法代码路径
    private static final String FOLDER_NAME = "test";

    //标准语法代码路径
    private static final String FOLDER_STANDARD_NAME = "test-standard";

    private String getFolderName() {
        return (useStandardSyntax ? FOLDER_STANDARD_NAME : FOLDER_NAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
        initLuaView();
    }

    private void initContent() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getContentData());

        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String fileName = adapter.getItem(position - getListView().getHeaderViewsCount());
                final Intent intent = new Intent(DemoActivity.this, DemoLuaViewActivity.class);
                intent.putExtra(Constants.PARAM_URI, getFolderName() + "/" + fileName);
                startActivity(intent);
            }
        });
    }

    private List<String> getContentData() {
        String[] array = null;
        List<String> result = new ArrayList<String>();
        try {
            array = getResources().getAssets().list(getFolderName());

            if (array != null) {
                for (String name : array) {
                    if (LuaScriptManager.isLuaScript(name)) {
                        result.add(name);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 初始化LuaView
     */
    private void initLuaView() {
        LuaViewConfig.init(this);//初始化，只需要调用一次
        LuaViewConfig.setDebug(true);//设置是否debug，默认release模式下不会开启
        LuaViewConfig.setOpenDebugger(false);//是否开启调试器，默认模拟器环境会开启，真机不会开启。TODO Android 真机调试
        LuaViewConfig.setAutoSetupClickEffects(true);//是否自动设置点击效果（如果有点击事件会自动设置）
    }
}
