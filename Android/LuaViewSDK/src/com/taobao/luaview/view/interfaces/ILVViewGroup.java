package com.taobao.luaview.view.interfaces;


import android.view.View;

import com.taobao.luaview.userdata.ui.UDView;

import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * ViewGroup interface
 *
 * @author song
 * @date 15/8/20
 */
public interface ILVViewGroup extends ILVView {
    void addLVView(View view, Varargs varargs);
    void setChildNodeViews(ArrayList<UDView> childNodeViews);
}
