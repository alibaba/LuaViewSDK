/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
#import "JUFLXCSSParser.h"
#import "JUFLXNode.h"

static inline BOOL isValidFloatString(NSString *floatString) {
    BOOL valid = NO;
    if (floatString && [floatString isKindOfClass:[NSString class]]) {
        CGFloat fv = [floatString floatValue];
        valid = fv != NSNotFound;
    }
    
    return valid;
}

static inline CGFloat getFloatFromString(NSString *string, CGFloat defaultValue) {
    CGFloat result = defaultValue;
    if (isValidFloatString(string)) {
        result = [string floatValue];
    }

    return result;
}

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

NSString * const kHRCSSjustifyContent = @"justify-content";
NSString * const kHRCSSjustifyContentStart = @"flex-start";
NSString * const kHRCSSjustifyContentCenter = @"center";
NSString * const kHRCSSjustifyContentEnd = @"flex-end";
NSString * const kHRCSSjustifyContentBetween = @"space-between";
NSString * const kHRCSSjustifyContentAround = @"space-around";

NSString * const kHRCSSFlex = @"flex";
NSString * const kHRCSSFlexWrap = @"flex-wrap";

NSString * const kHRCSSDimensionSize = @"size";
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

NSString * const kHRCSSSizeToFit = @"sizetofit";

@implementation JUFLXCSSParser

/**
 *  Stand is @"diretion:row;alignitems:auto;justifyContent:start"
 */
+ (NSDictionary *)transferInlineCSS:(NSString *)inlineCSS {
    if ([inlineCSS hasPrefix:@"{"]) inlineCSS = [inlineCSS substringFromIndex:1];
    if ([inlineCSS hasSuffix:@"}"]) inlineCSS = [inlineCSS substringToIndex:inlineCSS.length - 1];

    
    NSArray *inlineArray = [inlineCSS componentsSeparatedByString:@","];
    if (inlineArray.count == 0) {
        if (inlineCSS.length > 0) {
            NSLog(@"===>JUFLXNode inlineCSS string maybe be wrong, check it %@", inlineCSS);
            return nil;
        }
    };
    
    NSMutableDictionary *result = [NSMutableDictionary dictionary];
    NSCharacterSet *whiteAndNewLine = [NSCharacterSet whitespaceAndNewlineCharacterSet];
    
    for (NSString *kv in inlineArray) {
        NSArray *kvArray = [kv componentsSeparatedByString:@":"];
        if (kvArray.count == 2) {
            NSString *key = kvArray[0];
            NSString *value = kvArray[1];
            if ([key isKindOfClass:[NSString class]]
                && key.length > 0
                && [value isKindOfClass:[NSString class]]
                && value.length > 0) {
                
                key = [[key stringByTrimmingCharactersInSet:whiteAndNewLine] lowercaseString];
                value = [[value stringByTrimmingCharactersInSet:whiteAndNewLine] lowercaseString];
                result[key] = value;
            }
        }
    }
    
    return result;
}

+ (NSSet *)validInlineCssKeys {
    static dispatch_once_t onceToken;
    static NSSet *set;
    dispatch_once(&onceToken, ^{
        set = [NSSet setWithObjects:
               kHRCSSPosition,
               kHRCSSPositionTop,
               kHRCSSPositionLeft,
               kHRCSSPositionBottom,
               kHRCSSPositionRight,
               kHRCSSDirection,
               kHRCSSAlignItems,
               kHRCSSAlignContent,
               kHRCSSjustifyContent,
               kHRCSSFlexWrap,
               kHRCSSAlignSelf,
               kHRCSSFlex,
               kHRCSSDimensionSize,
               kHRCSSDimensionWidth,
               kHRCSSDimensionHeight,
               kHRCSSMinDimensionWidth,
               kHRCSSMinDimensionHeight,
               kHRCSSMaxDimensionWidth,
               kHRCSSMaxDimensionHeight,
               kHRCSSMargin,
               kHRCSSMarginTop,
               kHRCSSMarginLeft,
               kHRCSSMarginBottom,
               kHRCSSMarginRight,
               kHRCSSPadding,
               kHRCSSPaddingTop,
               kHRCSSPaddingLeft,
               kHRCSSPaddingBottom,
               kHRCSSPaddingRight,
               kHRCSSSizeToFit,
               nil];
    });
    return set;

}

+ (NSDictionary *)validDirections {
    static dispatch_once_t onceToken;
    static NSDictionary *dic;
    dispatch_once(&onceToken, ^{
        dic = @{kHRCSSDirectionColumn : @(JUFLXLayoutDirectionColumn),
                kHRCSSDirectionRow : @(JUFLXLayoutDirectionRow),
                kHRCSSDirectionRowReverse : @(JUFLXLayoutDirectionRowReverse),
                kHRCSSDirectionColumnReverse : @(JUFLXLayoutDirectionColumnReverse)};
    });
    return dic;
}

+ (NSDictionary *)validAlignments {
    static dispatch_once_t onceToken;
    static NSDictionary *dic;
    dispatch_once(&onceToken, ^{
        dic = @{kHRCSSAlignAuto : @(JUFLXLayoutAlignmentAuto),
                kHRCSSAlignStart : @(JUFLXLayoutAlignmentStart),
                kHRCSSAlignCenter : @(JUFLXLayoutAlignmentCenter),
                kHRCSSAlignEnd : @(JUFLXLayoutAlignmentEnd),
                kHRCSSAlignStretch : @(JUFLXLayoutAlignmentStretch)};
    });
    return dic;
}

+ (NSDictionary *)validjustifyContents {
    static dispatch_once_t onceToken;
    static NSDictionary *dic;
    dispatch_once(&onceToken, ^{
        dic = @{kHRCSSjustifyContentStart : @(JUFLXLayoutJustifyContentStart),
                kHRCSSjustifyContentCenter : @(JUFLXLayoutJustifyContentCenter),
                kHRCSSjustifyContentEnd : @(JUFLXLayoutJustifyContentEnd),
                kHRCSSjustifyContentBetween : @(JUFLXLayoutJustifyContentBetween),
                kHRCSSjustifyContentAround : @(JUFLXLayoutJustifyContentAround),
                };
    });
    return dic;

}

+ (NSInteger)mappedEnumValueInDictionary:(NSDictionary *)dic withKey:(NSString *)key {
    NSString *value = [dic objectForKey:key];
    if (!value) {
        NSLog(@"%@ isn't a valid key in valid Dic %@", key, dic);
        NSUInteger initialValue = 0;
        if ([key isEqualToString:kHRCSSDirection]) {
            initialValue = JUFLXLayoutDirectionRow;
            
        } else if ([key isEqualToString:kHRCSSAlignItems]) {
            initialValue = JUFLXLayoutAlignmentStretch;
            
        } else if ([key isEqualToString:kHRCSSjustifyContent]) {
            initialValue = JUFLXLayoutJustifyContentStart;
            
        } else if ([key isEqualToString:kHRCSSAlignSelf]) {
            initialValue = JUFLXLayoutAlignmentAuto;
            
        } else if ([key isEqualToString:kHRCSSAlignContent]) {
            initialValue = JUFLXLayoutAlignmentStretch;
        }
            
        return initialValue;
    }
    
    return [value integerValue];
}

+ (void)parseInlineCSS:(NSString *)inlineCSS toNode:(JUFLXNode *)node {
    NSDictionary *inlineDic = [self transferInlineCSS:inlineCSS];

    //filter the invalid key and log
#if DEBUG
    id predicateBlock = ^BOOL(id evaluatedObject, NSDictionary *bindings) {
        return ![[self validInlineCssKeys] containsObject:evaluatedObject];
    };
    NSArray *invalidKeys = [inlineDic.allKeys filteredArrayUsingPredicate:[NSPredicate predicateWithBlock:predicateBlock]];
    if (invalidKeys.count) {
        NSLog(@"<---JUFLXNode---> valid keys %@ doesn't contain these keys %@ \n Please check -> JUFLXNode.h", [self validInlineCssKeys], invalidKeys);
    }
#endif
    
    NSString *position = inlineDic[kHRCSSPosition];
    node.absolute = [position isKindOfClass:[NSString class]] && [position.lowercaseString isEqualToString:@"absolute"];
    CGFloat positionTop = getFloatFromString(inlineDic[kHRCSSPositionTop], node.absoluteEdges.top);
    CGFloat positionLeft = getFloatFromString(inlineDic[kHRCSSPositionLeft], node.absoluteEdges.left);
    CGFloat positionBottom = getFloatFromString(inlineDic[kHRCSSPositionBottom], node.absoluteEdges.bottom);
    CGFloat positionRight = getFloatFromString(inlineDic[kHRCSSPositionRight], node.absoluteEdges.right);
    node.absoluteEdges = UIEdgeInsetsMake(positionTop, positionLeft, positionBottom, positionRight);

    NSString *direction = inlineDic[kHRCSSDirection];
    if (direction) node.direction = [self mappedEnumValueInDictionary:[self validDirections] withKey:direction];
    
    NSString *alignItems = inlineDic[kHRCSSAlignItems];
    if (alignItems) node.alignItems = [self mappedEnumValueInDictionary:[self validAlignments] withKey:alignItems];
    
    NSString *alignSelf = inlineDic[kHRCSSAlignSelf];
    if (alignSelf) node.alignSelf = [self mappedEnumValueInDictionary:[self validAlignments] withKey:alignSelf];
    
    NSString *alignContent = inlineDic[kHRCSSAlignContent];
    if (alignContent) node.alignContent = [self mappedEnumValueInDictionary:[self validAlignments] withKey:alignContent];
    
    NSString *justifyContent = inlineDic[kHRCSSjustifyContent];
    if (justifyContent) node.justifyContent = [self mappedEnumValueInDictionary:[self validjustifyContents] withKey:justifyContent];
    

    node.flex = getFloatFromString(inlineDic[kHRCSSFlex], node.flex);
    
    node.flexWrap = getFloatFromString(inlineDic[kHRCSSFlexWrap], node.flexWrap);
    
    NSString *size = inlineDic[kHRCSSDimensionSize];
    if (isValidFloatString(size)) {
        CGFloat sizef = [size floatValue];
        node.dimensions = CGSizeMake(sizef, sizef);
    }
    
    CGFloat width = getFloatFromString(inlineDic[kHRCSSDimensionWidth], node.dimensions.width);
    CGFloat height = getFloatFromString(inlineDic[kHRCSSDimensionHeight], node.dimensions.height);
    node.dimensions = CGSizeMake(width, height);
    
    CGFloat minWidth = getFloatFromString(inlineDic[kHRCSSMinDimensionWidth], node.minDimensions.width);
    CGFloat minHeight = getFloatFromString(inlineDic[kHRCSSMinDimensionHeight], node.minDimensions.height);
    node.minDimensions = CGSizeMake(minWidth, minHeight);
    
    CGFloat maxWidth = getFloatFromString(inlineDic[kHRCSSMaxDimensionWidth], node.maxDimensions.width);
    CGFloat maxHeight = getFloatFromString(inlineDic[kHRCSSMaxDimensionHeight], node.maxDimensions.height);
    node.maxDimensions = CGSizeMake(maxWidth, maxHeight);

    NSString *margin = inlineDic[kHRCSSMargin];
    if (isValidFloatString(margin)) {
        CGFloat marginf = [margin floatValue];
        node.margin = UIEdgeInsetsMake(marginf, marginf, marginf, marginf);
    }
    
    CGFloat marginTop = getFloatFromString(inlineDic[kHRCSSMarginTop], node.margin.top);
    CGFloat marginLeft = getFloatFromString(inlineDic[kHRCSSMarginLeft], node.margin.left);
    CGFloat marginBottom = getFloatFromString(inlineDic[kHRCSSMarginBottom], node.margin.bottom);
    CGFloat marginRight = getFloatFromString(inlineDic[kHRCSSMarginRight], node.margin.right);
    node.margin = UIEdgeInsetsMake(marginTop, marginLeft, marginBottom, marginRight);
    
    NSString *padding = inlineDic[kHRCSSPadding];
    if (isValidFloatString(padding)) {
        CGFloat paddingf = [padding floatValue];
        node.padding = UIEdgeInsetsMake(paddingf, paddingf, paddingf, paddingf);
    }
    
    CGFloat paddingTop = getFloatFromString(inlineDic[kHRCSSPaddingTop], node.padding.top);
    CGFloat paddingLeft = getFloatFromString(inlineDic[kHRCSSPaddingLeft], node.padding.left);
    CGFloat paddingBottom = getFloatFromString(inlineDic[kHRCSSPaddingBottom], node.padding.bottom);
    CGFloat paddingRight = getFloatFromString(inlineDic[kHRCSSPaddingRight], node.padding.right);
    node.padding = UIEdgeInsetsMake(paddingTop, paddingLeft, paddingBottom, paddingRight);
    
    CGFloat sizeToFit = getFloatFromString(inlineDic[kHRCSSSizeToFit], 0);
    if (sizeToFit > 0) {
        node.sizeToFit = YES;
    }
}


@end
