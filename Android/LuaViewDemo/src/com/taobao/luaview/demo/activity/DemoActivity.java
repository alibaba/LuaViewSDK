package com.taobao.luaview.demo.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.taobao.luaview.global.Constants;
import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends ListActivity {
    private static final String FOLDER_NAME = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化
        init();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getData());

        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String fileName = adapter.getItem(position - getListView().getHeaderViewsCount());
                final Intent intent = new Intent(DemoActivity.this, DemoLuaViewActivity.class);
                intent.putExtra(Constants.PARAM_URI, FOLDER_NAME + "/" + fileName);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化
     */
    private void init() {
        LuaViewConfig.setDebug(true);
        LuaViewConfig.setOpenDebugger(false);
        LuaViewConfig.setLibsLazyLoad(true);
//        LuaViewConfig.setUseLuaDC(true);
    }

    private List<String> getData() {
        String[] array = null;
        List<String> result = new ArrayList<String>();
        try {
            array = getResources().getAssets().list(FOLDER_NAME);

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
}
