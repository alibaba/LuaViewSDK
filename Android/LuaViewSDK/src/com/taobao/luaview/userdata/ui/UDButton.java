/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.StateSet;
import android.widget.Button;

import com.taobao.luaview.util.ImageUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Map;

public class UDButton extends UDTextView<Button> {
    private String mNormalImage;
    private String mPressedImage;

    public UDButton(Button view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    /**
     * 设置高亮颜色
     *
     * @param highlightColor
     * @return
     */
    public UDButton setHighlightColor(Integer highlightColor) {
        if (highlightColor != null) {
            final Button btn = getView();
            if (btn != null) {
                btn.setHighlightColor(highlightColor);
            }
        }
        return this;
    }


    /**
     * 获取高亮颜色
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getHighlightColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return getView() != null ? getView().getHighlightColor() : 0;
        } else {
            return 0;
        }
    }

    /**
     * 设置按钮的图片
     *
     * @param normalImage
     * @param pressedImage
     * @return
     */
    public UDButton setImage(final String normalImage, final String pressedImage) {
        this.mNormalImage = normalImage;
        this.mPressedImage = pressedImage;
        final Button btn = getView();
        if (btn != null && getContext() != null) {
            ImageUtil.fetch(getContext(), getLuaResourceFinder(), new String[]{normalImage, pressedImage}, new ImageUtil.LoadCallback() {
                @Override
                public void onLoadResult(Map<String, Drawable> drawables) {
                    if (drawables != null) {
                        final StateListDrawable stateListDrawable = new StateListDrawable();
                        if (drawables.containsKey(pressedImage)) {
                            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, drawables.get(pressedImage));
                        }
                        if (drawables.containsKey(normalImage)) {
                            stateListDrawable.addState(StateSet.WILD_CARD, drawables.get(normalImage));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            btn.setBackground(stateListDrawable);
                        } else {
                            btn.setBackgroundDrawable(stateListDrawable);
                        }
                    }
                }
            });
        }
        return this;
    }

    /**
     * get image
     *
     * @return
     */
    public Varargs getImage() {
        return varargsOf(valueOf(mNormalImage), valueOf(mPressedImage));
    }
}
