/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>

@class JUFLXNode;
@interface JUFLXCSSParser : NSObject

+ (void)parseInlineCSS:(NSString *)inlineCSS toNode:(JUFLXNode *)node;

@end


/*
 * ---------------------------------------------------------------------------
 * The valid css value
 * ---------------------------------------------------------------------------
 */
/*
NSString * const kHRCSSPosition = @"position";
NSString * const kHRCSSPositionAbsolute = @"absolute";
NSString * const kHRCSSPositionTop = @"top";
NSString * const kHRCSSPositionLeft = @"left";
NSString * const kHRCSSPositionBottom = @"bottom";
NSString * const kHRCSSPositionRight = @"right";

NSString * const kHRCSSDirection = @"flex-direction";
NSString * const kHRCSSDirectionRow = @"row";
NSString * const kHRCSSDirectionRowReverse = @"row-reverse";
NSString * const kHRCSSDirectionColumn = @"column";
NSString * const kHRCSSDirectionColumnReverse = @"column-reverse";

NSString * const kHRCSSAlignItems = @"align-items";
NSString * const kHRCSSAlignContent = @"align-content";
NSString * const kHRCSSAlignSelf = @"align-self";
NSString * const kHRCSSAlignAuto = @"auto";
NSString * const kHRCSSAlignStart = @"flex-start";
NSString * const kHRCSSAlignCenter = @"center";
NSString * const kHRCSSAlignEnd = @"flex-end";
NSString * const kHRCSSAlignStretch = @"stretch";

NSString * const kHRCSSJustification = @"justify-content";
NSString * const kHRCSSJustificationStart = @"flex-start";
NSString * const kHRCSSJustificationCenter = @"center";
NSString * const kHRCSSJustificationEnd = @"flex-end";
NSString * const kHRCSSJustificationBetween = @"space-between";
NSString * const kHRCSSJustificationAround = @"space-around";

NSString * const kHRCSSFlex = @"flex";
NSString * const kHRCSSFlexWrap = @"flex-wrap";

NSString * const kHRCSSDimensionWidth = @"width";
NSString * const kHRCSSDimensionHeight = @"height";

NSString * const kHRCSSMinDimensionWidth = @"min-width";
NSString * const kHRCSSMinDimensionHeight = @"min-height";

NSString * const kHRCSSMaxDimensionWidth = @"max-width";
NSString * const kHRCSSMaxDimensionHeight = @"max-height";

NSString * const kHRCSSMargin = @"margin";
NSString * const kHRCSSMarginLeft = @"margin-left";
NSString * const kHRCSSMarginTop = @"margin-top";
NSString * const kHRCSSMarginBottom = @"margin-bottom";
NSString * const kHRCSSMarginRight = @"margin-right";

NSString * const kHRCSSPadding = @"padding";
NSString * const kHRCSSPaddingLeft = @"padding-left";
NSString * const kHRCSSPaddingTop = @"padding-top";
NSString * const kHRCSSPaddingBottom = @"padding-bottom";
NSString * const kHRCSSPaddingRight = @"padding-right";
*/
