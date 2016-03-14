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
    public String fileName;
    public String script;

    public ScriptFile(final String fileName, final String script) {
        this.fileName = LuaScriptManager.changeSuffix(fileName, LuaScriptManager.POSTFIX_LUA);//这里使用真实的lua脚本名称，因为这里的脚本都是被解密过的
        this.script = script;
    }

    public InputStream getInputStream() {
        if (script != null) {
            return new ByteArrayInputStream(script.getBytes());
        }
        return null;
    }
}
