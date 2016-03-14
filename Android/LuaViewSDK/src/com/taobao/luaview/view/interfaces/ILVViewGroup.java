package com.taobao.luaview.view.interfaces;


import android.view.View;

import org.luaj.vm2.Varargs;

/**
 * ViewGroup interface
 *
 * @author song
 * @date 15/8/20
 */
public interface ILVViewGroup extends ILVView {
    void addLVView(View view, Varargs varargs);
}
