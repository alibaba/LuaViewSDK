package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.fun.mapper.list.UIRefreshRecyclerViewMethodMapper;
import com.taobao.luaview.view.LVRefreshRecyclerView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * 容器类，放各个view, Refreshable CollectionView/RecyclerView
 *
 * @author song
 * @date 15/8/20
 */
public class UIRefreshRecyclerViewBinder extends BaseFunctionBinder {

    public UIRefreshRecyclerViewBinder() {
        super("RefreshCollectionView");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UIRefreshRecyclerViewMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new LVRefreshRecyclerView(globals, metaTable, varargs);
            }
        };
    }
}
