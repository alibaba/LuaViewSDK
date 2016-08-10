package com.taobao.luaview.scriptbundle;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 脚本文件类，封装了从服务端下发的脚本，包含脚本名称以及对应的代码
 *
 * @author song
 * @date 15/11/10
 */
public class ScriptFile {
    public String url;
    public String destBaseFilePath;
    public String fileName;
    public String script;

    public ScriptFile(final String url, final String destBaseFilePath, final String fileName, final String script) {
        this.url = url;
        this.destBaseFilePath = destBaseFilePath;
        this.fileName = LuaScriptManager.changeSuffix(fileName, LuaScriptManager.POSTFIX_LUA);//这里使用真实的lua脚本名称，因为这里的脚本都是被解密过的
        this.script = script;

//        LogUtil.d("[ScriptFile]", url, destBaseFilePath);
    }

    public String getFilePath() {
        StringBuffer sb = new StringBuffer().append(destBaseFilePath).append(fileName);
//        LogUtil.d("[ScriptFile-Path]", sb.toString());
        return sb.toString();
    }

    public InputStream getInputStream() {
        if (script != null) {
            return new ByteArrayInputStream(script.getBytes());
        }
        return null;
    }
}
