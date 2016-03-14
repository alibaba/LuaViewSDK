package com.taobao.luaview.userdata.list;

import android.widget.ListView;

import com.taobao.luaview.view.LVListView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * 容器类-ListView，模拟OC的section分区实现，Section顺序排列
 *
 * @author song
 * @date 15/8/20
 */
public class UDListView extends UDBaseListView<LVListView> {

    public UDListView(LVListView view, Globals globals, LuaValue metaTable, LuaValue initParams) {
        super(view, globals, metaTable, initParams);
    }

    @Override
    public ListView getListView() {
        return getView();
    }
}
