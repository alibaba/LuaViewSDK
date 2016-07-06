package com.taobao.luaview.vm.extend.luadc;

import dalvik.system.DexClassLoader;

/**
 * Dex 文件Loader
 *
 * @author song
 * @date 16/7/6
 * 主要功能描述
 * 修改描述
 * 上午10:53 song XXX
 */
public class DexLoader extends DexClassLoader {

    public DexLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
    }

}
