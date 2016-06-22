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
import com.taobao.luaview.util.DebugUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;

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
     * 创建Globals
     * 根据是否用lua-to-java bytecode来处理（如果使用LuaJC的话则会使用库bcel 533k)
     *
     * @return
     */
    public static Globals createGlobals() {
        final Globals globals = LuaViewConfig.isOpenDebugger() ? JsePlatform.debugGlobals() : JsePlatform.standardGlobals();//加载系统libs
        if (LuaViewConfig.isIsUseLuaJC()) {
            LuaJC.install(globals);
        }
        loadLuaViewLibs(globals);//加载用户lib
        return globals;
    }

    /**
     * tryLazyLoad Android API lib
     * TODO 能否做到按需加载，而不是首次进来加载全部binder
     *
     * @param globals
     */
    public static void loadLuaViewLibs(final Globals globals) {
        DebugUtil.ts("loadLuaViewLibs");
        //ui
        globals.tryLazyLoad(new UITextViewBinder());
        globals.tryLazyLoad(new UIEditTextBinder());
        globals.tryLazyLoad(new UIButtonBinder());
        globals.tryLazyLoad(new UIImageViewBinder());
        globals.tryLazyLoad(new UIViewGroupBinder());
        globals.tryLazyLoad(new UIListViewBinder());
        globals.tryLazyLoad(new UIRecyclerViewBinder());
        globals.tryLazyLoad(new UIRefreshListViewBinder());
        globals.tryLazyLoad(new UIRefreshRecyclerViewBinder());
        globals.tryLazyLoad(new UIViewPagerBinder());
        globals.tryLazyLoad(new UICustomViewPagerIndicatorBinder());
        globals.tryLazyLoad(new UICircleViewPagerIndicatorBinder());
        globals.tryLazyLoad(new UIHorizontalScrollViewBinder());
        globals.tryLazyLoad(new UIAlertBinder());

        globals.tryLazyLoad(new UILoadingViewBinder());
        globals.tryLazyLoad(new UILoadingDialogBinder());
        globals.tryLazyLoad(new UIToastBinder());
        globals.tryLazyLoad(new SpannableStringBinder());

        //animation
        globals.tryLazyLoad(new UIAnimatorBinder());

        //net
        globals.tryLazyLoad(new HttpBinder());

        //kit
        globals.tryLazyLoad(new TimerBinder());
        globals.tryLazyLoad(new SystemBinder());
        globals.tryLazyLoad(new ActionBarBinder());
        globals.tryLazyLoad(new DownloaderBinder());
        globals.tryLazyLoad(new UnicodeBinder());
        globals.tryLazyLoad(new DataBinder());
        globals.tryLazyLoad(new JsonBinder());
        globals.tryLazyLoad(new AudioBinder());
        globals.tryLazyLoad(new VibratorBinder());

        //常量
        globals.tryLazyLoad(new AlignBinder());
        globals.tryLazyLoad(new TextAlignBinder());
        globals.tryLazyLoad(new FontWeightBinder());
        globals.tryLazyLoad(new FontStyleBinder());
        globals.tryLazyLoad(new ScaleTypeBinder());
        globals.tryLazyLoad(new GravityBinder());
        globals.tryLazyLoad(new EllipsizeBinder());
        globals.tryLazyLoad(new InterpolatorBinder());

        DebugUtil.te("loadLuaViewLibs");
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
