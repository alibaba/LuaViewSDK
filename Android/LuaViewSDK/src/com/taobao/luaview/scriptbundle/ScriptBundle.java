/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle;

import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.util.FileUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 脚本文件包，每一个加载的脚本包，加载成功后返回该bundle
 *
 * @author song
 * @date 15/11/10
 */
public class ScriptBundle {
    //脚本网络地址
    private String mUrl;

    //脚本本地地址
    private String mBaseFilePath;

    //脚本文件
    private HashMap<String, ScriptFile> mScriptFileMap;

    //是否是bytecode
    private boolean isBytecode;

    //config，TODO 可以根据脚本包里的配置来控制虚拟机的运行
    private Properties mProps;

    public ScriptBundle() {
        mScriptFileMap = new HashMap<String, ScriptFile>();
    }

    public ScriptBundle addScript(ScriptFile scriptFile) {
        if (mScriptFileMap != null) {
            mScriptFileMap.put(scriptFile.fileName, scriptFile);
        }
        return this;
    }

    public int size() {
        return mScriptFileMap != null ? mScriptFileMap.size() : 0;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setBytecode(boolean isBytecode) {
        this.isBytecode = isBytecode;
    }

    public void setBaseFilePath(String mBaseFilePath) {
        this.mBaseFilePath = mBaseFilePath;
    }

    public String getBaseFilePath() {
        return mBaseFilePath;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isBytecode() {
        return isBytecode;
    }

    public Map<String, ScriptFile> getScriptFileMap() {
        return mScriptFileMap;
    }

    public boolean containsKey(final String key) {
        return mScriptFileMap != null && mScriptFileMap.containsKey(key);
    }

    public ScriptFile getScriptFile(final String key) {
        return mScriptFileMap != null ? mScriptFileMap.get(key) : null;
    }

    public void saveToFile(final String path) {
        if (path != null) {
            if (mScriptFileMap != null) {
                new SimpleTask1<Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        ScriptFile file = null;
                        for (String key : mScriptFileMap.keySet()) {
                            file = mScriptFileMap.get(key);
                            if (file != null) {
                                FileUtil.save(path + "/" + file.fileName, file.scriptData);
                            }
                        }
                        return null;
                    }
                }.executeInPool();
            }
        }
    }
}
