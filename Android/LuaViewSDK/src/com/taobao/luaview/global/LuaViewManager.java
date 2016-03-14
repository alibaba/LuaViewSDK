package com.taobao.luaview.global;

import com.taobao.luaview.fun.binder.constants.AlignBinder;
import com.taobao.luaview.fun.binder.constants.EllipsizeBinder;
import com.taobao.luaview.fun.binder.constants.FontStyleBinder;
import com.taobao.luaview.fun.binder.constants.FontWeightBinder;
import com.taobao.luaview.fun.binder.constants.GravityBinder;
import com.taobao.luaview.fun.binder.constants.InterpolatorBinder;
import com.taobao.luaview.fun.binder.constants.ScaleTypeBinder;
import com.taobao.luaview.fun.binder.constants.TextAlignBinder;
import com.taobao.luaview.fun.binder.indicator.UICircleViewPagerIndicatorBinder;
import com.taobao.luaview.fun.binder.indicator.UICustomViewPagerIndicatorBinder;
import com.taobao.luaview.fun.binder.kit.ActionBarBinder;
import com.taobao.luaview.fun.binder.kit.AudioBinder;
import com.taobao.luaview.fun.binder.kit.DataBinder;
import com.taobao.luaview.fun.binder.kit.DownloaderBinder;
import com.taobao.luaview.fun.binder.kit.JsonBinder;
import com.taobao.luaview.fun.binder.kit.SystemBinder;
import com.taobao.luaview.fun.binder.kit.TimerBinder;
import com.taobao.luaview.fun.binder.kit.UnicodeBinder;
import com.taobao.luaview.fun.binder.kit.VibratorBinder;
import com.taobao.luaview.fun.binder.net.HttpBinder;
import com.taobao.luaview.fun.binder.ui.SpannableStringBinder;
import com.taobao.luaview.fun.binder.ui.UIAlertBinder;
import com.taobao.luaview.fun.binder.ui.UIAnimatorBinder;
import com.taobao.luaview.fun.binder.ui.UIButtonBinder;
import com.taobao.luaview.fun.binder.ui.UIEditTextBinder;
import com.taobao.luaview.fun.binder.ui.UIHorizontalScrollViewBinder;
import com.taobao.luaview.fun.binder.ui.UIImageViewBinder;
import com.taobao.luaview.fun.binder.ui.UIListViewBinder;
import com.taobao.luaview.fun.binder.ui.UILoadingDialogBinder;
import com.taobao.luaview.fun.binder.ui.UILoadingViewBinder;
import com.taobao.luaview.fun.binder.ui.UIRecyclerViewBinder;
import com.taobao.luaview.fun.binder.ui.UIRefreshListViewBinder;
import com.taobao.luaview.fun.binder.ui.UIRefreshRecyclerViewBinder;
import com.taobao.luaview.fun.binder.ui.UITextViewBinder;
import com.taobao.luaview.fun.binder.ui.UIToastBinder;
import com.taobao.luaview.fun.binder.ui.UIViewGroupBinder;
import com.taobao.luaview.fun.binder.ui.UIViewPagerBinder;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.LibFunction;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * LuaView Lib管理等
 *
 * @author song
 * @date 15/8/14
 */
public class LuaViewManager {

    /**
     * load Android API lib
     * TODO 能否做到按需加载，而不是首次进来加载全部binder
     *
     * @param globals
     */
    public static void loadLuaViewLibs(final Globals globals) {
        //ui
        globals.load(new UITextViewBinder());
        globals.load(new UIEditTextBinder());
        globals.load(new UIButtonBinder());
        globals.load(new UIImageViewBinder());
        globals.load(new UIViewGroupBinder());
        globals.load(new UIListViewBinder());
        globals.load(new UIRecyclerViewBinder());
        globals.load(new UIRefreshListViewBinder());
        globals.load(new UIRefreshRecyclerViewBinder());
        globals.load(new UIViewPagerBinder());
        globals.load(new UICustomViewPagerIndicatorBinder());
        globals.load(new UICircleViewPagerIndicatorBinder());
        globals.load(new UIHorizontalScrollViewBinder());
        globals.load(new UIAlertBinder());

        globals.load(new UILoadingViewBinder());
        globals.load(new UILoadingDialogBinder());
        globals.load(new UIToastBinder());
        globals.load(new SpannableStringBinder());

        //animation
        globals.load(new UIAnimatorBinder());

        //net
        globals.load(new HttpBinder());

        //kit
        globals.load(new TimerBinder());
        globals.load(new SystemBinder());
        globals.load(new ActionBarBinder());
        globals.load(new DownloaderBinder());
        globals.load(new UnicodeBinder());
        globals.load(new DataBinder());
        globals.load(new JsonBinder());
        globals.load(new AudioBinder());
        globals.load(new VibratorBinder());

        //常量
        globals.load(new AlignBinder());
        globals.load(new TextAlignBinder());
        globals.load(new FontWeightBinder());
        globals.load(new FontStyleBinder());
        globals.load(new ScaleTypeBinder());
        globals.load(new GravityBinder());
        globals.load(new EllipsizeBinder());
        globals.load(new InterpolatorBinder());
    }

    /**
     * bind lua function
     *
     * @param factory
     * @param methods
     */
    public static LuaTable bind(Class<? extends LibFunction> factory, Method[] methods) {
        if (methods != null) {
            return bind(factory, Arrays.asList(methods));
        }
        return new LuaTable();
    }

    /**
     * bind lua functions
     *
     * @param factory
     * @param methods
     * @return
     */
    public static LuaTable bind(Class<? extends LibFunction> factory, List<Method> methods) {
        LuaTable env = new LuaTable();
        try {
            if (methods != null) {
                for (int i = 0; i < methods.size(); i++) {
                    LibFunction f = factory.newInstance();
                    f.method = methods.get(i);
                    f.name = methods.get(i).getName();
                    env.set(f.name, f);
                }
            }
        } catch (Exception e) {
            throw new LuaError("[Bind Failed] " + e);
        } finally {
            return env;
        }
    }

}
