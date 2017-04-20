/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.util.Log;

import com.facebook.csslayout.CSSAlign;
import com.facebook.csslayout.CSSFlexDirection;
import com.facebook.csslayout.CSSJustify;
import com.facebook.csslayout.CSSNode;
import com.facebook.csslayout.CSSPositionType;
import com.facebook.csslayout.CSSWrap;
import com.facebook.csslayout.Spacing;
import com.taobao.luaview.util.DimenUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiekaiwei on 16/3/17.
 */
public class FlexboxCSSParser {
    private static final String TAG = "FlexboxCSSParser";
    private static final String POSITION = "position";
    private static final String ABSOLUTE = "absolute";
    private static final String TOP = "top";
    private static final String LEFT = "left";
    private static final String BOTTOM = "bottom";
    private static final String RIGHT = "right";
    private static final String FLEXDEIRECTION = "flex-direction";
    private static final String FLEXDIRECTIONROW = "row";
    private static final String FLEXDIRECTIONROWREVERSE = "row-reverse";
    private static final String FLEXDIRECTIONCOLUMN = "column";
    private static final String FLEXDIRECTIONCOLUMNREVERSE = "column-reverse";
    private static final String ALIGNITEMS = "align-items";
    private static final String ALIGNCONTENT = "align-content";
    private static final String ALIGNSELF = "align-self";
    private static final String ALIGNAUTO = "auto";
    private static final String ALIGNSTART = "flex-start";
    private static final String ALIGNCENTER = "center";
    private static final String ALIGNEND = "flex-end";
    private static final String ALIGNSTRETCH = "stretch";
    private static final String JUSTIFYCONTENT = "justify-content";
    private static final String JUSTIFYCONTENTSTART = "flex-start";
    private static final String JUSTIFYCONTENTCENTER = "center";
    private static final String JUSTIFYCONTENTEND = "flex-end";
    private static final String JUSTIFYCONTENTBETWEEN = "space-between";
    private static final String JUSTIFYCONTENTAROUND = "space-around";
    private static final String FLEX = "flex";
    private static final String FLEXWRAP = "flex-wrap";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String MINWIDTH = "min-width";
    private static final String MINHEIGHT = "min-height";
    private static final String MAXWIDTH = "max-height";
    private static final String MAXHEIGHT = "max-height";
    private static final String MARGIN = "margin";
    private static final String MARGINLEFT = "margin-left";
    private static final String MARGINTOP = "margin-top";
    private static final String MARGINBOTTOM = "margin-bottom";
    private static final String MARGINRIGHT = "margin-right";
    private static final String PADDING = "padding";
    private static final String PADDINGLEFT = "padding-left";
    private static final String PADDINGTOP = "padding-top";
    private static final String PADDINGBOTTOM = "padding-bottom";
    private static final String PADDINGRIGHT = "padding-right";
    private static final String SIZETOFIT = "sizetofit";

    private static Map<String, String> mInlineMap;
    private static String mCssString;
    private static CSSNode mNode;

    public static void parseFlexNodeCSS(CSSNode node, String cssString) {
        if (mCssString == cssString || mNode == node) {
            return;
        }

        mCssString = cssString;
        mNode = node;
        resetInlineMap();

        setPosition();
        setDirection();
        setAlignItems();
        setAlignSelf();
        setAlignContent();
        setJustfiyContent();
        setFlex();
        setFlexWrap();
        setSize();
        setMinSize();
        setMaxSize();
        setMargin();
        setPadding();
        setSizeToFit();
    }

    private static float pixelFloat(String num) {
        float fnum = Float.parseFloat(num);
        return DimenUtil.dpiToPx(fnum);
    }

    private static void setPosition() {
        String position = mInlineMap.get(POSITION);
        Log.d(TAG, "setPosition: with postion" + position);
        if (position != null && position.equals(ABSOLUTE)) {
            mNode.setPositionType(CSSPositionType.ABSOLUTE);

            String top = mInlineMap.get(TOP);
            if (top != null) {
                mNode.setPositionTop(pixelFloat(top));
            }

            String left = mInlineMap.get(LEFT);
            if (left != null) {
                mNode.setPositionLeft(pixelFloat(left));
            }

            String bottom = mInlineMap.get(BOTTOM);
            if (bottom != null) {
                mNode.setPositionBottom(pixelFloat(bottom));
            }

            String right = mInlineMap.get(RIGHT);
            if (right != null) {
                mNode.setPositionRight(pixelFloat(right));
            }
        }
    }

    private static void setDirection() {
        String direction = mInlineMap.get(FLEXDEIRECTION);
        if (direction != null) {
            CSSFlexDirection dir = directionMap.get(direction);
            if (dir != null) {
                mNode.setFlexDirection(dir);
            }
        }
    }

    private static void setAlignItems() {
        String alignItems = mInlineMap.get(ALIGNITEMS);
        if (alignItems != null) {
            CSSAlign align = alignMap.get(alignItems);
            if (align != null) {
                mNode.setAlignItems(align);
            }
        }
    }

    private static void setAlignSelf() {
        String alignSelf = mInlineMap.get(ALIGNSELF);
        if (alignSelf != null) {
            CSSAlign align = alignMap.get(alignSelf);
            if (align != null) {
                mNode.setAlignSelf(align);
            }
        }
    }

    private static void setAlignContent() {
        String alignContent = mInlineMap.get(ALIGNCONTENT);
        if (alignContent != null) {
            CSSAlign align = alignMap.get(alignContent);
            if (align != null) {
                mNode.setAlignContent(align);
            }
        }
    }

    private static void setJustfiyContent() {
        String justifyContent = mInlineMap.get(JUSTIFYCONTENT);
        if (justifyContent != null) {
            CSSJustify juc = justifyMap.get(justifyContent);
            if (juc != null) {
                mNode.setJustifyContent(juc);
            }
        }
    }

    private static void setFlex() {
        String flex = mInlineMap.get(FLEX);
        if (flex != null) {
            mNode.setFlex(Float.parseFloat(flex));
        }
    }

    private static void setFlexWrap() {
        String flexWrap = mInlineMap.get(FLEXWRAP);
        if (flexWrap != null) {
            CSSWrap ifWrap = Integer.parseInt(flexWrap) > 0 ? CSSWrap.WRAP : CSSWrap.NOWRAP;
            mNode.setWrap(ifWrap);
        }
    }

    private static void setSize() {
        String width = mInlineMap.get(WIDTH);
        if (width != null) {
            mNode.setStyleWidth(pixelFloat(width));
        }

        String height = mInlineMap.get(HEIGHT);
        if (height != null) {
            mNode.setStyleHeight(pixelFloat(height));
        }
    }

    private static void setMinSize() {
        String width = mInlineMap.get(MINWIDTH);
        if (width != null) {
            mNode.setStyleMinWidth(pixelFloat(width));
        }

        String height = mInlineMap.get(MINHEIGHT);
        if (height != null) {
            mNode.setStyleMinWidth(pixelFloat(height));
        }
    }

    private static void setMaxSize() {
        String width = mInlineMap.get(MAXWIDTH);
        if (width != null) {
            mNode.setStyleMaxWidth(pixelFloat(width));
        }

        String height = mInlineMap.get(MAXHEIGHT);
        if (height != null) {
            mNode.setStyleMaxHeight(pixelFloat(height));
        }
    }

    private static void setMargin() {
        String margin = mInlineMap.get(MARGIN);
        if (margin != null) {
            Float fMargin = pixelFloat(margin);
            mNode.setMargin(Spacing.LEFT, fMargin);
            mNode.setMargin(Spacing.TOP, fMargin);
            mNode.setMargin(Spacing.BOTTOM, fMargin);
            mNode.setMargin(Spacing.RIGHT, fMargin);
        }

        String marginLeft = mInlineMap.get(MARGINLEFT);
        if (marginLeft != null) {
            mNode.setMargin(Spacing.LEFT, pixelFloat(marginLeft));
        }

        String marginTop = mInlineMap.get(MARGINTOP);
        if (marginTop != null) {
            mNode.setMargin(Spacing.TOP, pixelFloat(marginTop));
        }

        String marginBottom = mInlineMap.get(MARGINBOTTOM);
        if (marginBottom != null) {
            mNode.setMargin(Spacing.BOTTOM, pixelFloat(marginBottom));
        }

        String marginRight = mInlineMap.get(MARGINRIGHT);
        if (marginRight != null) {
            mNode.setMargin(Spacing.RIGHT, pixelFloat(marginRight));
        }
    }

    private static void setPadding() {
        String padding = mInlineMap.get(PADDING);
        if (padding != null) {
            Float mPadding = pixelFloat(padding);
            mNode.setPadding(Spacing.LEFT, mPadding);
            mNode.setPadding(Spacing.TOP, mPadding);
            mNode.setPadding(Spacing.BOTTOM, mPadding);
            mNode.setPadding(Spacing.RIGHT, mPadding);
        }

        String paddingLeft = mInlineMap.get(PADDINGLEFT);
        if (paddingLeft != null) {
            mNode.setPadding(Spacing.LEFT, pixelFloat(paddingLeft));
        }

        String paddingTop = mInlineMap.get(PADDINGTOP);
        if (paddingTop != null) {
            mNode.setPadding(Spacing.TOP, pixelFloat(paddingTop));
        }

        String paddingBottom = mInlineMap.get(PADDINGBOTTOM);
        if (paddingBottom != null) {
            mNode.setPadding(Spacing.BOTTOM, pixelFloat(paddingBottom));
        }

        String paddingRight = mInlineMap.get(PADDINGRIGHT);
        if (paddingRight != null) {
            mNode.setPadding(Spacing.RIGHT, pixelFloat(paddingRight));
        }
    }

    private static void setSizeToFit() {
        String fit = mInlineMap.get(SIZETOFIT);
        if (fit != null) {
            mNode.setSizeToFit(Integer.parseInt(fit) > 0);
        }
    }

    private static Map<String, String> resetInlineMap() {
        List<String> items = Arrays.asList(mCssString.split("\\s*,\\s*"));
        Map<String, String> resultMap = new HashMap<String, String>();

        for (int i = 0; i < items.size(); i++) {
            String kv = items.get(i);
            try {
                List<String> keyAndValue = Arrays.asList(kv.split("\\s*:\\s*"));
                String key = keyAndValue.get(0);
                if (!validCssKeys.contains(key)) {
                    Log.d(TAG, "getInlineMap: with un correct key: " + key + " check in" + validCssKeys);
                } else {
                    resultMap.put(keyAndValue.get(0), keyAndValue.get(1));
                }
            } catch (Exception e) {
                Log.e(TAG, "getInlineMap: wrong", e);
                continue;
            }
        }

        Log.d(TAG, "getInlineMap: " + items + "resultMap: " + resultMap);
        mInlineMap = resultMap;

        return mInlineMap;
    }

    private static final Set<String> validCssKeys;

    static {
        validCssKeys = new HashSet<String>(Arrays.asList(
                POSITION,
                TOP,
                LEFT,
                RIGHT,
                BOTTOM,
                FLEXDEIRECTION,
                ALIGNITEMS,
                ALIGNCONTENT,
                ALIGNSELF,
                JUSTIFYCONTENT,
                FLEX,
                FLEXWRAP,
                WIDTH,
                HEIGHT,
                MINWIDTH,
                MINHEIGHT,
                MAXWIDTH,
                MAXHEIGHT,
                MARGIN,
                MARGINLEFT,
                MARGINTOP,
                MARGINBOTTOM,
                MARGINRIGHT,
                PADDING,
                PADDINGLEFT,
                PADDINGTOP,
                PADDINGBOTTOM,
                PADDINGRIGHT,
                SIZETOFIT
        ));
    }

    private static final Map<String, CSSFlexDirection> directionMap;

    static {
        directionMap = new HashMap<String, CSSFlexDirection>();
        directionMap.put(FLEXDIRECTIONCOLUMN, CSSFlexDirection.COLUMN);
        directionMap.put(FLEXDIRECTIONROW, CSSFlexDirection.ROW);
        directionMap.put(FLEXDIRECTIONCOLUMNREVERSE, CSSFlexDirection.COLUMN_REVERSE);
        directionMap.put(FLEXDIRECTIONROWREVERSE, CSSFlexDirection.ROW_REVERSE);
    }

    private static final Map<String, CSSAlign> alignMap;

    static {
        alignMap = new HashMap<String, CSSAlign>();
        alignMap.put(ALIGNAUTO, CSSAlign.AUTO);
        alignMap.put(ALIGNCENTER, CSSAlign.CENTER);
        alignMap.put(ALIGNSTART, CSSAlign.FLEX_START);
        alignMap.put(ALIGNEND, CSSAlign.FLEX_END);
        alignMap.put(ALIGNSTRETCH, CSSAlign.STRETCH);
    }

    private static final Map<String, CSSJustify> justifyMap;

    static {
        justifyMap = new HashMap<String, CSSJustify>();
        justifyMap.put(JUSTIFYCONTENTAROUND, CSSJustify.SPACE_AROUND);
        justifyMap.put(JUSTIFYCONTENTBETWEEN, CSSJustify.SPACE_BETWEEN);
        justifyMap.put(JUSTIFYCONTENTCENTER, CSSJustify.CENTER);
        justifyMap.put(JUSTIFYCONTENTSTART, CSSJustify.FLEX_START);
        justifyMap.put(JUSTIFYCONTENTEND, CSSJustify.FLEX_END);
    }
}
