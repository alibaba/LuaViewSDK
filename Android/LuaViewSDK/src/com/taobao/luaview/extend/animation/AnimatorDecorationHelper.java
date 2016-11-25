package com.taobao.luaview.extend.animation;

import com.taobao.luaview.extend.animation.attention.BounceAnimatorDecoration;
import com.taobao.luaview.extend.animation.attention.FlashAnimatorDecoration;
import com.taobao.luaview.extend.animation.attention.PulseAnimatorDecoration;
import com.taobao.luaview.extend.animation.attention.RubberBandAnimatorDecoration;
import com.taobao.luaview.extend.animation.attention.ShakeAnimatorDecoration;
import com.taobao.luaview.extend.animation.attention.SwingAnimatorDecoration;
import com.taobao.luaview.extend.animation.attention.TadaAnimatorDecoration;
import com.taobao.luaview.extend.animation.attention.WaveAnimatorDecoration;
import com.taobao.luaview.extend.animation.attention.WobbleAnimatorDecoration;
import com.taobao.luaview.extend.animation.bouncing_entrances.BounceInAnimatorDecoration;
import com.taobao.luaview.extend.animation.bouncing_entrances.BounceInDownAnimatorDecoration;
import com.taobao.luaview.extend.animation.bouncing_entrances.BounceInLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.bouncing_entrances.BounceInRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.bouncing_entrances.BounceInUpAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_entrances.FadeInAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_entrances.FadeInDownAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_entrances.FadeInLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_entrances.FadeInRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_entrances.FadeInUpAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_exits.FadeOutAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_exits.FadeOutDownAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_exits.FadeOutLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_exits.FadeOutRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.fading_exits.FadeOutUpAnimatorDecoration;
import com.taobao.luaview.extend.animation.flippers.FlipInXAnimatorDecoration;
import com.taobao.luaview.extend.animation.flippers.FlipInYAnimatorDecoration;
import com.taobao.luaview.extend.animation.flippers.FlipOutXAnimatorDecoration;
import com.taobao.luaview.extend.animation.flippers.FlipOutYAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_entrances.RotateInAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_entrances.RotateInDownLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_entrances.RotateInDownRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_entrances.RotateInUpLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_entrances.RotateInUpRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_exits.RotateOutAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_exits.RotateOutDownLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_exits.RotateOutDownRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_exits.RotateOutUpLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.rotating_exits.RotateOutUpRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.sliders.SlideInDownAnimatorDecoration;
import com.taobao.luaview.extend.animation.sliders.SlideInLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.sliders.SlideInRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.sliders.SlideInUpAnimatorDecoration;
import com.taobao.luaview.extend.animation.sliders.SlideOutDownAnimatorDecoration;
import com.taobao.luaview.extend.animation.sliders.SlideOutLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.sliders.SlideOutRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.sliders.SlideOutUpAnimatorDecoration;
import com.taobao.luaview.extend.animation.specials.RollInAnimatorDecoration;
import com.taobao.luaview.extend.animation.specials.RollOutAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_entrances.ZoomInAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_entrances.ZoomInDownAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_entrances.ZoomInLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_entrances.ZoomInRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_entrances.ZoomInUpAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_exits.ZoomOutAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_exits.ZoomOutDownAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_exits.ZoomOutLeftAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_exits.ZoomOutRightAnimatorDecoration;
import com.taobao.luaview.extend.animation.zooming_exits.ZoomOutUpAnimatorDecoration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Decoration of Animator
 *
 * @author song
 * @date 16/11/1
 * 主要功能描述
 * 修改描述
 * 下午3:48 song XXX
 */
public class AnimatorDecorationHelper {

    private static final Map<String, Class<? extends BaseViewAnimatorDecoration>> mViewDecorations = new HashMap<String, Class<? extends BaseViewAnimatorDecoration>>();

    static {
        //Attention
        mViewDecorations.put("Attention.Bounce", BounceAnimatorDecoration.class);
        mViewDecorations.put("Attention.Flash", FlashAnimatorDecoration.class);
        mViewDecorations.put("Attention.Pulse", PulseAnimatorDecoration.class);
        mViewDecorations.put("Attention.RubberBand", RubberBandAnimatorDecoration.class);
        mViewDecorations.put("Attention.Shake", ShakeAnimatorDecoration.class);
        mViewDecorations.put("Attention.Swing", SwingAnimatorDecoration.class);
        mViewDecorations.put("Attention.Tada", TadaAnimatorDecoration.class);
        mViewDecorations.put("Attention.Wave", WaveAnimatorDecoration.class);
        mViewDecorations.put("Attention.Wobble", WobbleAnimatorDecoration.class);

        //Bounce
        mViewDecorations.put("Bounce.In", BounceInAnimatorDecoration.class);
        mViewDecorations.put("Bounce.In.Down", BounceInDownAnimatorDecoration.class);
        mViewDecorations.put("Bounce.In.Left", BounceInLeftAnimatorDecoration.class);
        mViewDecorations.put("Bounce.In.Right", BounceInRightAnimatorDecoration.class);
        mViewDecorations.put("Bounce.In.Up", BounceInUpAnimatorDecoration.class);


        //Fade in
        mViewDecorations.put("Fade.In", FadeInAnimatorDecoration.class);
        mViewDecorations.put("Fade.In.Down", FadeInDownAnimatorDecoration.class);
        mViewDecorations.put("Fade.In.Left", FadeInLeftAnimatorDecoration.class);
        mViewDecorations.put("Fade.In.Right", FadeInRightAnimatorDecoration.class);
        mViewDecorations.put("Fade.In.Up", FadeInUpAnimatorDecoration.class);

        //Fade out
        mViewDecorations.put("Fade.Out", FadeOutAnimatorDecoration.class);
        mViewDecorations.put("Fade.Out.Down", FadeOutDownAnimatorDecoration.class);
        mViewDecorations.put("Fade.Out.Left", FadeOutLeftAnimatorDecoration.class);
        mViewDecorations.put("Fade.Out.Right", FadeOutRightAnimatorDecoration.class);
        mViewDecorations.put("Fade.Out.Up", FadeOutUpAnimatorDecoration.class);

        //Flip
        mViewDecorations.put("Flip.In.X", FlipInXAnimatorDecoration.class);
        mViewDecorations.put("Flip.In.Y", FlipInYAnimatorDecoration.class);
        mViewDecorations.put("Flip.Out.X", FlipOutXAnimatorDecoration.class);
        mViewDecorations.put("Flip.Out.Y", FlipOutYAnimatorDecoration.class);

        //rotate in
        mViewDecorations.put("Rotate.In", RotateInAnimatorDecoration.class);
        mViewDecorations.put("Rotate.In.DownLeft", RotateInDownLeftAnimatorDecoration.class);
        mViewDecorations.put("Rotate.In.DownRight", RotateInDownRightAnimatorDecoration.class);
        mViewDecorations.put("Rotate.In.UpLeft", RotateInUpLeftAnimatorDecoration.class);
        mViewDecorations.put("Rotate.In.UpRight", RotateInUpRightAnimatorDecoration.class);

        //rotate out
        mViewDecorations.put("Rotate.Out", RotateOutAnimatorDecoration.class);
        mViewDecorations.put("Rotate.Out.DownLeft", RotateOutDownLeftAnimatorDecoration.class);
        mViewDecorations.put("Rotate.Out.DownRight", RotateOutDownRightAnimatorDecoration.class);
        mViewDecorations.put("Rotate.Out.UpLeft", RotateOutUpLeftAnimatorDecoration.class);
        mViewDecorations.put("Rotate.Out.UpRight", RotateOutUpRightAnimatorDecoration.class);

        //slide in
        mViewDecorations.put("Slide.In.Down", SlideInDownAnimatorDecoration.class);
        mViewDecorations.put("Slide.In.Left", SlideInLeftAnimatorDecoration.class);
        mViewDecorations.put("Slide.In.Right", SlideInRightAnimatorDecoration.class);
        mViewDecorations.put("Slide.In.Up", SlideInUpAnimatorDecoration.class);

        //slide out
        mViewDecorations.put("Slide.Out.Down", SlideOutDownAnimatorDecoration.class);
        mViewDecorations.put("Slide.Out.Left", SlideOutLeftAnimatorDecoration.class);
        mViewDecorations.put("Slide.Out.Right", SlideOutRightAnimatorDecoration.class);
        mViewDecorations.put("Slide.Out.Up", SlideOutUpAnimatorDecoration.class);

        //roll
        mViewDecorations.put("Roll.In", RollInAnimatorDecoration.class);
        mViewDecorations.put("Roll.Out", RollOutAnimatorDecoration.class);

        //Zoom in
        mViewDecorations.put("Zoom.In", ZoomInAnimatorDecoration.class);
        mViewDecorations.put("Zoom.In.Down", ZoomInDownAnimatorDecoration.class);
        mViewDecorations.put("Zoom.In.Left", ZoomInLeftAnimatorDecoration.class);
        mViewDecorations.put("Zoom.In.Right", ZoomInRightAnimatorDecoration.class);
        mViewDecorations.put("Zoom.In.Up", ZoomInUpAnimatorDecoration.class);

        //Zoom out
        mViewDecorations.put("Zoom.Out", ZoomOutAnimatorDecoration.class);
        mViewDecorations.put("Zoom.Out.Down", ZoomOutDownAnimatorDecoration.class);
        mViewDecorations.put("Zoom.Out.Left", ZoomOutLeftAnimatorDecoration.class);
        mViewDecorations.put("Zoom.Out.Right", ZoomOutRightAnimatorDecoration.class);
        mViewDecorations.put("Zoom.Out.Up", ZoomOutUpAnimatorDecoration.class);
    }

    /**
     * create a decoration
     *
     * @param name
     * @return
     */
    public static BaseViewAnimatorDecoration createDecoration(String name) {
        if (mViewDecorations != null && mViewDecorations.containsKey(name)) {
            Class clazz = mViewDecorations.get(name);
            if (clazz != null) {
                try {
                    Constructor<BaseViewAnimatorDecoration> constructor = clazz.getConstructor();
                    if (constructor != null) {
                        return constructor.newInstance();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
