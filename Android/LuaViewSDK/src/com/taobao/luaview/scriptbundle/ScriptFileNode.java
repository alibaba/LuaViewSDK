package com.taobao.luaview.scriptbundle;


/**
 * 保存服务端下载的脚本信息，每个node是一个文件
 * @author song
 */
public class ScriptFileNode {
    public String fileName;
    public byte[] bytes;
    public byte[] signBytes;//签名信息
    public boolean isLuaScript;

    public ScriptFileNode(final String fileName, final byte[] bytes, final byte[] signBytes) {
        this.fileName = fileName;
        this.bytes = bytes;
        this.signBytes = signBytes;
        this.isLuaScript = LuaScriptManager.isLuaEncryptScript(fileName);
    }

}