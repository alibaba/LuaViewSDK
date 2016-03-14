package com.taobao.luaview.scriptbundle;

import java.util.HashMap;

/**
 * 脚本文件包，每一个加载的脚本包，加载成功后返回该bundle
 *
 * @author song
 * @date 15/11/10
 */
public class ScriptBundle {
    private String mBaseFilePath;
    private HashMap<String, ScriptFile> mScriptFileMap;

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

    public void setBaseFilePath(String mBaseFilePath) {
        this.mBaseFilePath = mBaseFilePath;
    }

    public String getBaseFilePath() {
        return mBaseFilePath;
    }

    public boolean containsKey(final String key) {
        return mScriptFileMap != null && mScriptFileMap.containsKey(key);
    }

    public ScriptFile getScriptFile(final String key) {
        return mScriptFileMap != null ? mScriptFileMap.get(key) : null;
    }
}
