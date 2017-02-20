package com.taobao.luaview.view.interfaces;


import com.taobao.luaview.userdata.ui.UDView;

import java.util.ArrayList;

/**
 * ViewGroup interface
 *
 * @author song
 * @date 15/8/20
 */
public interface ILVViewGroup extends ILVView {
    void setChildNodeViews(ArrayList<UDView> childNodeViews);
}
