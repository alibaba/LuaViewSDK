//
//  UIView+JUFLXNode.m
//  all_layouts
//
//  Created by xiekw on 15/7/8.
//  Copyright (c) 2015年 xiekw. All rights reserved.
//

#import "UIView+JUFLXNode.h"
#import <objc/runtime.h>
#import "JUFLXNode.h"

NSString * const JUFLXNodeUIViewNodeKey = @"hrnode.JUFLXNodeUIViewNodeKey";
NSString * const JUFLXNodeUIViewDidFinishBlockKey = @"hrnode.JUFLXNodeUIViewDidFinishBlockKey";

@implementation UIView (JUFLXNodeGetter)

- (JUFLXNode *)ju_flxNode {
    JUFLXNode *node = objc_getAssociatedObject(self, &JUFLXNodeUIViewNodeKey);
    if (!node) {
        node = [JUFLXNode nodeWithView:self];
    }
    return node;
}

- (BOOL)ju_hasFlxNode {
    JUFLXNode *node = objc_getAssociatedObject(self, &JUFLXNodeUIViewNodeKey);
    return node != nil;
}

- (void)setJu_flxNodeDidFinishLayoutBlock:(NodeDidFinishLayout)block {
    objc_setAssociatedObject(self, &JUFLXNodeUIViewDidFinishBlockKey, block, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

@end

@implementation UIView (JUFLXNodes)

- (NSArray *)flx_childViews {
    return [self.ju_flxNode.childNodes valueForKey:@"view"];
}

- (void)setFlx_childViews:(NSArray *)flx_childViews {
    self.ju_flxNode.childNodes = [flx_childViews valueForKey:@"ju_flxNode"];
}

- (UIEdgeInsets)flx_absoluteEdgeInsets {
    return self.ju_flxNode.absoluteEdges;
}

- (void)setFlx_absoluteEdgeInsets:(UIEdgeInsets)flx_absoluteEdgeInsets {
    self.ju_flxNode.absoluteEdges = flx_absoluteEdgeInsets;
}

- (BOOL)flx_absolute {
    return self.ju_flxNode.absolute;
}

- (void)setFlx_absolute:(BOOL)flx_absolute {
    self.ju_flxNode.absolute = flx_absolute;
}

- (JUFLXLayoutDirection)flx_direction {
    return self.ju_flxNode.direction;
}

- (void)setFlx_direction:(JUFLXLayoutDirection)flx_direction {
    self.ju_flxNode.direction = flx_direction;
}

- (JUFLXLayoutAlignment)flx_alignItems {
    return self.ju_flxNode.alignItems;
}

- (void)setFlx_alignItems:(JUFLXLayoutAlignment)flx_alignItems {
    self.ju_flxNode.alignItems = flx_alignItems;
}

- (JUFLXLayoutAlignment)flx_alignContent {
    return self.ju_flxNode.alignContent;
}

- (void)setFlx_alignContent:(JUFLXLayoutAlignment)flx_alignContent {
    self.ju_flxNode.alignContent = flx_alignContent;
}

- (JUFLXLayoutJustifyContent)flx_justifyContent {
    return self.ju_flxNode.justifyContent;
}

- (void)setFlx_justifyContent:(JUFLXLayoutJustifyContent)flx_justifyContent {
    self.ju_flxNode.justifyContent = flx_justifyContent;
}

- (BOOL)flx_flexWrap {
    return self.ju_flxNode.flexWrap;
}

- (void)setFlx_flexWrap:(BOOL)flx_flexWrap {
    self.ju_flxNode.flexWrap = flx_flexWrap;
}

- (JUFLXLayoutAlignment)flx_alignSelf {
    return self.ju_flxNode.alignSelf;
}

- (void)setFlx_alignSelf:(JUFLXLayoutAlignment)flx_alignSelf {
    self.ju_flxNode.alignSelf = flx_alignSelf;
}

- (CGFloat)flx_flex {
    return self.ju_flxNode.flex;
}

- (void)setFlx_flex:(CGFloat)flx_flex {
    self.ju_flxNode.flex = flx_flex;
}

- (CGSize)flx_dimensions {
    return self.ju_flxNode.dimensions;
}

-(void)setFlx_dimensions:(CGSize)flx_dimensions {
    self.ju_flxNode.dimensions = flx_dimensions;
}

- (CGSize)flx_minDimensions {
    return self.ju_flxNode.minDimensions;
}

- (void)setFlx_minDimensions:(CGSize)flx_minDimensions {
    self.ju_flxNode.minDimensions = flx_minDimensions;
}

- (CGSize)flx_maxDimensions {
    return self.ju_flxNode.maxDimensions;
}

- (void)setFlx_maxDimensions:(CGSize)flx_maxDimensions {
    self.ju_flxNode.maxDimensions = flx_maxDimensions;
}

- (UIEdgeInsets)flx_margin {
    return self.ju_flxNode.margin;
}

- (void)setFlx_margin:(UIEdgeInsets)flx_margin {
    self.ju_flxNode.margin = flx_margin;
}

- (UIEdgeInsets)flx_padding {
    return self.ju_flxNode.padding;
}

- (void)setFlx_padding:(UIEdgeInsets)flx_padding {
    self.ju_flxNode.padding = flx_padding;
}

- (void)setFlx_measure:(CGSize (^)(CGFloat))flx_measure {
    self.ju_flxNode.measure = flx_measure;
}

- (CGSize (^)(CGFloat))flx_measure {
    return self.ju_flxNode.measure;
}

- (BOOL)flx_sizeToFit {
    return self.ju_flxNode.isSizeToFit;
}

- (void)setFlx_sizeToFit:(BOOL)flx_sizeToFit {
    self.ju_flxNode.sizeToFit = flx_sizeToFit;
}

- (void)flx_bindingInlineCSS:(NSString *)inlineCSS {
    [self.ju_flxNode bindingInlineCSS:inlineCSS];
}

- (void)flx_layout {
    [self.ju_flxNode layout];
}

- (void)flx_layoutAsync:(BOOL)async {
    [self.ju_flxNode layoutAsync:async];
}

- (void)flx_layoutAsync:(BOOL)async completion:(void(^)(CGRect frame))block {
    [self.ju_flxNode layoutAsync:async completionBlock:block];
}

@end