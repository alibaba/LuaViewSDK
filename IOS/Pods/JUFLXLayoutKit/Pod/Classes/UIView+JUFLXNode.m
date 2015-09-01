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
