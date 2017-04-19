/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle.asynctask.delegate;

import android.content.Context;

import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.util.DebugUtil;
import com.taobao.luaview.util.DecryptUtil;
import com.taobao.luaview.util.IOUtil;
import com.taobao.luaview.util.VerifyUtil;
import com.taobao.luaview.util.ZipUtil;

import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Prototype;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * ScriptBundle Load Delegate
 *
 * @author song
 * @date 17/2/8
 * 主要功能描述
 * 修改描述
 * 下午2:44 song XXX
 */

public class ScriptBundleLoadDelegate {
    public ScriptBundleLoadDelegate() {
    }

    public ScriptBundle load(Context context, ScriptBundle scriptBundle) {
        if (scriptBundle != null && scriptBundle.size() > 0 && verifyAllScripts(context, scriptBundle)) {//强校验，如果脚本存在并且校验成功才返回
            Map<String, ScriptFile> files = scriptBundle.getScriptFileMap();
            ScriptFile scriptFile = null;

            for (String key : files.keySet()) {
                scriptFile = files.get(key);
                scriptFile.scriptData = loadEncryptScript(context, scriptBundle.isBytecode(), scriptFile);
                if (scriptBundle.isBytecode()) {//如果是bytecode，则加载prototype
                    DebugUtil.tsi("luaviewp-loadPrototype");
                    scriptFile.prototype = loadPrototype(scriptFile); // TODO 性能瓶颈
                    DebugUtil.tei("luaviewp-loadPrototype");
                }
            }
            return scriptBundle;
        } else {
            return null;
        }
    }

    //------------------------------------------加载脚本函数------------------------------------------

    /**
     * load a prototype
     *
     * @param scriptFile
     * @return
     */
    private Prototype loadPrototype(final ScriptFile scriptFile) {
        if (LoadState.instance != null && scriptFile != null) {
            try {
                return LoadState.instance.undump(new BufferedInputStream(new ByteArrayInputStream(scriptFile.scriptData)), scriptFile.getFilePath());//TODO 低端机性能上可以进一步优化
            } catch (LuaError error) {
                error.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 加载一个脚本
     *
     * @param context
     * @return
     */
    public static InputStream loadEncryptScript(final Context context, final InputStream inputStream) {
        if (inputStream != null) {
            InputStream result = new ByteArrayInputStream(ZipUtil.unzip(DecryptUtil.aes(context, IOUtil.toBytes(inputStream))));
            try {
                inputStream.close();
            } catch (Exception e) {//close input, 这里需要注意，外面不能使用该inputStream
            }
            return result;
        }
        return inputStream;
    }

    /**
     * 加载加密过的脚本
     *
     * @param context
     * @param scriptFile
     * @return
     */
    private static byte[] loadEncryptScript(final Context context, final boolean isBytecode, final ScriptFile scriptFile) {
        if (scriptFile != null) {
            if (isBytecode) {//bytecode不进行unzip
                if (scriptFile.signData != null && scriptFile.signData.length > 0) {//加密过则进行解密并unzip
                    return DecryptUtil.aes(context, scriptFile.scriptData);
                } else {
                    return scriptFile.scriptData;
                }
            } else {
                if (scriptFile.signData != null && scriptFile.signData.length > 0) {//加密过则进行解密并unzip
                    return ZipUtil.unzip(DecryptUtil.aes(context, scriptFile.scriptData));
                } else {
                    return ZipUtil.unzip(scriptFile.scriptData);
                }
            }
        }
        return null;
    }

    /**
     * 加载加密过的脚本
     *
     * @param context
     * @param script
     * @return
     */
    private static byte[] loadEncryptScript(final Context context, final byte[] script) {
        return ZipUtil.unzip(DecryptUtil.aes(context, script));
    }

    /**
     * 验证所有脚本，有任何一个失败则返回失败
     *
     * @param bundle
     * @return
     */
    private static boolean verifyAllScripts(Context context, ScriptBundle bundle) {
        Map<String, ScriptFile> files = bundle != null ? bundle.getScriptFileMap() : null;
        if (files != null) {
            for (final String key : files.keySet()) {
                if (verifyScript(context, bundle.isBytecode(), files.get(key)) == false) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 验证一个脚本
     *
     * @param isBytecode
     * @param script
     * @return
     */
    private static boolean verifyScript(Context context, boolean isBytecode, ScriptFile script) {
        if (script != null) {
            if (isBytecode) {//bytecode 模式下，如果没有signdata也算验证通过
                if (script.signData != null && script.signData.length > 0) {
                    return VerifyUtil.rsa(context, script.scriptData, script.signData);
                }
                return true;
            } else {
                return VerifyUtil.rsa(context, script.scriptData, script.signData);
            }
        }
        return false;
    }
}
